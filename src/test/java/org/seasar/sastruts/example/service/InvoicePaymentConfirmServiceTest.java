package org.seasar.sastruts.example.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.PostBindFields;
import org.seasar.framework.unit.annotation.TxBehavior;
import org.seasar.framework.unit.annotation.TxBehaviorType;
import org.seasar.sastruts.example.cache.InvoiceCache;
import org.seasar.sastruts.example.dao.DbApprovalHistoryDao;
import org.seasar.sastruts.example.dao.DbCustomerDao;
import org.seasar.sastruts.example.dao.DbDepartmentDao;
import org.seasar.sastruts.example.dao.DbScenarioInvoiceDao;
import org.seasar.sastruts.example.entity.DbApprovalHistory;
import org.seasar.sastruts.example.entity.DbScenarioInvoice;
import org.seasar.sastruts.example.testsupport.DbInvoiceScenario;
import org.seasar.sastruts.example.testsupport.DbInvoiceScenarioFixture;
import org.seasar.sastruts.example.testsupport.SqlTestSupport;

/**
 * InvoicePaymentConfirmServiceの支払確定処理を検証するS2JUnit4テスト。
 * Service入口からLogic、Daoを跨ぐ処理をH2インメモリDBで確認する。
 */
@RunWith(Seasar2.class)
public class InvoicePaymentConfirmServiceTest {

    public JdbcManager jdbcManager;

    public InvoicePaymentConfirmService invoicePaymentConfirmService;

    public InvoiceCachedReferenceService invoiceCachedReferenceService;

    public InvoiceCache invoiceCache;

    public DbCustomerDao dbCustomerDao;

    public DbDepartmentDao dbDepartmentDao;

    public DbScenarioInvoiceDao dbScenarioInvoiceDao;

    public DbApprovalHistoryDao dbApprovalHistoryDao;

    private DbInvoiceScenarioFixture dbInvoiceScenarioFixture;

    private SqlTestSupport sqlTestSupport;

    @PostBindFields
    public void setUp() {
        assertNotNull(jdbcManager);
        assertNotNull(invoicePaymentConfirmService);
        assertNotNull(invoicePaymentConfirmService.invoiceCache);
        assertNotNull(invoicePaymentConfirmService.invoicePaymentConfirmValidationLogic);
        assertNotNull(invoicePaymentConfirmService.invoicePaymentHistoryLogic);
        assertNotNull(invoicePaymentConfirmService.dbScenarioInvoiceDao);
        assertNotNull(invoicePaymentConfirmService.dbApprovalHistoryDao);
        assertNotNull(invoiceCachedReferenceService);
        assertNotNull(invoiceCache);
        assertNotNull(dbCustomerDao);
        assertNotNull(dbDepartmentDao);
        assertNotNull(dbScenarioInvoiceDao);
        assertNotNull(dbApprovalHistoryDao);

        invoiceCache.clear();
        sqlTestSupport = new SqlTestSupport(jdbcManager);
        dropScenarioTables();
        sqlTestSupport.executeSqlFile("sql/db_invoice_scenario_schema.sql");
        dbInvoiceScenarioFixture = new DbInvoiceScenarioFixture(
                dbCustomerDao,
                dbDepartmentDao,
                dbScenarioInvoiceDao,
                dbApprovalHistoryDao);
    }

    // 承認済み請求書を支払確定でき、ステータスがPAYMENT_CONFIRMEDになることを確認する。
    @Test
    public void testConfirmPaymentApprovedInvoice() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createApprovedInvoiceScenario();

        DbScenarioInvoice paymentConfirmed = invoicePaymentConfirmService.confirmPayment(
                scenario.getInvoice().getId());

        assertEquals("PAYMENT_CONFIRMED", paymentConfirmed.getStatus());
        assertEquals("PAYMENT_CONFIRMED",
                dbScenarioInvoiceDao.findById(scenario.getInvoice().getId()).getStatus());
    }

    // 支払確定が成功した後、古い請求書キャッシュがevictされ、再参照でDB最新状態を取得できることを確認する。
    @Test
    public void testConfirmPaymentEvictsInvoiceCache() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createApprovedInvoiceScenario();
        Long invoiceId = scenario.getInvoice().getId();

        DbScenarioInvoice cached = invoiceCachedReferenceService.findById(invoiceId);
        assertEquals("APPROVED", cached.getStatus());
        assertTrue(invoiceCache.contains(invoiceId));

        invoicePaymentConfirmService.confirmPayment(invoiceId);

        assertFalse(invoiceCache.contains(invoiceId));
        DbScenarioInvoice reloaded = invoiceCachedReferenceService.findById(invoiceId);
        assertEquals("PAYMENT_CONFIRMED", reloaded.getStatus());
        assertTrue(invoiceCache.contains(invoiceId));
    }

    // 支払確定後、対象請求書IDに紐づくPAYMENT_CONFIRMED履歴が追加されることを確認する。
    @Test
    public void testConfirmPaymentInsertsPaymentConfirmedHistory() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createApprovedInvoiceScenario();

        invoicePaymentConfirmService.confirmPayment(scenario.getInvoice().getId());

        List<DbApprovalHistory> histories = dbApprovalHistoryDao.findByInvoiceId(
                scenario.getInvoice().getId());
        DbApprovalHistory paymentHistory = histories.get(histories.size() - 1);

        assertEquals(2, histories.size());
        assertEquals(scenario.getInvoice().getId(), paymentHistory.getInvoiceId());
        assertEquals("PAYMENT_CONFIRMED", paymentHistory.getStatus());
    }

    // 履歴登録時に例外が発生した場合、先に更新した請求書ステータスもロールバックされることを確認する。
    @Test
    @TxBehavior(TxBehaviorType.NONE)
    public void testConfirmPaymentRollsBackWhenHistoryInsertFails() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createApprovedInvoiceScenario();
        Long invoiceId = scenario.getInvoice().getId();
        long historyCountBefore = dbApprovalHistoryDao.count();
        DbApprovalHistoryDao originalDbApprovalHistoryDao =
                invoicePaymentConfirmService.dbApprovalHistoryDao;

        assertEquals("APPROVED", dbScenarioInvoiceDao.findById(invoiceId).getStatus());

        invoicePaymentConfirmService.dbApprovalHistoryDao =
                new FailingDbApprovalHistoryDao();
        try {
            invoicePaymentConfirmService.confirmPayment(invoiceId);
            fail("Expected RuntimeException.");
        } catch (RuntimeException e) {
            assertEquals("intentional failure for rollback test", e.getMessage());
        } finally {
            invoicePaymentConfirmService.dbApprovalHistoryDao =
                    originalDbApprovalHistoryDao;
        }

        assertEquals("APPROVED", dbScenarioInvoiceDao.findById(invoiceId).getStatus());
        assertEquals(historyCountBefore, dbApprovalHistoryDao.count());
    }

    // invoiceIdがnullの場合は支払確定できず、履歴も増えないことを確認する。
    @Test
    public void testConfirmPaymentInvoiceIdNull() {
        assertEquals(0L, dbApprovalHistoryDao.count());

        try {
            invoicePaymentConfirmService.confirmPayment(null);
            fail("Expected IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertEquals(0L, dbApprovalHistoryDao.count());
        }
    }

    // 存在しない請求書IDの場合は支払確定できず、履歴が増えないことを確認する。
    @Test
    public void testConfirmPaymentInvoiceIdNotFound() {
        assertEquals(0L, dbApprovalHistoryDao.count());

        try {
            invoicePaymentConfirmService.confirmPayment(Long.valueOf(999L));
            fail("Expected IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertEquals(0L, dbApprovalHistoryDao.count());
        }
    }

    // 未承認請求書は支払確定できず、ステータスと履歴件数が変わらないことを確認する。
    @Test
    public void testConfirmPaymentUnapprovedInvoiceDoesNotChangeState() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createUnapprovedInvoiceScenario();

        try {
            invoicePaymentConfirmService.confirmPayment(scenario.getInvoice().getId());
            fail("Expected IllegalStateException.");
        } catch (IllegalStateException e) {
            assertEquals("UNAPPROVED",
                    dbScenarioInvoiceDao.findById(scenario.getInvoice().getId()).getStatus());
            assertEquals(0L, dbApprovalHistoryDao.count());
        }
    }

    // 差戻し済み請求書は支払確定できず、ステータスと既存履歴件数が変わらないことを確認する。
    @Test
    public void testConfirmPaymentRejectedInvoiceDoesNotChangeState() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createRejectedInvoiceScenario();

        try {
            invoicePaymentConfirmService.confirmPayment(scenario.getInvoice().getId());
            fail("Expected IllegalStateException.");
        } catch (IllegalStateException e) {
            assertEquals("REJECTED",
                    dbScenarioInvoiceDao.findById(scenario.getInvoice().getId()).getStatus());
            assertEquals(1, dbApprovalHistoryDao.findByInvoiceId(scenario.getInvoice().getId()).size());
            assertEquals(1L, dbApprovalHistoryDao.count());
        }
    }

    // 支払確定済み請求書は再支払確定できず、履歴が二重登録されないことを確認する。
    @Test
    public void testConfirmPaymentAlreadyPaymentConfirmedInvoiceDoesNotInsertDuplicateHistory() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createApprovedInvoiceScenario();
        invoicePaymentConfirmService.confirmPayment(scenario.getInvoice().getId());

        try {
            invoicePaymentConfirmService.confirmPayment(scenario.getInvoice().getId());
            fail("Expected IllegalStateException.");
        } catch (IllegalStateException e) {
            assertEquals("PAYMENT_CONFIRMED",
                    dbScenarioInvoiceDao.findById(scenario.getInvoice().getId()).getStatus());
            assertEquals(2, dbApprovalHistoryDao.findByInvoiceId(scenario.getInvoice().getId()).size());
            assertEquals(2L, dbApprovalHistoryDao.count());
        }
    }

    private void dropScenarioTables() {
        dropTable("DB_APPROVAL_HISTORY");
        dropTable("DB_SCENARIO_INVOICE");
        dropTable("DB_DEPARTMENT");
        dropTable("DB_CUSTOMER");
    }

    private void dropTable(String tableName) {
        try {
            jdbcManager.updateBySql("drop table " + tableName).execute();
        } catch (RuntimeException e) {
            // H2 1.0.69でIF EXISTSに依存しないため、初回だけ例外を無視する。
        }
    }

    private static class FailingDbApprovalHistoryDao extends DbApprovalHistoryDao {

        @Override
        public int insert(DbApprovalHistory history) {
            throw new RuntimeException("intentional failure for rollback test");
        }
    }
}

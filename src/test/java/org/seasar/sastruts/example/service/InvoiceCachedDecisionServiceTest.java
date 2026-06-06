package org.seasar.sastruts.example.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.PostBindFields;
import org.seasar.sastruts.example.cache.InvoiceCache;
import org.seasar.sastruts.example.cache.InvoiceCacheLoader;
import org.seasar.sastruts.example.dao.DbApprovalHistoryDao;
import org.seasar.sastruts.example.dao.DbCustomerDao;
import org.seasar.sastruts.example.dao.DbDepartmentDao;
import org.seasar.sastruts.example.dao.DbScenarioInvoiceDao;
import org.seasar.sastruts.example.testsupport.DbInvoiceScenario;
import org.seasar.sastruts.example.testsupport.DbInvoiceScenarioFixture;
import org.seasar.sastruts.example.testsupport.SqlTestSupport;

/**
 * InvoiceCachedDecisionServiceが事前ロード済みCacheを参照する前提を検証するS2JUnit4テスト。
 * LogicやDaoにCache責務を持たせず、テスト側でH2投入とCacheLoader実行の順序を確認する。
 */
@RunWith(Seasar2.class)
public class InvoiceCachedDecisionServiceTest {

    public JdbcManager jdbcManager;

    public InvoiceCachedDecisionService invoiceCachedDecisionService;

    public InvoiceCache invoiceCache;

    public DbCustomerDao dbCustomerDao;

    public DbDepartmentDao dbDepartmentDao;

    public DbScenarioInvoiceDao dbScenarioInvoiceDao;

    public DbApprovalHistoryDao dbApprovalHistoryDao;

    private DbInvoiceScenarioFixture dbInvoiceScenarioFixture;

    private SqlTestSupport sqlTestSupport;

    private InvoiceCacheLoader invoiceCacheLoader;

    @PostBindFields
    public void setUp() {
        assertNotNull(jdbcManager);
        assertNotNull(invoiceCachedDecisionService);
        assertNotNull(invoiceCachedDecisionService.invoiceCache);
        assertNotNull(invoiceCache);
        assertNotNull(dbCustomerDao);
        assertNotNull(dbDepartmentDao);
        assertNotNull(dbScenarioInvoiceDao);
        assertNotNull(dbApprovalHistoryDao);

        invoiceCache.clear();
        invoiceCache.deactivate();
        sqlTestSupport = new SqlTestSupport(jdbcManager);
        dropScenarioTables();
        sqlTestSupport.executeSqlFile("sql/db_invoice_scenario_schema.sql");
        dbInvoiceScenarioFixture = new DbInvoiceScenarioFixture(
                dbCustomerDao,
                dbDepartmentDao,
                dbScenarioInvoiceDao,
                dbApprovalHistoryDao);
        invoiceCacheLoader = new InvoiceCacheLoader(dbScenarioInvoiceDao, invoiceCache);
    }

    // 脱Excel相当としてFixtureでH2へ投入しただけではCacheがactiveにならず、Cache not activeになることを確認する。
    @Test
    public void testCanConfirmPaymentThrowsCacheNotActiveWithoutCacheLoad() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createApprovedInvoiceScenario();

        try {
            invoiceCachedDecisionService.canConfirmPayment(scenario.getInvoice().getId());
            fail("Expected IllegalStateException.");
        } catch (IllegalStateException e) {
            assertEquals("Cache not active", e.getMessage());
            assertEquals(1L, dbScenarioInvoiceDao.count());
            assertFalse(invoiceCache.isActive());
        }
    }

    // Excel相当としてH2投入後にCacheLoader.loadを呼ぶと、APPROVED請求書を支払確定可能と判定できる。
    @Test
    public void testCanConfirmPaymentReturnsTrueAfterCacheLoadForApprovedInvoice() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createApprovedInvoiceScenario();
        invoiceCacheLoader.load();

        assertTrue(invoiceCache.isActive());
        assertTrue(invoiceCachedDecisionService.canConfirmPayment(scenario.getInvoice().getId()));
    }

    // H2投入後にCacheLoader.loadを呼んだ状態で、UNAPPROVED請求書は支払確定不可と判定される。
    @Test
    public void testCanConfirmPaymentReturnsFalseAfterCacheLoadForUnapprovedInvoice() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createUnapprovedInvoiceScenario();
        invoiceCacheLoader.load();

        assertTrue(invoiceCache.isActive());
        assertFalse(invoiceCachedDecisionService.canConfirmPayment(scenario.getInvoice().getId()));
    }

    // H2投入後にCacheLoader.loadを呼んだ状態で、REJECTED請求書は支払確定不可と判定される。
    @Test
    public void testCanConfirmPaymentReturnsFalseAfterCacheLoadForRejectedInvoice() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createRejectedInvoiceScenario();
        invoiceCacheLoader.load();

        assertTrue(invoiceCache.isActive());
        assertFalse(invoiceCachedDecisionService.canConfirmPayment(scenario.getInvoice().getId()));
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
}

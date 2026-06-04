package org.seasar.sastruts.example.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.PostBindFields;
import org.seasar.sastruts.example.cache.InvoiceCache;
import org.seasar.sastruts.example.dao.DbApprovalHistoryDao;
import org.seasar.sastruts.example.dao.DbCustomerDao;
import org.seasar.sastruts.example.dao.DbDepartmentDao;
import org.seasar.sastruts.example.dao.DbScenarioInvoiceDao;
import org.seasar.sastruts.example.entity.DbScenarioInvoice;
import org.seasar.sastruts.example.testsupport.DbInvoiceScenario;
import org.seasar.sastruts.example.testsupport.DbInvoiceScenarioFixture;
import org.seasar.sastruts.example.testsupport.SqlTestSupport;

/**
 * InvoiceCachedReferenceServiceのDB参照結果キャッシュを検証するS2JUnit4テスト。
 * H2インメモリDBで前提データを作り、初回DB取得と2回目以降のキャッシュ取得を確認する。
 */
@RunWith(Seasar2.class)
public class InvoiceCachedReferenceServiceTest {

    public JdbcManager jdbcManager;

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
        assertNotNull(invoiceCachedReferenceService);
        assertNotNull(invoiceCachedReferenceService.invoiceCache);
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

    // 初回findByIdではDBから請求書を取得し、InvoiceCacheに保存されることを確認する。
    @Test
    public void testFindByIdLoadsFromDbAndCachesInvoice() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createApprovedInvoiceScenario();
        Long invoiceId = scenario.getInvoice().getId();

        DbScenarioInvoice invoice = invoiceCachedReferenceService.findById(invoiceId);

        assertNotNull(invoice);
        assertEquals("APPROVED", invoice.getStatus());
        assertTrue(invoiceCache.contains(invoiceId));
        assertEquals(1, invoiceCache.size());
    }

    // 2回目findByIdではDBを更新してもキャッシュ済みの請求書が返ることを確認する。
    @Test
    public void testFindByIdReturnsCachedInvoiceAfterFirstLoad() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createApprovedInvoiceScenario();
        Long invoiceId = scenario.getInvoice().getId();

        DbScenarioInvoice first = invoiceCachedReferenceService.findById(invoiceId);
        dbScenarioInvoiceDao.updateStatus(invoiceId, "REJECTED");
        DbScenarioInvoice second = invoiceCachedReferenceService.findById(invoiceId);

        assertEquals("APPROVED", first.getStatus());
        assertEquals("APPROVED", second.getStatus());
        assertEquals("REJECTED", dbScenarioInvoiceDao.findById(invoiceId).getStatus());
        assertEquals(1, invoiceCache.size());
    }

    // 存在しないIDはnullを返し、null結果をキャッシュしないことを確認する。
    @Test
    public void testFindByIdDoesNotCacheMissingInvoice() {
        Long invoiceId = Long.valueOf(999L);

        DbScenarioInvoice invoice = invoiceCachedReferenceService.findById(invoiceId);

        assertNull(invoice);
        assertFalse(invoiceCache.contains(invoiceId));
        assertEquals(0, invoiceCache.size());
    }

    // setUpでcache.clear()されるため、テスト開始時点のキャッシュが空であることを確認する。
    @Test
    public void testCacheIsClearedBySetUp() {
        assertEquals(0, invoiceCache.size());
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

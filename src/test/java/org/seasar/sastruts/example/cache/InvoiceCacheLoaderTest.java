package org.seasar.sastruts.example.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.PostBindFields;
import org.seasar.sastruts.example.dao.DbApprovalHistoryDao;
import org.seasar.sastruts.example.dao.DbCustomerDao;
import org.seasar.sastruts.example.dao.DbDepartmentDao;
import org.seasar.sastruts.example.dao.DbScenarioInvoiceDao;
import org.seasar.sastruts.example.entity.DbScenarioInvoice;
import org.seasar.sastruts.example.testsupport.DbInvoiceScenario;
import org.seasar.sastruts.example.testsupport.DbInvoiceScenarioFixture;
import org.seasar.sastruts.example.testsupport.SqlTestSupport;

/**
 * H2 DBからInvoiceCacheへロードする初期化フローを検証するS2JUnit4テスト。
 * Excel方式で暗黙に行われる「H2投入後のCacheロード」を、SQL / Fixture方式で明示的に再現する。
 */
@RunWith(Seasar2.class)
public class InvoiceCacheLoaderTest {

    public JdbcManager jdbcManager;

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

    // DBにデータが存在しても、CacheLoader.load前はCacheがactiveではないことを確認する。
    @Test
    public void testCacheIsInactiveBeforeLoadEvenIfDbHasData() {
        dbInvoiceScenarioFixture.createApprovedInvoiceScenario();

        assertEquals(1L, dbScenarioInvoiceDao.count());
        assertFalse(invoiceCache.isActive());
        assertEquals(0, invoiceCache.size());
    }

    // loadを呼ぶとCacheがactiveになり、DBに投入した請求書をCacheから取得できることを確認する。
    @Test
    public void testLoadActivatesCacheAndLoadsInvoicesFromDb() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createApprovedInvoiceScenario();

        invoiceCacheLoader.load();

        assertTrue(invoiceCache.isActive());
        assertEquals(1, invoiceCache.size());
        DbScenarioInvoice cached = invoiceCache.get(scenario.getInvoice().getId());
        assertNotNull(cached);
        assertEquals("APPROVED", cached.getStatus());
    }

    // reloadは既存キャッシュをclearし、DBの最新状態だけを再ロードすることを確認する。
    @Test
    public void testReloadClearsAndLoadsCurrentDbInvoices() {
        DbInvoiceScenario approved = dbInvoiceScenarioFixture.createApprovedInvoiceScenario();
        invoiceCacheLoader.load();
        assertTrue(invoiceCache.contains(approved.getInvoice().getId()));
        invoiceCache.put(createCachedOnlyInvoice(Long.valueOf(999L)));
        assertTrue(invoiceCache.contains(Long.valueOf(999L)));

        dropScenarioTables();
        sqlTestSupport.executeSqlFile("sql/db_invoice_scenario_schema.sql");
        dbInvoiceScenarioFixture = new DbInvoiceScenarioFixture(
                dbCustomerDao,
                dbDepartmentDao,
                dbScenarioInvoiceDao,
                dbApprovalHistoryDao);
        DbInvoiceScenario unapproved = dbInvoiceScenarioFixture.createUnapprovedInvoiceScenario();

        invoiceCacheLoader.reload();

        assertTrue(invoiceCache.isActive());
        assertEquals(1, invoiceCache.size());
        assertFalse(invoiceCache.contains(Long.valueOf(999L)));
        assertEquals("UNAPPROVED", invoiceCache.get(unapproved.getInvoice().getId()).getStatus());
    }

    // clear/deactivateにより、テストごとにキャッシュ状態を初期化できることを確認する。
    @Test
    public void testClearAndDeactivateResetCacheState() {
        dbInvoiceScenarioFixture.createApprovedInvoiceScenario();
        invoiceCacheLoader.load();

        invoiceCache.clear();
        invoiceCache.deactivate();

        assertFalse(invoiceCache.isActive());
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

    private DbScenarioInvoice createCachedOnlyInvoice(Long id) {
        DbScenarioInvoice invoice = new DbScenarioInvoice();
        invoice.setId(id);
        invoice.setCustomerId(Long.valueOf(999L));
        invoice.setDepartmentId(Long.valueOf(999L));
        invoice.setTitle("cached only invoice");
        invoice.setStatus("APPROVED");
        return invoice;
    }
}

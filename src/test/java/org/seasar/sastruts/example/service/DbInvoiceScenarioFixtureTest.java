package org.seasar.sastruts.example.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.PostBindFields;
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
 * 複数TABLEをまたぐDbInvoiceScenarioFixtureを検証するS2JUnit4テスト。
 * Dao層とScenario Fixtureを対象にし、H2インメモリDB上で顧客・部署・請求書・承認履歴の関連を確認する。
 * DDLはSQLファイルで準備し、Scenario FixtureでExcelに依存しない業務状態を作成する。
 */
@RunWith(Seasar2.class)
public class DbInvoiceScenarioFixtureTest {

    public JdbcManager jdbcManager;

    public DbCustomerDao dbCustomerDao;

    public DbDepartmentDao dbDepartmentDao;

    public DbScenarioInvoiceDao dbScenarioInvoiceDao;

    public DbApprovalHistoryDao dbApprovalHistoryDao;

    private DbInvoiceScenarioFixture dbInvoiceScenarioFixture;

    private SqlTestSupport sqlTestSupport;

    @PostBindFields
    public void setUp() {
        assertNotNull(jdbcManager);
        assertNotNull(dbCustomerDao);
        assertNotNull(dbDepartmentDao);
        assertNotNull(dbScenarioInvoiceDao);
        assertNotNull(dbApprovalHistoryDao);

        sqlTestSupport = new SqlTestSupport(jdbcManager);
        dropScenarioTables();
        sqlTestSupport.executeSqlFile("sql/db_invoice_scenario_schema.sql");
        dbInvoiceScenarioFixture = new DbInvoiceScenarioFixture(
                dbCustomerDao,
                dbDepartmentDao,
                dbScenarioInvoiceDao,
                dbApprovalHistoryDao);
    }

    // 顧客・部署が存在する前提をScenario Fixtureで作り、未承認請求書と履歴なしの状態を確認する。
    @Test
    public void testCreateUnapprovedInvoiceScenario() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createUnapprovedInvoiceScenario();

        assertNotNull(dbCustomerDao.findById(scenario.getCustomer().getId()));
        assertNotNull(dbDepartmentDao.findById(scenario.getDepartment().getId()));

        DbScenarioInvoice invoice = dbScenarioInvoiceDao.findById(scenario.getInvoice().getId());
        assertNotNull(invoice);
        assertEquals("UNAPPROVED", invoice.getStatus());
        assertEquals(scenario.getCustomer().getId(), invoice.getCustomerId());
        assertEquals(scenario.getDepartment().getId(), invoice.getDepartmentId());
        assertNull(scenario.getApprovalHistory());
        assertEquals(0L, dbApprovalHistoryDao.count());
    }

    // 承認済みシナリオを作成し、顧客・部署・請求書・承認履歴がまとめてDB登録されることを確認する。
    @Test
    public void testCreateApprovedInvoiceScenario() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createApprovedInvoiceScenario();

        assertNotNull(dbCustomerDao.findById(scenario.getCustomer().getId()));
        assertNotNull(dbDepartmentDao.findById(scenario.getDepartment().getId()));
        assertNotNull(dbScenarioInvoiceDao.findById(scenario.getInvoice().getId()));
        assertNotNull(scenario.getApprovalHistory());
        assertNotNull(dbApprovalHistoryDao.findById(scenario.getApprovalHistory().getId()));
        assertEquals("APPROVED", scenario.getInvoice().getStatus());
        assertEquals("APPROVED", scenario.getApprovalHistory().getStatus());
    }

    // 承認済み請求書と承認履歴の関連を確認し、複数TABLE間のINVOICE_IDが一致することを検証する。
    @Test
    public void testApprovedScenarioLinksInvoiceAndApprovalHistory() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createApprovedInvoiceScenario();

        List<DbApprovalHistory> histories = dbApprovalHistoryDao.findByInvoiceId(
                scenario.getInvoice().getId());

        assertEquals(1, histories.size());
        assertEquals(scenario.getInvoice().getId(), histories.get(0).getInvoiceId());
        assertEquals(scenario.getApprovalHistory().getId(), histories.get(0).getId());
    }

    // 未承認・承認済み・差戻し済みを連続作成し、複数シナリオ間でIDが衝突しないことを確認する。
    @Test
    public void testMultipleScenariosDoNotConflictIds() {
        DbInvoiceScenario unapproved = dbInvoiceScenarioFixture.createUnapprovedInvoiceScenario();
        DbInvoiceScenario approved = dbInvoiceScenarioFixture.createApprovedInvoiceScenario();
        DbInvoiceScenario rejected = dbInvoiceScenarioFixture.createRejectedInvoiceScenario();

        assertNotNull(dbScenarioInvoiceDao.findById(unapproved.getInvoice().getId()));
        assertNotNull(dbScenarioInvoiceDao.findById(approved.getInvoice().getId()));
        assertNotNull(dbScenarioInvoiceDao.findById(rejected.getInvoice().getId()));
        assertEquals(3L, dbCustomerDao.count());
        assertEquals(3L, dbDepartmentDao.count());
        assertEquals(3L, dbScenarioInvoiceDao.count());
        assertEquals(2L, dbApprovalHistoryDao.count());
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

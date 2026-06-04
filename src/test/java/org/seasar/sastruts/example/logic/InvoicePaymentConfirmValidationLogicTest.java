package org.seasar.sastruts.example.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
 * InvoicePaymentConfirmValidationLogicの支払確定可否判定を検証するLogic層テスト。
 * DBはLogicへ渡す入力Entityを準備するためだけに使用し、Logic自体にDBアクセス責務を持たせない。
 * Scenario FixtureとDaoで前提データを作り、DBから取得したEntityをLogicへ渡して判定する。
 */
@RunWith(Seasar2.class)
public class InvoicePaymentConfirmValidationLogicTest {

    public JdbcManager jdbcManager;

    public InvoicePaymentConfirmValidationLogic invoicePaymentConfirmValidationLogic;

    public DbCustomerDao dbCustomerDao;

    public DbDepartmentDao dbDepartmentDao;

    public DbScenarioInvoiceDao dbScenarioInvoiceDao;

    public DbApprovalHistoryDao dbApprovalHistoryDao;

    private DbInvoiceScenarioFixture dbInvoiceScenarioFixture;

    private SqlTestSupport sqlTestSupport;

    @PostBindFields
    public void setUp() {
        assertNotNull(jdbcManager);
        assertNotNull(invoicePaymentConfirmValidationLogic);
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

    // DB上に作成した承認済み請求書を取得し、Logicで支払確定可能と判定できることを確認する。
    @Test
    public void testValidateCanConfirmPaymentApprovedInvoiceFromDb() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createApprovedInvoiceScenario();

        DbScenarioInvoice invoice = findInvoiceFromDb(scenario);

        assertEquals("APPROVED", invoice.getStatus());
        invoicePaymentConfirmValidationLogic.validateCanConfirmPayment(invoice);
    }

    // DB上に作成した未承認請求書を取得し、Logicで支払確定不可と判定されることを確認する。
    @Test(expected = IllegalStateException.class)
    public void testValidateCanConfirmPaymentUnapprovedInvoiceFromDb() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createUnapprovedInvoiceScenario();

        DbScenarioInvoice invoice = findInvoiceFromDb(scenario);

        assertEquals("UNAPPROVED", invoice.getStatus());
        invoicePaymentConfirmValidationLogic.validateCanConfirmPayment(invoice);
    }

    // DB上に作成した差戻し済み請求書を取得し、Logicで支払確定不可と判定されることを確認する。
    @Test(expected = IllegalStateException.class)
    public void testValidateCanConfirmPaymentRejectedInvoiceFromDb() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createRejectedInvoiceScenario();

        DbScenarioInvoice invoice = findInvoiceFromDb(scenario);

        assertEquals("REJECTED", invoice.getStatus());
        invoicePaymentConfirmValidationLogic.validateCanConfirmPayment(invoice);
    }

    // DB上に作成した支払確定済み請求書を取得し、Logicで再支払確定不可と判定されることを確認する。
    @Test(expected = IllegalStateException.class)
    public void testValidateCanConfirmPaymentPaymentConfirmedInvoiceFromDb() {
        DbInvoiceScenario scenario = dbInvoiceScenarioFixture.createPaymentConfirmedInvoiceScenario();

        DbScenarioInvoice invoice = findInvoiceFromDb(scenario);

        assertEquals("PAYMENT_CONFIRMED", invoice.getStatus());
        invoicePaymentConfirmValidationLogic.validateCanConfirmPayment(invoice);
    }

    private DbScenarioInvoice findInvoiceFromDb(DbInvoiceScenario scenario) {
        DbScenarioInvoice invoice = dbScenarioInvoiceDao.findById(scenario.getInvoice().getId());
        assertNotNull(invoice);
        return invoice;
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

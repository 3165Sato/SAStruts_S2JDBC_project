package org.seasar.sastruts.example.testsupport;

import org.seasar.sastruts.example.entity.DbApprovalHistory;
import org.seasar.sastruts.example.entity.DbCustomer;
import org.seasar.sastruts.example.entity.DbDepartment;
import org.seasar.sastruts.example.entity.DbScenarioInvoice;
import org.seasar.sastruts.example.dao.DbApprovalHistoryDao;
import org.seasar.sastruts.example.dao.DbCustomerDao;
import org.seasar.sastruts.example.dao.DbDepartmentDao;
import org.seasar.sastruts.example.dao.DbScenarioInvoiceDao;

/**
 * 複数TABLEをまたぐ請求書シナリオを作成するScenario Fixture。
 * 顧客・部署・請求書・承認履歴を組み合わせ、DB登録済みの業務状態を準備する。
 * Scenario Fixture自体はS2コンテナ管理せず、テスト側からDI済みDaoを渡して使用する。
 */
public class DbInvoiceScenarioFixture {

    private final DbCustomerDao dbCustomerDao;

    private final DbDepartmentDao dbDepartmentDao;

    private final DbScenarioInvoiceDao dbScenarioInvoiceDao;

    private final DbApprovalHistoryDao dbApprovalHistoryDao;

    private long nextId = 1L;

    public DbInvoiceScenarioFixture(DbCustomerDao dbCustomerDao,
            DbDepartmentDao dbDepartmentDao,
            DbScenarioInvoiceDao dbScenarioInvoiceDao,
            DbApprovalHistoryDao dbApprovalHistoryDao) {
        this.dbCustomerDao = dbCustomerDao;
        this.dbDepartmentDao = dbDepartmentDao;
        this.dbScenarioInvoiceDao = dbScenarioInvoiceDao;
        this.dbApprovalHistoryDao = dbApprovalHistoryDao;
    }

    public DbInvoiceScenario createUnapprovedInvoiceScenario() {
        Long customerId = nextId();
        Long departmentId = nextId();
        Long invoiceId = nextId();

        DbCustomer customer = DbCustomerTestDataBuilder.customer(customerId);
        DbDepartment department = DbDepartmentTestDataBuilder.department(departmentId);
        DbScenarioInvoice invoice = DbScenarioInvoiceTestDataBuilder.unapprovedInvoice(
                invoiceId, customerId, departmentId);

        dbCustomerDao.insert(customer);
        dbDepartmentDao.insert(department);
        dbScenarioInvoiceDao.insert(invoice);

        return new DbInvoiceScenario(customer, department, invoice, null);
    }

    public DbInvoiceScenario createApprovedInvoiceScenario() {
        Long customerId = nextId();
        Long departmentId = nextId();
        Long invoiceId = nextId();
        Long historyId = nextId();

        DbCustomer customer = DbCustomerTestDataBuilder.customer(customerId);
        DbDepartment department = DbDepartmentTestDataBuilder.department(departmentId);
        DbScenarioInvoice invoice = DbScenarioInvoiceTestDataBuilder.approvedInvoice(
                invoiceId, customerId, departmentId);
        DbApprovalHistory history = DbApprovalHistoryTestDataBuilder.approvedHistory(historyId, invoiceId);

        dbCustomerDao.insert(customer);
        dbDepartmentDao.insert(department);
        dbScenarioInvoiceDao.insert(invoice);
        dbApprovalHistoryDao.insert(history);

        return new DbInvoiceScenario(customer, department, invoice, history);
    }

    public DbInvoiceScenario createRejectedInvoiceScenario() {
        Long customerId = nextId();
        Long departmentId = nextId();
        Long invoiceId = nextId();
        Long historyId = nextId();

        DbCustomer customer = DbCustomerTestDataBuilder.customer(customerId);
        DbDepartment department = DbDepartmentTestDataBuilder.department(departmentId);
        DbScenarioInvoice invoice = DbScenarioInvoiceTestDataBuilder.rejectedInvoice(
                invoiceId, customerId, departmentId);
        DbApprovalHistory history = DbApprovalHistoryTestDataBuilder.rejectedHistory(historyId, invoiceId);

        dbCustomerDao.insert(customer);
        dbDepartmentDao.insert(department);
        dbScenarioInvoiceDao.insert(invoice);
        dbApprovalHistoryDao.insert(history);

        return new DbInvoiceScenario(customer, department, invoice, history);
    }

    public DbInvoiceScenario createPaymentConfirmedInvoiceScenario() {
        Long customerId = nextId();
        Long departmentId = nextId();
        Long invoiceId = nextId();
        Long historyId = nextId();

        DbCustomer customer = DbCustomerTestDataBuilder.customer(customerId);
        DbDepartment department = DbDepartmentTestDataBuilder.department(departmentId);
        DbScenarioInvoice invoice = DbScenarioInvoiceTestDataBuilder.paymentConfirmedInvoice(
                invoiceId, customerId, departmentId);
        DbApprovalHistory history = DbApprovalHistoryTestDataBuilder.paymentConfirmedHistory(historyId, invoiceId);

        dbCustomerDao.insert(customer);
        dbDepartmentDao.insert(department);
        dbScenarioInvoiceDao.insert(invoice);
        dbApprovalHistoryDao.insert(history);

        return new DbInvoiceScenario(customer, department, invoice, history);
    }

    private Long nextId() {
        Long id = Long.valueOf(nextId);
        nextId++;
        return id;
    }
}

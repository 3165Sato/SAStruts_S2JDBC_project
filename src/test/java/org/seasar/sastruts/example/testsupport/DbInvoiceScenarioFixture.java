package org.seasar.sastruts.example.testsupport;

import org.seasar.sastruts.example.entity.DbApprovalHistory;
import org.seasar.sastruts.example.entity.DbCustomer;
import org.seasar.sastruts.example.entity.DbDepartment;
import org.seasar.sastruts.example.entity.DbScenarioInvoice;
import org.seasar.sastruts.example.service.DbApprovalHistoryService;
import org.seasar.sastruts.example.service.DbCustomerService;
import org.seasar.sastruts.example.service.DbDepartmentService;
import org.seasar.sastruts.example.service.DbScenarioInvoiceService;

/**
 * 複数TABLEをまたぐ請求書シナリオを作成するScenario Fixture。
 * 顧客・部署・請求書・承認履歴を組み合わせ、DB登録済みの業務状態を準備する。
 * Scenario Fixture自体はS2コンテナ管理せず、テスト側からDI済みServiceを渡して使用する。
 */
public class DbInvoiceScenarioFixture {

    private final DbCustomerService dbCustomerService;

    private final DbDepartmentService dbDepartmentService;

    private final DbScenarioInvoiceService dbScenarioInvoiceService;

    private final DbApprovalHistoryService dbApprovalHistoryService;

    private long nextId = 1L;

    public DbInvoiceScenarioFixture(DbCustomerService dbCustomerService,
            DbDepartmentService dbDepartmentService,
            DbScenarioInvoiceService dbScenarioInvoiceService,
            DbApprovalHistoryService dbApprovalHistoryService) {
        this.dbCustomerService = dbCustomerService;
        this.dbDepartmentService = dbDepartmentService;
        this.dbScenarioInvoiceService = dbScenarioInvoiceService;
        this.dbApprovalHistoryService = dbApprovalHistoryService;
    }

    public DbInvoiceScenario createUnapprovedInvoiceScenario() {
        Long customerId = nextId();
        Long departmentId = nextId();
        Long invoiceId = nextId();

        DbCustomer customer = DbCustomerTestDataBuilder.customer(customerId);
        DbDepartment department = DbDepartmentTestDataBuilder.department(departmentId);
        DbScenarioInvoice invoice = DbScenarioInvoiceTestDataBuilder.unapprovedInvoice(
                invoiceId, customerId, departmentId);

        dbCustomerService.insert(customer);
        dbDepartmentService.insert(department);
        dbScenarioInvoiceService.insert(invoice);

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

        dbCustomerService.insert(customer);
        dbDepartmentService.insert(department);
        dbScenarioInvoiceService.insert(invoice);
        dbApprovalHistoryService.insert(history);

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

        dbCustomerService.insert(customer);
        dbDepartmentService.insert(department);
        dbScenarioInvoiceService.insert(invoice);
        dbApprovalHistoryService.insert(history);

        return new DbInvoiceScenario(customer, department, invoice, history);
    }

    private Long nextId() {
        Long id = Long.valueOf(nextId);
        nextId++;
        return id;
    }
}

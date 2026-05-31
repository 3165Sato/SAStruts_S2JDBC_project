package org.seasar.sastruts.example.testsupport;

import org.seasar.sastruts.example.entity.DbApprovalHistory;
import org.seasar.sastruts.example.entity.DbCustomer;
import org.seasar.sastruts.example.entity.DbDepartment;
import org.seasar.sastruts.example.entity.DbScenarioInvoice;

/**
 * Scenario Fixtureが作成した複数TABLEの業務状態をまとめて返すDTO。
 * テスト側で顧客・部署・請求書・承認履歴のIDや関連を検証しやすくする。
 */
public class DbInvoiceScenario {

    private final DbCustomer customer;

    private final DbDepartment department;

    private final DbScenarioInvoice invoice;

    private final DbApprovalHistory approvalHistory;

    public DbInvoiceScenario(DbCustomer customer, DbDepartment department,
            DbScenarioInvoice invoice, DbApprovalHistory approvalHistory) {
        this.customer = customer;
        this.department = department;
        this.invoice = invoice;
        this.approvalHistory = approvalHistory;
    }

    public DbCustomer getCustomer() {
        return customer;
    }

    public DbDepartment getDepartment() {
        return department;
    }

    public DbScenarioInvoice getInvoice() {
        return invoice;
    }

    public DbApprovalHistory getApprovalHistory() {
        return approvalHistory;
    }
}

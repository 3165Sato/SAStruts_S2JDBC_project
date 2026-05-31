package org.seasar.sastruts.example.testsupport;

import java.math.BigDecimal;

import org.seasar.sastruts.example.entity.DbScenarioInvoice;

public final class DbScenarioInvoiceTestDataBuilder {

    private static final String DEFAULT_TITLE = "test scenario invoice";

    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.valueOf(1000L);

    private DbScenarioInvoiceTestDataBuilder() {
    }

    public static DbScenarioInvoice unapprovedInvoice(Long id, Long customerId, Long departmentId) {
        return invoice(id, customerId, departmentId, DEFAULT_TITLE, DEFAULT_AMOUNT, "UNAPPROVED");
    }

    public static DbScenarioInvoice approvedInvoice(Long id, Long customerId, Long departmentId) {
        return invoice(id, customerId, departmentId, DEFAULT_TITLE, DEFAULT_AMOUNT, "APPROVED");
    }

    public static DbScenarioInvoice rejectedInvoice(Long id, Long customerId, Long departmentId) {
        return invoice(id, customerId, departmentId, DEFAULT_TITLE, DEFAULT_AMOUNT, "REJECTED");
    }

    public static DbScenarioInvoice invoice(Long id, Long customerId, Long departmentId,
            String title, BigDecimal amount, String status) {
        DbScenarioInvoice invoice = new DbScenarioInvoice();
        invoice.setId(id);
        invoice.setCustomerId(customerId);
        invoice.setDepartmentId(departmentId);
        invoice.setTitle(title);
        invoice.setAmount(amount);
        invoice.setStatus(status);
        return invoice;
    }
}

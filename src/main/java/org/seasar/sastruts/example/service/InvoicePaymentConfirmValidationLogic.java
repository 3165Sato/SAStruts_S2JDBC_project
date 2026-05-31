package org.seasar.sastruts.example.service;

import org.seasar.sastruts.example.entity.DbScenarioInvoice;

public class InvoicePaymentConfirmValidationLogic {

    public void validateInvoiceId(Long invoiceId) {
        if (invoiceId == null) {
            throw new IllegalArgumentException("invoiceId is required.");
        }
    }

    public DbScenarioInvoice validateInvoiceExists(DbScenarioInvoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("invoice does not exist.");
        }
        return invoice;
    }

    public void validateCanConfirmPayment(DbScenarioInvoice invoice) {
        if (!"APPROVED".equals(invoice.getStatus())) {
            throw new IllegalStateException("invoice can not be payment confirmed.");
        }
    }
}

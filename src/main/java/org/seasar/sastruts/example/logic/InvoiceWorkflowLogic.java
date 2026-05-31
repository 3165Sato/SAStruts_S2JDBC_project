package org.seasar.sastruts.example.logic;

import org.seasar.sastruts.example.entity.Invoice;
import org.seasar.sastruts.example.entity.InvoiceStatus;

public class InvoiceWorkflowLogic {

    public Invoice approve(Invoice invoice) {
        requireStatus(invoice, InvoiceStatus.UNAPPROVED, "invoice can not be approved.");
        invoice.setStatus(InvoiceStatus.APPROVED);
        return invoice;
    }

    public Invoice reject(Invoice invoice) {
        requireStatus(invoice, InvoiceStatus.UNAPPROVED, "invoice can not be rejected.");
        invoice.setStatus(InvoiceStatus.REJECTED);
        return invoice;
    }

    public Invoice confirmPayment(Invoice invoice) {
        requireStatus(invoice, InvoiceStatus.APPROVED, "invoice payment can not be confirmed.");
        invoice.setStatus(InvoiceStatus.PAYMENT_CONFIRMED);
        return invoice;
    }

    private void requireStatus(Invoice invoice, InvoiceStatus expectedStatus, String message) {
        if (expectedStatus != invoice.getStatus()) {
            throw new IllegalStateException(message);
        }
    }
}

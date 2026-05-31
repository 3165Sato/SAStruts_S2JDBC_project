package org.seasar.sastruts.example.testsupport;

import java.math.BigDecimal;

import org.seasar.sastruts.example.entity.DbInvoice;

public final class DbInvoiceTestDataBuilder {

    private static final String DEFAULT_TITLE = "test invoice";

    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.valueOf(1000L);

    private DbInvoiceTestDataBuilder() {
    }

    public static DbInvoice unapprovedInvoice(Long id) {
        return invoice(id, DEFAULT_TITLE, DEFAULT_AMOUNT, "UNAPPROVED");
    }

    public static DbInvoice approvedInvoice(Long id) {
        return invoice(id, DEFAULT_TITLE, DEFAULT_AMOUNT, "APPROVED");
    }

    public static DbInvoice rejectedInvoice(Long id) {
        return invoice(id, DEFAULT_TITLE, DEFAULT_AMOUNT, "REJECTED");
    }

    public static DbInvoice paymentConfirmedInvoice(Long id) {
        return invoice(id, DEFAULT_TITLE, DEFAULT_AMOUNT, "PAYMENT_CONFIRMED");
    }

    public static DbInvoice invoice(Long id, String title, BigDecimal amount, String status) {
        DbInvoice invoice = new DbInvoice();
        invoice.setId(id);
        invoice.setTitle(title);
        invoice.setAmount(amount);
        invoice.setStatus(status);
        return invoice;
    }
}

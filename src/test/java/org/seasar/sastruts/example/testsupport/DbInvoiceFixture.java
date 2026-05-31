package org.seasar.sastruts.example.testsupport;

import org.seasar.sastruts.example.entity.DbInvoice;
import org.seasar.sastruts.example.service.DbInvoiceService;

public class DbInvoiceFixture {

    private final DbInvoiceService dbInvoiceService;

    public DbInvoiceFixture(DbInvoiceService dbInvoiceService) {
        this.dbInvoiceService = dbInvoiceService;
    }

    public DbInvoice insertUnapprovedInvoice(Long id) {
        return insertInvoice(DbInvoiceTestDataBuilder.unapprovedInvoice(id));
    }

    public DbInvoice insertApprovedInvoice(Long id) {
        return insertInvoice(DbInvoiceTestDataBuilder.approvedInvoice(id));
    }

    public DbInvoice insertRejectedInvoice(Long id) {
        return insertInvoice(DbInvoiceTestDataBuilder.rejectedInvoice(id));
    }

    public DbInvoice insertPaymentConfirmedInvoice(Long id) {
        return insertInvoice(DbInvoiceTestDataBuilder.paymentConfirmedInvoice(id));
    }

    public DbInvoice insertInvoice(DbInvoice invoice) {
        dbInvoiceService.insert(invoice);
        return invoice;
    }
}

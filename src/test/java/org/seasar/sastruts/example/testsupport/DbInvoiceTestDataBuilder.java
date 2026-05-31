package org.seasar.sastruts.example.testsupport;

import java.math.BigDecimal;

import org.seasar.sastruts.example.entity.DbInvoice;

/**
 * DbInvoiceのテストデータを作成するBuilder。
 * Entityオブジェクトを作るだけで、DB登録は行わない。
 * テストデータの意味をメソッド名で表現し、項目追加時の修正範囲を局所化する。
 */
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

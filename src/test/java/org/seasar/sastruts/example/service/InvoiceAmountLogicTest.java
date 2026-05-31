package org.seasar.sastruts.example.service;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.seasar.sastruts.example.entity.Invoice;
import org.seasar.sastruts.example.entity.InvoiceStatus;

/**
 * InvoiceAmountLogicの金額変更ルールを検証するS2JUnit4テスト。
 * Amount Logic層を対象にし、未承認のみ金額変更できることと、承認後は変更できないことを確認する。
 * DBは使わず、業務状態ごとの金額変更可否を小さな単位で検証する。
 */
@RunWith(Seasar2.class)
public class InvoiceAmountLogicTest {

    private final InvoiceAmountLogic invoiceAmountLogic = new InvoiceAmountLogic();

    // 未承認の請求書は金額変更でき、ステータスが変わらないことを確認する。
    @Test
    public void testChangeAmountUnapprovedInvoice() {
        Invoice invoice = createInvoice(InvoiceStatus.UNAPPROVED);

        Invoice changed = invoiceAmountLogic.changeAmount(invoice, BigDecimal.valueOf(2000L));

        assertEquals(BigDecimal.valueOf(2000L), changed.getAmount());
        assertEquals(InvoiceStatus.UNAPPROVED, changed.getStatus());
    }

    // 承認済みの請求書は金額変更できないことを確認する。
    @Test(expected = IllegalStateException.class)
    public void testChangeAmountApprovedInvoice() {
        Invoice invoice = createInvoice(InvoiceStatus.APPROVED);

        invoiceAmountLogic.changeAmount(invoice, BigDecimal.valueOf(2000L));
    }

    private Invoice createInvoice(InvoiceStatus status) {
        Invoice invoice = new Invoice();
        invoice.setId(Long.valueOf(1L));
        invoice.setTitle("test invoice");
        invoice.setAmount(BigDecimal.valueOf(1000L));
        invoice.setStatus(status);
        return invoice;
    }
}

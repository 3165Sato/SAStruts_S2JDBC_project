package org.seasar.sastruts.example.logic;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.seasar.sastruts.example.entity.Invoice;
import org.seasar.sastruts.example.entity.InvoiceStatus;

/**
 * InvoiceAmountLogicの金額変更ルールを検証するLogic層テスト。
 * DB副作用やService全体の業務フローは扱わず、状態ごとの金額変更可否を確認する。
 */
@RunWith(Seasar2.class)
public class InvoiceAmountLogicTest {

    public InvoiceAmountLogic invoiceAmountLogic;

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

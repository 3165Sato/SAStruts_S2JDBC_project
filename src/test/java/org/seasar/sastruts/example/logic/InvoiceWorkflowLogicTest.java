package org.seasar.sastruts.example.logic;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.seasar.sastruts.example.entity.Invoice;
import org.seasar.sastruts.example.entity.InvoiceStatus;

/**
 * InvoiceWorkflowLogicの状態遷移ルールを検証するLogic層テスト。
 * DB副作用やService全体の業務フローは扱わず、状態ごとの許可・不許可を確認する。
 */
@RunWith(Seasar2.class)
public class InvoiceWorkflowLogicTest {

    public InvoiceWorkflowLogic invoiceWorkflowLogic;

    // 未承認の請求書を承認済みに変更できることを確認する。
    @Test
    public void testApproveUnapprovedInvoice() {
        Invoice invoice = createInvoice(InvoiceStatus.UNAPPROVED);

        Invoice approved = invoiceWorkflowLogic.approve(invoice);

        assertEquals(InvoiceStatus.APPROVED, approved.getStatus());
    }

    // 差戻し済みの請求書は承認できないことを確認する。
    @Test(expected = IllegalStateException.class)
    public void testApproveRejectedInvoice() {
        invoiceWorkflowLogic.approve(createInvoice(InvoiceStatus.REJECTED));
    }

    // 未承認の請求書を差戻しに変更できることを確認する。
    @Test
    public void testRejectUnapprovedInvoice() {
        Invoice invoice = createInvoice(InvoiceStatus.UNAPPROVED);

        Invoice rejected = invoiceWorkflowLogic.reject(invoice);

        assertEquals(InvoiceStatus.REJECTED, rejected.getStatus());
    }

    // 承認済みの請求書は差戻しできないことを確認する。
    @Test(expected = IllegalStateException.class)
    public void testRejectApprovedInvoice() {
        invoiceWorkflowLogic.reject(createInvoice(InvoiceStatus.APPROVED));
    }

    // 承認済みの請求書を支払確定に変更できることを確認する。
    @Test
    public void testConfirmPaymentApprovedInvoice() {
        Invoice invoice = createInvoice(InvoiceStatus.APPROVED);

        Invoice paymentConfirmed = invoiceWorkflowLogic.confirmPayment(invoice);

        assertEquals(InvoiceStatus.PAYMENT_CONFIRMED, paymentConfirmed.getStatus());
    }

    // 未承認の請求書は支払確定できないことを確認する。
    @Test(expected = IllegalStateException.class)
    public void testConfirmPaymentUnapprovedInvoice() {
        invoiceWorkflowLogic.confirmPayment(createInvoice(InvoiceStatus.UNAPPROVED));
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

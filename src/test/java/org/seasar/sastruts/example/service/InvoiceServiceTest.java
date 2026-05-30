package org.seasar.sastruts.example.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.PostBindFields;
import org.seasar.sastruts.example.entity.Invoice;
import org.seasar.sastruts.example.entity.InvoiceStatus;

@RunWith(Seasar2.class)
public class InvoiceServiceTest {

    public InvoiceService invoiceService;

    @PostBindFields
    public void setUp() {
        assertNotNull(invoiceService);
        invoiceService.clear();
    }

    // 請求書を登録し、ID・タイトル・金額・検索結果が正しいことを確認する。
    @Test
    public void testRegister() {
        Invoice invoice = invoiceService.register("test invoice", BigDecimal.valueOf(1000L));

        assertNotNull(invoice);
        assertNotNull(invoice.getId());
        assertEquals("test invoice", invoice.getTitle());
        assertEquals(BigDecimal.valueOf(1000L), invoice.getAmount());
        assertEquals(invoice, invoiceService.findById(invoice.getId()));
    }

    // 請求書登録時の初期ステータスが未承認であることを確認する。
    @Test
    public void testRegisterStatusIsUnapproved() {
        Invoice invoice = invoiceService.register("test invoice", BigDecimal.valueOf(1000L));

        assertEquals(InvoiceStatus.UNAPPROVED, invoice.getStatus());
    }

    // 金額が1円の場合に請求書を登録できることを確認する。
    @Test
    public void testRegisterAmountOne() {
        Invoice invoice = invoiceService.register("test invoice", BigDecimal.ONE);

        assertEquals(BigDecimal.ONE, invoice.getAmount());
    }

    // 金額がnullの場合に登録できないことを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterAmountNull() {
        invoiceService.register("test invoice", null);
    }

    // 金額が0円の場合に登録できないことを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterAmountZero() {
        invoiceService.register("test invoice", BigDecimal.ZERO);
    }

    // 金額がマイナスの場合に登録できないことを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterAmountMinus() {
        invoiceService.register("test invoice", BigDecimal.valueOf(-1L));
    }

    // タイトルがnullの場合に登録できないことを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterTitleNull() {
        invoiceService.register(null, BigDecimal.valueOf(1000L));
    }

    // タイトルが空文字の場合に登録できないことを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterTitleEmpty() {
        invoiceService.register("", BigDecimal.valueOf(1000L));
    }

    // タイトルが半角スペースのみの場合に登録できないことを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterTitleOnlySpaces() {
        invoiceService.register("   ", BigDecimal.valueOf(1000L));
    }

    // 複数の請求書を登録した場合にIDが重複しないことを確認する。
    @Test
    public void testRegisterMultipleInvoicesIdIsUnique() {
        Invoice first = invoiceService.register("first invoice", BigDecimal.valueOf(1000L));
        Invoice second = invoiceService.register("second invoice", BigDecimal.valueOf(2000L));

        assertNotNull(first.getId());
        assertNotNull(second.getId());
        assertFalse(first.getId().equals(second.getId()));
    }

    // 請求書IDがnullの場合にfindByIdがnullを返すことを確認する。
    @Test
    public void testFindByIdNull() {
        assertNull(invoiceService.findById(null));
    }

    // 存在しない請求書IDの場合にfindByIdがnullを返すことを確認する。
    @Test
    public void testFindByIdNotFound() {
        assertNull(invoiceService.findById(Long.valueOf(999L)));
    }

    // 未承認の請求書を承認済みに変更できることを確認する。
    @Test
    public void testApproveUnapprovedInvoice() {
        Invoice invoice = registerUnapprovedInvoice();

        Invoice approved = invoiceService.approve(invoice.getId());

        assertEquals(InvoiceStatus.APPROVED, approved.getStatus());
        assertEquals(InvoiceStatus.APPROVED, invoiceService.findById(invoice.getId()).getStatus());
    }

    // 請求書IDがnullの場合に承認できないことを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testApproveInvoiceIdNull() {
        invoiceService.approve(null);
    }

    // 存在しない請求書IDの場合に承認できないことを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testApproveInvoiceIdNotFound() {
        invoiceService.approve(Long.valueOf(999L));
    }

    // 承認済みの請求書を再承認できないことを確認する。
    @Test(expected = IllegalStateException.class)
    public void testApproveAlreadyApprovedInvoice() {
        Invoice invoice = registerApprovedInvoice();

        invoiceService.approve(invoice.getId());
    }

    // 差戻し済みの請求書を承認できないことを確認する。
    @Test(expected = IllegalStateException.class)
    public void testApproveRejectedInvoice() {
        Invoice invoice = registerRejectedInvoice();

        invoiceService.approve(invoice.getId());
    }

    // 支払確定済みの請求書を承認できないことを確認する。
    @Test(expected = IllegalStateException.class)
    public void testApprovePaymentConfirmedInvoice() {
        Invoice invoice = registerPaymentConfirmedInvoice();

        invoiceService.approve(invoice.getId());
    }

    // 承認できない場合に請求書ステータスが変更されないことを確認する。
    @Test
    public void testApproveFailureDoesNotChangeStatus() {
        Invoice invoice = registerRejectedInvoice();

        try {
            invoiceService.approve(invoice.getId());
            fail("Expected IllegalStateException.");
        } catch (IllegalStateException e) {
            assertEquals(InvoiceStatus.REJECTED, invoice.getStatus());
            assertEquals(InvoiceStatus.REJECTED, invoiceService.findById(invoice.getId()).getStatus());
        }
    }

    // 未承認の請求書を差戻しに変更できることを確認する。
    @Test
    public void testRejectUnapprovedInvoice() {
        Invoice invoice = registerUnapprovedInvoice();

        Invoice rejected = invoiceService.reject(invoice.getId());

        assertEquals(InvoiceStatus.REJECTED, rejected.getStatus());
        assertEquals(InvoiceStatus.REJECTED, invoiceService.findById(invoice.getId()).getStatus());
    }

    // 請求書IDがnullの場合に差戻しできないことを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testRejectInvoiceIdNull() {
        invoiceService.reject(null);
    }

    // 存在しない請求書IDの場合に差戻しできないことを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testRejectInvoiceIdNotFound() {
        invoiceService.reject(Long.valueOf(999L));
    }

    // 承認済みの請求書を差戻しできないことを確認する。
    @Test(expected = IllegalStateException.class)
    public void testRejectApprovedInvoice() {
        Invoice invoice = registerApprovedInvoice();

        invoiceService.reject(invoice.getId());
    }

    // 差戻し済みの請求書を再差戻しできないことを確認する。
    @Test(expected = IllegalStateException.class)
    public void testRejectAlreadyRejectedInvoice() {
        Invoice invoice = registerRejectedInvoice();

        invoiceService.reject(invoice.getId());
    }

    // 支払確定済みの請求書を差戻しできないことを確認する。
    @Test(expected = IllegalStateException.class)
    public void testRejectPaymentConfirmedInvoice() {
        Invoice invoice = registerPaymentConfirmedInvoice();

        invoiceService.reject(invoice.getId());
    }

    // 差戻しできない場合に請求書ステータスが変更されないことを確認する。
    @Test
    public void testRejectFailureDoesNotChangeStatus() {
        Invoice invoice = registerApprovedInvoice();

        try {
            invoiceService.reject(invoice.getId());
            fail("Expected IllegalStateException.");
        } catch (IllegalStateException e) {
            assertEquals(InvoiceStatus.APPROVED, invoice.getStatus());
            assertEquals(InvoiceStatus.APPROVED, invoiceService.findById(invoice.getId()).getStatus());
        }
    }

    // 承認済みの請求書を支払確定に変更できることを確認する。
    @Test
    public void testConfirmPaymentApprovedInvoice() {
        Invoice invoice = registerApprovedInvoice();

        Invoice paymentConfirmed = invoiceService.confirmPayment(invoice.getId());

        assertEquals(InvoiceStatus.PAYMENT_CONFIRMED, paymentConfirmed.getStatus());
        assertEquals(InvoiceStatus.PAYMENT_CONFIRMED, invoiceService.findById(invoice.getId()).getStatus());
    }

    // 請求書IDがnullの場合に支払確定できないことを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testConfirmPaymentInvoiceIdNull() {
        invoiceService.confirmPayment(null);
    }

    // 存在しない請求書IDの場合に支払確定できないことを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testConfirmPaymentInvoiceIdNotFound() {
        invoiceService.confirmPayment(Long.valueOf(999L));
    }

    // 未承認の請求書を支払確定できないことを確認する。
    @Test(expected = IllegalStateException.class)
    public void testConfirmPaymentUnapprovedInvoice() {
        Invoice invoice = registerUnapprovedInvoice();

        invoiceService.confirmPayment(invoice.getId());
    }

    // 差戻し済みの請求書を支払確定できないことを確認する。
    @Test(expected = IllegalStateException.class)
    public void testConfirmPaymentRejectedInvoice() {
        Invoice invoice = registerRejectedInvoice();

        invoiceService.confirmPayment(invoice.getId());
    }

    // 支払確定済みの請求書を再度支払確定できないことを確認する。
    @Test(expected = IllegalStateException.class)
    public void testConfirmPaymentAlreadyPaymentConfirmedInvoice() {
        Invoice invoice = registerPaymentConfirmedInvoice();

        invoiceService.confirmPayment(invoice.getId());
    }

    // 支払確定できない場合に請求書ステータスが変更されないことを確認する。
    @Test
    public void testConfirmPaymentFailureDoesNotChangeStatus() {
        Invoice invoice = registerUnapprovedInvoice();

        try {
            invoiceService.confirmPayment(invoice.getId());
            fail("Expected IllegalStateException.");
        } catch (IllegalStateException e) {
            assertEquals(InvoiceStatus.UNAPPROVED, invoice.getStatus());
            assertEquals(InvoiceStatus.UNAPPROVED, invoiceService.findById(invoice.getId()).getStatus());
        }
    }

    // 未承認の請求書の金額を変更でき、ステータスが未承認のままであることを確認する。
    @Test
    public void testChangeAmountUnapprovedInvoice() {
        Invoice invoice = registerUnapprovedInvoice();

        Invoice changed = invoiceService.changeAmount(invoice.getId(), BigDecimal.valueOf(2000L));

        assertEquals(BigDecimal.valueOf(2000L), changed.getAmount());
        assertEquals(InvoiceStatus.UNAPPROVED, changed.getStatus());
        assertEquals(BigDecimal.valueOf(2000L), invoiceService.findById(invoice.getId()).getAmount());
    }

    // 変更後金額が1円の場合に金額変更できることを確認する。
    @Test
    public void testChangeAmountToOne() {
        Invoice invoice = registerUnapprovedInvoice();

        Invoice changed = invoiceService.changeAmount(invoice.getId(), BigDecimal.ONE);

        assertEquals(BigDecimal.ONE, changed.getAmount());
    }

    // 請求書IDがnullの場合に金額変更できないことを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testChangeAmountInvoiceIdNull() {
        invoiceService.changeAmount(null, BigDecimal.valueOf(2000L));
    }

    // 存在しない請求書IDの場合に金額変更できないことを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testChangeAmountInvoiceIdNotFound() {
        invoiceService.changeAmount(Long.valueOf(999L), BigDecimal.valueOf(2000L));
    }

    // 変更後金額がnullの場合に金額変更できないことを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testChangeAmountNull() {
        Invoice invoice = registerUnapprovedInvoice();

        invoiceService.changeAmount(invoice.getId(), null);
    }

    // 変更後金額が0円の場合に金額変更できないことを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testChangeAmountZero() {
        Invoice invoice = registerUnapprovedInvoice();

        invoiceService.changeAmount(invoice.getId(), BigDecimal.ZERO);
    }

    // 変更後金額がマイナスの場合に金額変更できないことを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testChangeAmountMinus() {
        Invoice invoice = registerUnapprovedInvoice();

        invoiceService.changeAmount(invoice.getId(), BigDecimal.valueOf(-1L));
    }

    // 承認済みの請求書の金額を変更できないことを確認する。
    @Test(expected = IllegalStateException.class)
    public void testChangeAmountApprovedInvoice() {
        Invoice invoice = registerApprovedInvoice();

        invoiceService.changeAmount(invoice.getId(), BigDecimal.valueOf(2000L));
    }

    // 差戻し済みの請求書の金額を変更できないことを確認する。
    @Test(expected = IllegalStateException.class)
    public void testChangeAmountRejectedInvoice() {
        Invoice invoice = registerRejectedInvoice();

        invoiceService.changeAmount(invoice.getId(), BigDecimal.valueOf(2000L));
    }

    // 支払確定済みの請求書の金額を変更できないことを確認する。
    @Test(expected = IllegalStateException.class)
    public void testChangeAmountPaymentConfirmedInvoice() {
        Invoice invoice = registerPaymentConfirmedInvoice();

        invoiceService.changeAmount(invoice.getId(), BigDecimal.valueOf(2000L));
    }

    // 金額変更できない状態の場合に金額とステータスが変更されないことを確認する。
    @Test
    public void testChangeAmountFailureDoesNotChangeAmountAndStatus() {
        Invoice invoice = registerApprovedInvoice();

        try {
            invoiceService.changeAmount(invoice.getId(), BigDecimal.valueOf(2000L));
            fail("Expected IllegalStateException.");
        } catch (IllegalStateException e) {
            assertEquals(BigDecimal.valueOf(1000L), invoice.getAmount());
            assertEquals(InvoiceStatus.APPROVED, invoice.getStatus());
            assertEquals(BigDecimal.valueOf(1000L), invoiceService.findById(invoice.getId()).getAmount());
            assertEquals(InvoiceStatus.APPROVED, invoiceService.findById(invoice.getId()).getStatus());
        }
    }

    // 変更後金額が不正な場合に金額とステータスが変更されないことを確認する。
    @Test
    public void testChangeAmountInvalidAmountDoesNotChangeAmountAndStatus() {
        Invoice invoice = registerUnapprovedInvoice();

        try {
            invoiceService.changeAmount(invoice.getId(), BigDecimal.ZERO);
            fail("Expected IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertEquals(BigDecimal.valueOf(1000L), invoice.getAmount());
            assertEquals(InvoiceStatus.UNAPPROVED, invoice.getStatus());
            assertEquals(BigDecimal.valueOf(1000L), invoiceService.findById(invoice.getId()).getAmount());
            assertEquals(InvoiceStatus.UNAPPROVED, invoiceService.findById(invoice.getId()).getStatus());
        }
    }

    private Invoice registerUnapprovedInvoice() {
        return invoiceService.register("test invoice", BigDecimal.valueOf(1000L));
    }

    private Invoice registerApprovedInvoice() {
        Invoice invoice = registerUnapprovedInvoice();
        return invoiceService.approve(invoice.getId());
    }

    private Invoice registerRejectedInvoice() {
        Invoice invoice = registerUnapprovedInvoice();
        return invoiceService.reject(invoice.getId());
    }

    private Invoice registerPaymentConfirmedInvoice() {
        Invoice invoice = registerApprovedInvoice();
        return invoiceService.confirmPayment(invoice.getId());
    }
}

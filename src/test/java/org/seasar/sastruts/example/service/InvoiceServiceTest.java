package org.seasar.sastruts.example.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.seasar.sastruts.example.entity.Invoice;
import org.seasar.sastruts.example.entity.InvoiceStatus;

@RunWith(Seasar2.class)
public class InvoiceServiceTest {

    private InvoiceService invoiceService;

    @Before
    public void setUp() {
        invoiceService = new InvoiceService();
        invoiceService.clear();
    }

    @Test
    public void testRegister() {
        Invoice invoice = invoiceService.register("test invoice", BigDecimal.valueOf(1000L));

        assertNotNull(invoice);
        assertNotNull(invoice.getId());
        assertEquals("test invoice", invoice.getTitle());
        assertEquals(BigDecimal.valueOf(1000L), invoice.getAmount());
        assertEquals(invoice, invoiceService.findById(invoice.getId()));
    }

    @Test
    public void testRegisterStatusIsUnapproved() {
        Invoice invoice = invoiceService.register("test invoice", BigDecimal.valueOf(1000L));

        assertEquals(InvoiceStatus.UNAPPROVED, invoice.getStatus());
    }

    @Test
    public void testApprove() {
        Invoice invoice = invoiceService.register("test invoice", BigDecimal.valueOf(1000L));

        Invoice approved = invoiceService.approve(invoice.getId());

        assertEquals(InvoiceStatus.APPROVED, approved.getStatus());
        assertEquals(InvoiceStatus.APPROVED, invoiceService.findById(invoice.getId()).getStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterAmountNull() {
        invoiceService.register("test invoice", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterAmountZero() {
        invoiceService.register("test invoice", BigDecimal.ZERO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterAmountMinus() {
        invoiceService.register("test invoice", BigDecimal.valueOf(-1L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterTitleNull() {
        invoiceService.register(null, BigDecimal.valueOf(1000L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterTitleEmpty() {
        invoiceService.register("", BigDecimal.valueOf(1000L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApproveInvoiceIdNull() {
        invoiceService.approve(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApproveInvoiceIdNotFound() {
        invoiceService.approve(Long.valueOf(999L));
    }

    @Test(expected = IllegalStateException.class)
    public void testApproveAlreadyApproved() {
        Invoice invoice = invoiceService.register("test invoice", BigDecimal.valueOf(1000L));
        invoiceService.approve(invoice.getId());

        invoiceService.approve(invoice.getId());
    }
}

package org.seasar.sastruts.example.service;

import java.math.BigDecimal;

import org.seasar.sastruts.example.entity.Invoice;

public class InvoiceService {

    private final InvoiceValidationLogic invoiceValidationLogic = new InvoiceValidationLogic();

    private final InvoiceWorkflowLogic invoiceWorkflowLogic = new InvoiceWorkflowLogic();

    private final InvoiceAmountLogic invoiceAmountLogic = new InvoiceAmountLogic();

    private final InvoiceStore invoiceStore = new InvoiceStore();

    public Invoice register(String title, BigDecimal amount) {
        invoiceValidationLogic.validateTitle(title);
        invoiceValidationLogic.validateAmount(amount);
        return invoiceStore.save(title, amount);
    }

    public Invoice approve(Long invoiceId) {
        Invoice invoice = findRequiredInvoice(invoiceId);
        return invoiceWorkflowLogic.approve(invoice);
    }

    public Invoice reject(Long invoiceId) {
        Invoice invoice = findRequiredInvoice(invoiceId);
        return invoiceWorkflowLogic.reject(invoice);
    }

    public Invoice confirmPayment(Long invoiceId) {
        Invoice invoice = findRequiredInvoice(invoiceId);
        return invoiceWorkflowLogic.confirmPayment(invoice);
    }

    public Invoice changeAmount(Long invoiceId, BigDecimal amount) {
        invoiceValidationLogic.validateAmount(amount);
        Invoice invoice = findRequiredInvoice(invoiceId);
        return invoiceAmountLogic.changeAmount(invoice, amount);
    }

    public Invoice findById(Long invoiceId) {
        return invoiceStore.findById(invoiceId);
    }

    public void clear() {
        invoiceStore.clear();
    }

    private Invoice findRequiredInvoice(Long invoiceId) {
        invoiceValidationLogic.validateInvoiceId(invoiceId);
        return invoiceValidationLogic.validateInvoiceExists(invoiceStore.findById(invoiceId));
    }
}

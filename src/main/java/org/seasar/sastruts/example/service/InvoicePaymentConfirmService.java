package org.seasar.sastruts.example.service;

import org.seasar.sastruts.example.entity.DbApprovalHistory;
import org.seasar.sastruts.example.entity.DbScenarioInvoice;
import org.seasar.sastruts.example.logic.InvoicePaymentConfirmValidationLogic;
import org.seasar.sastruts.example.logic.InvoicePaymentHistoryLogic;

public class InvoicePaymentConfirmService {

    public DbScenarioInvoiceService dbScenarioInvoiceService;

    public DbApprovalHistoryService dbApprovalHistoryService;

    public InvoicePaymentConfirmValidationLogic invoicePaymentConfirmValidationLogic;

    public InvoicePaymentHistoryLogic invoicePaymentHistoryLogic;

    public DbScenarioInvoice confirmPayment(Long invoiceId) {
        invoicePaymentConfirmValidationLogic.validateInvoiceId(invoiceId);

        DbScenarioInvoice invoice = dbScenarioInvoiceService.findById(invoiceId);
        invoicePaymentConfirmValidationLogic.validateInvoiceExists(invoice);
        invoicePaymentConfirmValidationLogic.validateCanConfirmPayment(invoice);

        dbScenarioInvoiceService.updateStatus(invoiceId, "PAYMENT_CONFIRMED");
        DbScenarioInvoice paymentConfirmed = dbScenarioInvoiceService.findById(invoiceId);
        DbApprovalHistory history = invoicePaymentHistoryLogic.createPaymentConfirmedHistory(paymentConfirmed);
        dbApprovalHistoryService.insert(history);
        return paymentConfirmed;
    }
}

package org.seasar.sastruts.example.service;

import org.seasar.sastruts.example.entity.DbApprovalHistory;
import org.seasar.sastruts.example.entity.DbScenarioInvoice;

public class InvoicePaymentConfirmService {

    public DbScenarioInvoiceService dbScenarioInvoiceService;

    public DbApprovalHistoryService dbApprovalHistoryService;

    private final InvoicePaymentConfirmValidationLogic invoicePaymentConfirmValidationLogic =
            new InvoicePaymentConfirmValidationLogic();

    private final InvoicePaymentHistoryLogic invoicePaymentHistoryLogic = new InvoicePaymentHistoryLogic();

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

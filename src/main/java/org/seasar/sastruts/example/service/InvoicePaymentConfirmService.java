package org.seasar.sastruts.example.service;

import org.seasar.sastruts.example.dao.DbApprovalHistoryDao;
import org.seasar.sastruts.example.dao.DbScenarioInvoiceDao;
import org.seasar.sastruts.example.cache.InvoiceCache;
import org.seasar.sastruts.example.entity.DbApprovalHistory;
import org.seasar.sastruts.example.entity.DbScenarioInvoice;
import org.seasar.sastruts.example.logic.InvoicePaymentConfirmValidationLogic;
import org.seasar.sastruts.example.logic.InvoicePaymentHistoryLogic;

public class InvoicePaymentConfirmService {

    public DbScenarioInvoiceDao dbScenarioInvoiceDao;

    public DbApprovalHistoryDao dbApprovalHistoryDao;

    public InvoiceCache invoiceCache;

    public InvoicePaymentConfirmValidationLogic invoicePaymentConfirmValidationLogic;

    public InvoicePaymentHistoryLogic invoicePaymentHistoryLogic;

    public DbScenarioInvoice confirmPayment(Long invoiceId) {
        invoicePaymentConfirmValidationLogic.validateInvoiceId(invoiceId);

        DbScenarioInvoice invoice = dbScenarioInvoiceDao.findById(invoiceId);
        invoicePaymentConfirmValidationLogic.validateInvoiceExists(invoice);
        invoicePaymentConfirmValidationLogic.validateCanConfirmPayment(invoice);

        dbScenarioInvoiceDao.updateStatus(invoiceId, "PAYMENT_CONFIRMED");
        DbScenarioInvoice paymentConfirmed = dbScenarioInvoiceDao.findById(invoiceId);
        DbApprovalHistory history = invoicePaymentHistoryLogic.createPaymentConfirmedHistory(paymentConfirmed);
        dbApprovalHistoryDao.insert(history);
        invoiceCache.evict(invoiceId);
        return paymentConfirmed;
    }
}

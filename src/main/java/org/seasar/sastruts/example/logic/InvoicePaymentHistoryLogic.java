package org.seasar.sastruts.example.logic;

import java.sql.Timestamp;

import org.seasar.sastruts.example.entity.DbApprovalHistory;
import org.seasar.sastruts.example.entity.DbScenarioInvoice;

public class InvoicePaymentHistoryLogic {

    private static final Timestamp PAYMENT_CONFIRMED_AT = Timestamp.valueOf("2026-01-01 12:00:00");

    public DbApprovalHistory createPaymentConfirmedHistory(DbScenarioInvoice invoice) {
        DbApprovalHistory history = new DbApprovalHistory();
        history.setId(Long.valueOf(invoice.getId().longValue() * 1000L + 1L));
        history.setInvoiceId(invoice.getId());
        history.setApproverName("payment confirmer");
        history.setApprovedAt(PAYMENT_CONFIRMED_AT);
        history.setStatus("PAYMENT_CONFIRMED");
        return history;
    }
}

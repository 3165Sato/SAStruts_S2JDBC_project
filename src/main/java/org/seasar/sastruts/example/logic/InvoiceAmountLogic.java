package org.seasar.sastruts.example.logic;

import java.math.BigDecimal;

import org.seasar.sastruts.example.entity.Invoice;
import org.seasar.sastruts.example.entity.InvoiceStatus;

public class InvoiceAmountLogic {

    public Invoice changeAmount(Invoice invoice, BigDecimal amount) {
        requireUnapproved(invoice);
        invoice.setAmount(amount);
        return invoice;
    }

    private void requireUnapproved(Invoice invoice) {
        if (InvoiceStatus.UNAPPROVED != invoice.getStatus()) {
            throw new IllegalStateException("invoice amount can not be changed.");
        }
    }
}

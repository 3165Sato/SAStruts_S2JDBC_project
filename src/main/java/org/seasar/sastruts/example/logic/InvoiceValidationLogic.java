package org.seasar.sastruts.example.logic;

import java.math.BigDecimal;

import org.seasar.sastruts.example.entity.Invoice;

public class InvoiceValidationLogic {

    public void validateTitle(String title) {
        if (title == null || title.trim().length() == 0) {
            throw new IllegalArgumentException("title must not be empty.");
        }
    }

    public void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("amount must not be null.");
        }
        if (BigDecimal.ZERO.compareTo(amount) >= 0) {
            throw new IllegalArgumentException("amount must be greater than zero.");
        }
    }

    public void validateInvoiceId(Long invoiceId) {
        if (invoiceId == null) {
            throw new IllegalArgumentException("invoiceId must not be null.");
        }
    }

    public Invoice validateInvoiceExists(Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("invoice does not exist.");
        }
        return invoice;
    }
}

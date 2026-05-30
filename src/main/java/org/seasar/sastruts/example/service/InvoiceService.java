package org.seasar.sastruts.example.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.seasar.sastruts.example.entity.Invoice;
import org.seasar.sastruts.example.entity.InvoiceStatus;

public class InvoiceService {

    private final Map<Long, Invoice> invoices = new LinkedHashMap<Long, Invoice>();

    private long sequence = 1L;

    public Invoice register(String title, BigDecimal amount) {
        validateTitle(title);
        validateAmount(amount);

        Invoice invoice = new Invoice();
        invoice.setId(Long.valueOf(sequence++));
        invoice.setTitle(title);
        invoice.setAmount(amount);
        invoice.setStatus(InvoiceStatus.UNAPPROVED);
        invoices.put(invoice.getId(), invoice);
        return invoice;
    }

    public Invoice approve(Long invoiceId) {
        if (invoiceId == null) {
            throw new IllegalArgumentException("invoiceId must not be null.");
        }

        Invoice invoice = findById(invoiceId);
        if (invoice == null) {
            throw new IllegalArgumentException("invoice does not exist.");
        }
        if (InvoiceStatus.APPROVED == invoice.getStatus()) {
            throw new IllegalStateException("invoice is already approved.");
        }

        invoice.setStatus(InvoiceStatus.APPROVED);
        return invoice;
    }

    public Invoice findById(Long invoiceId) {
        if (invoiceId == null) {
            return null;
        }
        return invoices.get(invoiceId);
    }

    public void clear() {
        invoices.clear();
        sequence = 1L;
    }

    private void validateTitle(String title) {
        if (title == null || title.length() == 0) {
            throw new IllegalArgumentException("title must not be empty.");
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("amount must not be null.");
        }
        if (BigDecimal.ZERO.compareTo(amount) >= 0) {
            throw new IllegalArgumentException("amount must be greater than zero.");
        }
    }
}

package org.seasar.sastruts.example.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.seasar.sastruts.example.entity.Invoice;
import org.seasar.sastruts.example.entity.InvoiceStatus;

public class InvoiceStore {

    private final Map<Long, Invoice> invoices = new LinkedHashMap<Long, Invoice>();

    private long sequence = 1L;

    public Invoice save(String title, BigDecimal amount) {
        Invoice invoice = new Invoice();
        invoice.setId(Long.valueOf(sequence++));
        invoice.setTitle(title);
        invoice.setAmount(amount);
        invoice.setStatus(InvoiceStatus.UNAPPROVED);
        invoices.put(invoice.getId(), invoice);
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
}

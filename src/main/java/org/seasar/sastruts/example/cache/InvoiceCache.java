package org.seasar.sastruts.example.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import org.seasar.sastruts.example.entity.DbScenarioInvoice;

/**
 * DbScenarioInvoiceをinvoiceId単位で保持する検証用キャッシュ。
 * static Mapは使わず、S2管理コンポーネントとしてテストごとにclearできる形にする。
 */
public class InvoiceCache {

    private final Map<Long, DbScenarioInvoice> invoices =
            new LinkedHashMap<Long, DbScenarioInvoice>();

    public DbScenarioInvoice get(Long invoiceId) {
        return copy(invoices.get(invoiceId));
    }

    public void put(DbScenarioInvoice invoice) {
        if (invoice == null || invoice.getId() == null) {
            return;
        }
        invoices.put(invoice.getId(), copy(invoice));
    }

    public void evict(Long invoiceId) {
        invoices.remove(invoiceId);
    }

    public void clear() {
        invoices.clear();
    }

    public boolean contains(Long invoiceId) {
        return invoices.containsKey(invoiceId);
    }

    public int size() {
        return invoices.size();
    }

    private DbScenarioInvoice copy(DbScenarioInvoice source) {
        if (source == null) {
            return null;
        }
        DbScenarioInvoice invoice = new DbScenarioInvoice();
        invoice.setId(source.getId());
        invoice.setCustomerId(source.getCustomerId());
        invoice.setDepartmentId(source.getDepartmentId());
        invoice.setTitle(source.getTitle());
        invoice.setAmount(source.getAmount());
        invoice.setStatus(source.getStatus());
        return invoice;
    }
}

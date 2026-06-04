package org.seasar.sastruts.example.service;

import org.seasar.sastruts.example.cache.InvoiceCache;
import org.seasar.sastruts.example.dao.DbScenarioInvoiceDao;
import org.seasar.sastruts.example.entity.DbScenarioInvoice;

/**
 * DbScenarioInvoiceの参照結果をInvoiceCacheに保存するService。
 * 初回はDaoから取得し、2回目以降はキャッシュから返す。
 */
public class InvoiceCachedReferenceService {

    public DbScenarioInvoiceDao dbScenarioInvoiceDao;

    public InvoiceCache invoiceCache;

    public DbScenarioInvoice findById(Long invoiceId) {
        if (invoiceId == null) {
            return null;
        }

        DbScenarioInvoice cached = invoiceCache.get(invoiceId);
        if (cached != null) {
            return cached;
        }

        DbScenarioInvoice invoice = dbScenarioInvoiceDao.findById(invoiceId);
        if (invoice != null) {
            invoiceCache.put(invoice);
        }
        return invoice;
    }
}

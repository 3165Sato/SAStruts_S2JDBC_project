package org.seasar.sastruts.example.cache;

import java.util.List;

import org.seasar.sastruts.example.dao.DbScenarioInvoiceDao;
import org.seasar.sastruts.example.entity.DbScenarioInvoice;

/**
 * H2 DB上の請求書データをInvoiceCacheへロードする検証用コンポーネント。
 * DB投入後に明示的に呼ぶことで、Excel方式におけるH2からCacheへの初期化フローを再現する。
 */
public class InvoiceCacheLoader {

    public DbScenarioInvoiceDao dbScenarioInvoiceDao;

    public InvoiceCache invoiceCache;

    public InvoiceCacheLoader() {
    }

    public InvoiceCacheLoader(DbScenarioInvoiceDao dbScenarioInvoiceDao,
            InvoiceCache invoiceCache) {
        this.dbScenarioInvoiceDao = dbScenarioInvoiceDao;
        this.invoiceCache = invoiceCache;
    }

    public void load() {
        List<DbScenarioInvoice> invoices = dbScenarioInvoiceDao.findAll();
        for (int i = 0; i < invoices.size(); i++) {
            invoiceCache.put(invoices.get(i));
        }
        invoiceCache.activate();
    }

    public void reload() {
        invoiceCache.clear();
        invoiceCache.deactivate();
        load();
    }
}

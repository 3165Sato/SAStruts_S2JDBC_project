package org.seasar.sastruts.example.service;

import org.seasar.sastruts.example.cache.InvoiceCache;
import org.seasar.sastruts.example.entity.DbScenarioInvoice;

/**
 * DBではなくInvoiceCacheを参照して支払確定可否を判定するサンプルService。
 * 速度重視ロジックが事前ロード済みキャッシュを参照する想定を表現する。
 */
public class InvoiceCachedDecisionService {

    public InvoiceCache invoiceCache;

    public boolean canConfirmPayment(Long invoiceId) {
        DbScenarioInvoice invoice = invoiceCache.get(invoiceId);
        return invoice != null && "APPROVED".equals(invoice.getStatus());
    }
}

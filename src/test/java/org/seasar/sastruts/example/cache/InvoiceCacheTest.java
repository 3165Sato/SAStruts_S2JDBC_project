package org.seasar.sastruts.example.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.PostBindFields;
import org.seasar.sastruts.example.entity.DbScenarioInvoice;
import org.seasar.sastruts.example.testsupport.DbScenarioInvoiceTestDataBuilder;

/**
 * InvoiceCacheの基本挙動を検証するS2JUnit4テスト。
 * DBやServiceの業務フローは扱わず、S2コンテナからDIしたキャッシュをテストごとに初期化する。
 */
@RunWith(Seasar2.class)
public class InvoiceCacheTest {

    public InvoiceCache invoiceCache;

    @PostBindFields
    public void setUp() {
        assertNotNull(invoiceCache);
        invoiceCache.clear();
    }

    // putした請求書をgetでき、件数とcontainsで存在を確認できる。
    @Test
    public void testPutAndGetInvoice() {
        DbScenarioInvoice invoice = createInvoice(Long.valueOf(1L), "APPROVED");

        invoiceCache.put(invoice);

        DbScenarioInvoice cached = invoiceCache.get(Long.valueOf(1L));
        assertNotNull(cached);
        assertEquals("APPROVED", cached.getStatus());
        assertTrue(invoiceCache.contains(Long.valueOf(1L)));
        assertEquals(1, invoiceCache.size());
    }

    // evictすると対象IDの請求書だけを取得できなくなる。
    @Test
    public void testEvictInvoice() {
        invoiceCache.put(createInvoice(Long.valueOf(1L), "APPROVED"));

        invoiceCache.evict(Long.valueOf(1L));

        assertNull(invoiceCache.get(Long.valueOf(1L)));
        assertFalse(invoiceCache.contains(Long.valueOf(1L)));
        assertEquals(0, invoiceCache.size());
    }

    // clearするとキャッシュ済み請求書がすべて削除される。
    @Test
    public void testClearInvoices() {
        invoiceCache.put(createInvoice(Long.valueOf(1L), "APPROVED"));
        invoiceCache.put(createInvoice(Long.valueOf(2L), "REJECTED"));

        invoiceCache.clear();

        assertEquals(0, invoiceCache.size());
        assertFalse(invoiceCache.contains(Long.valueOf(1L)));
        assertFalse(invoiceCache.contains(Long.valueOf(2L)));
    }

    // getしたEntityを変更しても、キャッシュ内部の状態が直接書き換わらないことを確認する。
    @Test
    public void testGetReturnsCopiedInvoice() {
        invoiceCache.put(createInvoice(Long.valueOf(1L), "APPROVED"));

        DbScenarioInvoice cached = invoiceCache.get(Long.valueOf(1L));
        cached.setStatus("REJECTED");

        assertEquals("APPROVED", invoiceCache.get(Long.valueOf(1L)).getStatus());
    }

    private DbScenarioInvoice createInvoice(Long id, String status) {
        return DbScenarioInvoiceTestDataBuilder.invoice(
                id,
                Long.valueOf(10L),
                Long.valueOf(20L),
                "test invoice",
                BigDecimal.valueOf(1000L),
                status);
    }
}

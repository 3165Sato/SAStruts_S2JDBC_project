package org.seasar.sastruts.example.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.seasar.sastruts.example.entity.Invoice;
import org.seasar.sastruts.example.entity.InvoiceStatus;

/**
 * InvoiceStoreのインメモリ保存責務を検証するS2JUnit4テスト。
 * Store相当レイヤーを対象にし、保存・ID採番・検索・初期化が正しく動くことを確認する。
 * DBは使わず、ServiceやLogicから切り出したデータ保持部分の振る舞いを検証する。
 */
@RunWith(Seasar2.class)
public class InvoiceStoreTest {

    private final InvoiceStore invoiceStore = new InvoiceStore();

    // 請求書を保存でき、初期値と検索結果が正しいことを確認する。
    @Test
    public void testSave() {
        Invoice invoice = invoiceStore.save("test invoice", BigDecimal.valueOf(1000L));

        assertNotNull(invoice.getId());
        assertEquals("test invoice", invoice.getTitle());
        assertEquals(BigDecimal.valueOf(1000L), invoice.getAmount());
        assertEquals(InvoiceStatus.UNAPPROVED, invoice.getStatus());
        assertEquals(invoice, invoiceStore.findById(invoice.getId()));
    }

    // 複数の請求書を保存した場合にIDが重複しないことを確認する。
    @Test
    public void testSaveMultipleInvoicesIdIsUnique() {
        Invoice first = invoiceStore.save("first invoice", BigDecimal.valueOf(1000L));
        Invoice second = invoiceStore.save("second invoice", BigDecimal.valueOf(2000L));

        assertFalse(first.getId().equals(second.getId()));
    }

    // 請求書IDがnullの場合に検索結果がnullになることを確認する。
    @Test
    public void testFindByIdNull() {
        assertNull(invoiceStore.findById(null));
    }

    // 存在しない請求書IDの場合に検索結果がnullになることを確認する。
    @Test
    public void testFindByIdNotFound() {
        assertNull(invoiceStore.findById(Long.valueOf(999L)));
    }

    // 初期化後に保存済みの請求書が検索できなくなることを確認する。
    @Test
    public void testClear() {
        Invoice invoice = invoiceStore.save("test invoice", BigDecimal.valueOf(1000L));

        invoiceStore.clear();

        assertNull(invoiceStore.findById(invoice.getId()));
    }
}

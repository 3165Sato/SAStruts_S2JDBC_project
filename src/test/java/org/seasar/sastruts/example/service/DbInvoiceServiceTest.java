package org.seasar.sastruts.example.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.PostBindFields;
import org.seasar.sastruts.example.entity.DbInvoice;
import org.seasar.sastruts.example.testsupport.DbInvoiceTestDataBuilder;

@RunWith(Seasar2.class)
public class DbInvoiceServiceTest {

    public DbInvoiceService dbInvoiceService;

    public JdbcManager jdbcManager;

    @PostBindFields
    public void setUp() {
        assertNotNull(dbInvoiceService);
        assertNotNull(jdbcManager);
        dropTable();
        createTable();
    }

    // S2JDBCを使ってDbInvoiceをDBに登録できることを確認する。
    @Test
    public void testInsert() {
        DbInvoice invoice = DbInvoiceTestDataBuilder.unapprovedInvoice(Long.valueOf(1L));

        int updated = dbInvoiceService.insert(invoice);

        assertEquals(1, updated);
        assertEquals(1L, dbInvoiceService.count());
    }

    // 登録したDbInvoiceをIDで取得できることを確認する。
    @Test
    public void testFindById() {
        DbInvoice invoice = DbInvoiceTestDataBuilder.unapprovedInvoice(Long.valueOf(1L));
        dbInvoiceService.insert(invoice);

        DbInvoice found = dbInvoiceService.findById(Long.valueOf(1L));

        assertNotNull(found);
        assertEquals(Long.valueOf(1L), found.getId());
        assertEquals("test invoice", found.getTitle());
        assertEquals(0, BigDecimal.valueOf(1000L).compareTo(found.getAmount()));
        assertEquals("UNAPPROVED", found.getStatus());
    }

    // updateStatusでDbInvoiceのステータスを更新できることを確認する。
    @Test
    public void testUpdateStatus() {
        DbInvoice invoice = DbInvoiceTestDataBuilder.unapprovedInvoice(Long.valueOf(1L));
        dbInvoiceService.insert(invoice);

        int updated = dbInvoiceService.updateStatus(Long.valueOf(1L), "APPROVED");
        DbInvoice found = dbInvoiceService.findById(Long.valueOf(1L));

        assertEquals(1, updated);
        assertEquals("APPROVED", found.getStatus());
    }

    // countで登録件数を取得できることを確認する。
    @Test
    public void testCount() {
        dbInvoiceService.insert(DbInvoiceTestDataBuilder.invoice(
                Long.valueOf(1L), "first invoice", BigDecimal.valueOf(1000L), "UNAPPROVED"));
        dbInvoiceService.insert(DbInvoiceTestDataBuilder.invoice(
                Long.valueOf(2L), "second invoice", BigDecimal.valueOf(2000L), "APPROVED"));

        assertEquals(2L, dbInvoiceService.count());
    }

    // 存在しないIDの場合にfindByIdがnullを返すことを確認する。
    @Test
    public void testFindByIdNotFound() {
        assertNull(dbInvoiceService.findById(Long.valueOf(999L)));
    }

    private void dropTable() {
        try {
            jdbcManager.updateBySql("drop table DB_INVOICE").execute();
        } catch (RuntimeException e) {
            // H2 1.0.69でIF EXISTSに依存しないため、初回だけ例外を無視する。
        }
    }

    private void createTable() {
        jdbcManager.updateBySql(
                "create table DB_INVOICE ("
                        + "ID bigint not null primary key, "
                        + "TITLE varchar(255) not null, "
                        + "AMOUNT decimal(19, 2) not null, "
                        + "STATUS varchar(32) not null"
                        + ")").execute();
    }

}

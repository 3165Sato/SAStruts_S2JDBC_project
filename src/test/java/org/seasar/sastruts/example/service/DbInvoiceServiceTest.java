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
import org.seasar.sastruts.example.testsupport.DbInvoiceFixture;
import org.seasar.sastruts.example.testsupport.DbInvoiceTestDataBuilder;
import org.seasar.sastruts.example.testsupport.SqlTestSupport;

/**
 * DbInvoiceServiceのDBアクセス処理を検証するS2JUnit4テスト。
 * Service層とS2JDBC / JdbcManagerを対象にし、H2インメモリDBで登録・検索・更新・件数取得を確認する。
 * Excelテストデータには依存せず、Builder / Fixture / SQLファイル方式を組み合わせて前提データを準備する。
 */
@RunWith(Seasar2.class)
public class DbInvoiceServiceTest {

    public DbInvoiceService dbInvoiceService;

    public JdbcManager jdbcManager;

    private DbInvoiceFixture dbInvoiceFixture;

    private SqlTestSupport sqlTestSupport;

    @PostBindFields
    public void setUp() {
        assertNotNull(dbInvoiceService);
        assertNotNull(jdbcManager);
        dbInvoiceFixture = new DbInvoiceFixture(dbInvoiceService);
        sqlTestSupport = new SqlTestSupport(jdbcManager);
        dropTable();
        createTable();
    }

    // Builderで作成した未承認請求書を前提に、S2JDBCでDB登録できることと件数が増えることを確認する。
    @Test
    public void testInsert() {
        DbInvoice invoice = DbInvoiceTestDataBuilder.unapprovedInvoice(Long.valueOf(1L));

        int updated = dbInvoiceService.insert(invoice);

        assertEquals(1, updated);
        assertEquals(1L, dbInvoiceService.count());
    }

    // FixtureでDB登録済みの請求書を準備し、findByIdで同じ内容を取得できることを確認する。
    @Test
    public void testFindById() {
        dbInvoiceFixture.insertUnapprovedInvoice(Long.valueOf(1L));

        DbInvoice found = dbInvoiceService.findById(Long.valueOf(1L));

        assertNotNull(found);
        assertEquals(Long.valueOf(1L), found.getId());
        assertEquals("test invoice", found.getTitle());
        assertEquals(0, BigDecimal.valueOf(1000L).compareTo(found.getAmount()));
        assertEquals("UNAPPROVED", found.getStatus());
    }

    // 未承認請求書がDBにある前提で、updateStatusによりステータスだけを更新できることを確認する。
    @Test
    public void testUpdateStatus() {
        dbInvoiceFixture.insertUnapprovedInvoice(Long.valueOf(1L));

        int updated = dbInvoiceService.updateStatus(Long.valueOf(1L), "APPROVED");
        DbInvoice found = dbInvoiceService.findById(Long.valueOf(1L));

        assertEquals(1, updated);
        assertEquals("APPROVED", found.getStatus());
    }

    // 複数件の請求書をFixtureで登録し、countでDB上の登録件数を取得できることを確認する。
    @Test
    public void testCount() {
        dbInvoiceFixture.insertInvoice(DbInvoiceTestDataBuilder.invoice(
                Long.valueOf(1L), "first invoice", BigDecimal.valueOf(1000L), "UNAPPROVED"));
        dbInvoiceFixture.insertInvoice(DbInvoiceTestDataBuilder.invoice(
                Long.valueOf(2L), "second invoice", BigDecimal.valueOf(2000L), "APPROVED"));

        assertEquals(2L, dbInvoiceService.count());
    }

    // DBに対象IDが存在しない前提で、findByIdがnullを返す仕様であることを確認する。
    @Test
    public void testFindByIdNotFound() {
        assertNull(dbInvoiceService.findById(Long.valueOf(999L)));
    }

    // SQLファイルでDDLと初期データを投入し、固定データをfindByIdで取得できることを確認する。
    @Test
    public void testFindByIdUsingSqlFileData() {
        loadDbInvoiceSqlFiles();

        DbInvoice found = dbInvoiceService.findById(Long.valueOf(1L));

        assertNotNull(found);
        assertEquals(Long.valueOf(1L), found.getId());
        assertEquals("sql unapproved invoice", found.getTitle());
        assertEquals(0, BigDecimal.valueOf(1000L).compareTo(found.getAmount()));
        assertEquals("UNAPPROVED", found.getStatus());
    }

    // SQLファイルで投入した固定初期データの件数をcountで確認し、SQL方式の準備結果を検証する。
    @Test
    public void testCountUsingSqlFileData() {
        loadDbInvoiceSqlFiles();

        assertEquals(4L, dbInvoiceService.count());
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

    private void loadDbInvoiceSqlFiles() {
        sqlTestSupport.executeSqlFile("sql/db_invoice_schema.sql");
        sqlTestSupport.executeSqlFile("sql/db_invoice_test_data.sql");
    }

}

package org.seasar.sastruts.example.service;

import java.util.List;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.sastruts.example.entity.DbInvoice;

public class DbInvoiceService {

    public JdbcManager jdbcManager;

    public int insert(DbInvoice invoice) {
        return jdbcManager.insert(invoice).execute();
    }

    public DbInvoice findById(Long id) {
        List<DbInvoice> invoices = jdbcManager.selectBySql(
                DbInvoice.class,
                "select ID, TITLE, AMOUNT, STATUS from DB_INVOICE where ID = ?",
                id).getResultList();
        if (invoices.isEmpty()) {
            return null;
        }
        return invoices.get(0);
    }

    public int updateStatus(Long id, String status) {
        return jdbcManager.updateBySql(
                "update DB_INVOICE set STATUS = ? where ID = ?",
                String.class,
                Long.class).params(status, id).execute();
    }

    public long count() {
        return jdbcManager.getCountBySql("select * from DB_INVOICE");
    }
}

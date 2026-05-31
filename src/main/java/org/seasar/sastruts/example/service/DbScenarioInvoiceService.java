package org.seasar.sastruts.example.service;

import java.util.List;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.sastruts.example.entity.DbScenarioInvoice;

public class DbScenarioInvoiceService {

    public JdbcManager jdbcManager;

    public int insert(DbScenarioInvoice invoice) {
        return jdbcManager.insert(invoice).execute();
    }

    public DbScenarioInvoice findById(Long id) {
        List<DbScenarioInvoice> invoices = jdbcManager.selectBySql(
                DbScenarioInvoice.class,
                "select ID, CUSTOMER_ID, DEPARTMENT_ID, TITLE, AMOUNT, STATUS "
                        + "from DB_SCENARIO_INVOICE where ID = ?",
                id).getResultList();
        if (invoices.isEmpty()) {
            return null;
        }
        return invoices.get(0);
    }

    public long count() {
        return jdbcManager.getCountBySql("select * from DB_SCENARIO_INVOICE");
    }
}

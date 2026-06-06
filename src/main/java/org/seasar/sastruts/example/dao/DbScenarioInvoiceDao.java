package org.seasar.sastruts.example.dao;

import java.util.List;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.sastruts.example.entity.DbScenarioInvoice;

public class DbScenarioInvoiceDao {

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

    public List<DbScenarioInvoice> findAll() {
        return jdbcManager.selectBySql(
                DbScenarioInvoice.class,
                "select ID, CUSTOMER_ID, DEPARTMENT_ID, TITLE, AMOUNT, STATUS "
                        + "from DB_SCENARIO_INVOICE order by ID").getResultList();
    }

    public int updateStatus(Long id, String status) {
        return jdbcManager.updateBySql(
                "update DB_SCENARIO_INVOICE set STATUS = ? where ID = ?",
                String.class,
                Long.class).params(status, id).execute();
    }

    public long count() {
        return jdbcManager.getCountBySql("select * from DB_SCENARIO_INVOICE");
    }
}

package org.seasar.sastruts.example.dao;

import java.util.List;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.sastruts.example.entity.DbCustomer;

public class DbCustomerDao {

    public JdbcManager jdbcManager;

    public int insert(DbCustomer customer) {
        return jdbcManager.insert(customer).execute();
    }

    public DbCustomer findById(Long id) {
        List<DbCustomer> customers = jdbcManager.selectBySql(
                DbCustomer.class,
                "select ID, CUSTOMER_NAME from DB_CUSTOMER where ID = ?",
                id).getResultList();
        if (customers.isEmpty()) {
            return null;
        }
        return customers.get(0);
    }

    public long count() {
        return jdbcManager.getCountBySql("select * from DB_CUSTOMER");
    }
}

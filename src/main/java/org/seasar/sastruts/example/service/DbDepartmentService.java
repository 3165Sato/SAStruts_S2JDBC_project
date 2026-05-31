package org.seasar.sastruts.example.service;

import java.util.List;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.sastruts.example.entity.DbDepartment;

public class DbDepartmentService {

    public JdbcManager jdbcManager;

    public int insert(DbDepartment department) {
        return jdbcManager.insert(department).execute();
    }

    public DbDepartment findById(Long id) {
        List<DbDepartment> departments = jdbcManager.selectBySql(
                DbDepartment.class,
                "select ID, DEPARTMENT_NAME from DB_DEPARTMENT where ID = ?",
                id).getResultList();
        if (departments.isEmpty()) {
            return null;
        }
        return departments.get(0);
    }

    public long count() {
        return jdbcManager.getCountBySql("select * from DB_DEPARTMENT");
    }
}

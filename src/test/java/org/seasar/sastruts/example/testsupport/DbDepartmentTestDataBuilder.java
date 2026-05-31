package org.seasar.sastruts.example.testsupport;

import org.seasar.sastruts.example.entity.DbDepartment;

public final class DbDepartmentTestDataBuilder {

    private DbDepartmentTestDataBuilder() {
    }

    public static DbDepartment department(Long id) {
        return department(id, "test department");
    }

    public static DbDepartment department(Long id, String departmentName) {
        DbDepartment department = new DbDepartment();
        department.setId(id);
        department.setDepartmentName(departmentName);
        return department;
    }
}

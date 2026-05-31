package org.seasar.sastruts.example.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DB_DEPARTMENT")
public class DbDepartment {

    @Id
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "DEPARTMENT_NAME", nullable = false)
    private String departmentName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}

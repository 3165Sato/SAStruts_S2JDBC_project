package org.seasar.sastruts.example.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DB_APPROVAL_HISTORY")
public class DbApprovalHistory {

    @Id
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "INVOICE_ID", nullable = false)
    private Long invoiceId;

    @Column(name = "APPROVER_NAME", nullable = false)
    private String approverName;

    @Column(name = "APPROVED_AT", nullable = false)
    private Timestamp approvedAt;

    @Column(name = "STATUS", nullable = false)
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public Timestamp getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Timestamp approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

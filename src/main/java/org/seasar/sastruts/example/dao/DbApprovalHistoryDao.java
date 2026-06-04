package org.seasar.sastruts.example.dao;

import java.util.List;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.sastruts.example.entity.DbApprovalHistory;

public class DbApprovalHistoryDao {

    public JdbcManager jdbcManager;

    public int insert(DbApprovalHistory history) {
        return jdbcManager.insert(history).execute();
    }

    public DbApprovalHistory findById(Long id) {
        List<DbApprovalHistory> histories = jdbcManager.selectBySql(
                DbApprovalHistory.class,
                "select ID, INVOICE_ID, APPROVER_NAME, APPROVED_AT, STATUS "
                        + "from DB_APPROVAL_HISTORY where ID = ?",
                id).getResultList();
        if (histories.isEmpty()) {
            return null;
        }
        return histories.get(0);
    }

    public List<DbApprovalHistory> findByInvoiceId(Long invoiceId) {
        return jdbcManager.selectBySql(
                DbApprovalHistory.class,
                "select ID, INVOICE_ID, APPROVER_NAME, APPROVED_AT, STATUS "
                        + "from DB_APPROVAL_HISTORY where INVOICE_ID = ? order by ID",
                invoiceId).getResultList();
    }

    public long count() {
        return jdbcManager.getCountBySql("select * from DB_APPROVAL_HISTORY");
    }
}

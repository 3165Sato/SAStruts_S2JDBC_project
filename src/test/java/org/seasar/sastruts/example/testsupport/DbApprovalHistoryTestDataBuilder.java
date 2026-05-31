package org.seasar.sastruts.example.testsupport;

import java.sql.Timestamp;

import org.seasar.sastruts.example.entity.DbApprovalHistory;

/**
 * DbApprovalHistoryのテストデータを作成するBuilder。
 * 承認済み・差戻し済みなど、請求書に紐づく履歴Entityを作るだけでDB登録は行わない。
 */
public final class DbApprovalHistoryTestDataBuilder {

    private static final Timestamp DEFAULT_APPROVED_AT = Timestamp.valueOf("2026-01-01 10:00:00");

    private DbApprovalHistoryTestDataBuilder() {
    }

    public static DbApprovalHistory approvedHistory(Long id, Long invoiceId) {
        return history(id, invoiceId, "test approver", DEFAULT_APPROVED_AT, "APPROVED");
    }

    public static DbApprovalHistory rejectedHistory(Long id, Long invoiceId) {
        return history(id, invoiceId, "test approver", DEFAULT_APPROVED_AT, "REJECTED");
    }

    public static DbApprovalHistory history(Long id, Long invoiceId,
            String approverName, Timestamp approvedAt, String status) {
        DbApprovalHistory history = new DbApprovalHistory();
        history.setId(id);
        history.setInvoiceId(invoiceId);
        history.setApproverName(approverName);
        history.setApprovedAt(approvedAt);
        history.setStatus(status);
        return history;
    }
}

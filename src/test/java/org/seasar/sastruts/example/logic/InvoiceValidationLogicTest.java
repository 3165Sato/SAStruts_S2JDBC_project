package org.seasar.sastruts.example.logic;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.seasar.sastruts.example.entity.Invoice;

/**
 * InvoiceValidationLogicの入力チェックを検証するLogic層テスト。
 * DB副作用やService全体の業務フローは扱わず、入力値の境界条件を確認する。
 */
@RunWith(Seasar2.class)
public class InvoiceValidationLogicTest {

    public InvoiceValidationLogic invoiceValidationLogic;

    // 正常なタイトルが入力チェックを通過することを確認する。
    @Test
    public void testValidateTitle() {
        invoiceValidationLogic.validateTitle("test invoice");
    }

    // 半角スペースのみのタイトルは入力チェックでエラーになることを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testValidateTitleOnlySpaces() {
        invoiceValidationLogic.validateTitle("   ");
    }

    // 1円以上の金額が入力チェックを通過することを確認する。
    @Test
    public void testValidateAmount() {
        invoiceValidationLogic.validateAmount(BigDecimal.ONE);
    }

    // 0円の金額は入力チェックでエラーになることを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testValidateAmountZero() {
        invoiceValidationLogic.validateAmount(BigDecimal.ZERO);
    }

    // 請求書IDがnullの場合に入力チェックでエラーになることを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testValidateInvoiceIdNull() {
        invoiceValidationLogic.validateInvoiceId(null);
    }

    // 請求書が存在する場合は存在チェックを通過することを確認する。
    @Test
    public void testValidateInvoiceExists() {
        Invoice invoice = new Invoice();

        assertEquals(invoice, invoiceValidationLogic.validateInvoiceExists(invoice));
    }

    // 請求書が存在しない場合は存在チェックでエラーになることを確認する。
    @Test(expected = IllegalArgumentException.class)
    public void testValidateInvoiceExistsNull() {
        invoiceValidationLogic.validateInvoiceExists(null);
    }
}

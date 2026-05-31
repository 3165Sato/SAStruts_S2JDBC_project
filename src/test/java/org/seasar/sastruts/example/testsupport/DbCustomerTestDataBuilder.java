package org.seasar.sastruts.example.testsupport;

import org.seasar.sastruts.example.entity.DbCustomer;

/**
 * DbCustomerのテストデータを作成するBuilder。
 * 顧客Entityを作るだけで、DB登録はScenario FixtureやServiceに任せる。
 */
public final class DbCustomerTestDataBuilder {

    private DbCustomerTestDataBuilder() {
    }

    public static DbCustomer customer(Long id) {
        return customer(id, "test customer");
    }

    public static DbCustomer customer(Long id, String customerName) {
        DbCustomer customer = new DbCustomer();
        customer.setId(id);
        customer.setCustomerName(customerName);
        return customer;
    }
}

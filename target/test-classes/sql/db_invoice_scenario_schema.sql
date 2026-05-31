create table DB_CUSTOMER (
    ID bigint not null primary key,
    CUSTOMER_NAME varchar(255) not null
);

create table DB_DEPARTMENT (
    ID bigint not null primary key,
    DEPARTMENT_NAME varchar(255) not null
);

create table DB_SCENARIO_INVOICE (
    ID bigint not null primary key,
    CUSTOMER_ID bigint not null,
    DEPARTMENT_ID bigint not null,
    TITLE varchar(255) not null,
    AMOUNT decimal(19, 2) not null,
    STATUS varchar(32) not null
);

create table DB_APPROVAL_HISTORY (
    ID bigint not null primary key,
    INVOICE_ID bigint not null,
    APPROVER_NAME varchar(255) not null,
    APPROVED_AT timestamp not null,
    STATUS varchar(32) not null
);

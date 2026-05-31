drop table DB_INVOICE;

create table DB_INVOICE (
    ID bigint not null primary key,
    TITLE varchar(255) not null,
    AMOUNT decimal(19, 2) not null,
    STATUS varchar(32) not null
);

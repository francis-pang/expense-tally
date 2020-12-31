-- This SQL script contains the scheme for all the tables in the database

CREATE TABLE expense_report
(
    id               INTEGER PRIMARY KEY, -- row identifier
    amount           DECIMAL(5,3),        -- expensed amount
    category         VARCHAR(255),        -- category TODO: To put into enum
    subcategory      VARCHAR(255),        -- L2 category TODO: To put into enum
    payment_method   VARCHAR(255),        -- Payment method TODO: To put into enum
    description      VARCHAR(255),
    expensed         DATETIME,            -- Transaction time
    modified         DATETIME,            -- Last modified time of row
    reference_number VARCHAR(255),        -- Amount shown in the bank statement or receipt, without considering the
                                          -- split cost
    property2        VARCHAR(255),        -- name of the picture, normally refers to the receipt
    property5        VARCHAR(255)         -- Split amount
)
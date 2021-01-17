-- This SQL script contains the scheme for all the tables in the database

CREATE TABLE expense_manager
(
    id               INTEGER PRIMARY KEY,    -- row identifier
    amount           DECIMAL(8, 3) NOT NULL, -- expensed amount
    category         VARCHAR(255) NOT NULL,  -- category TODO: To put into enum
    subcategory      VARCHAR(255) NOT NULL,  -- L2 category TODO: To put into enum
    payment_method   VARCHAR(255) NOT NULL,  -- Payment method TODO: To put into enum
    description      VARCHAR(512) NOT NULL,
    expensed_time    DATETIME NOT NULL,      -- Transaction time
    reference_amount DECIMAL(8, 3)           -- Amount shown in the bank statement or receipt, without considering the
                                             -- split cost
)
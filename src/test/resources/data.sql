-- Recipients table
insert into recipients (first_name, last_name, routing_number, national_id_number, account_number, bank_name)
values ('UserName', 'UserSureName', '999999999', '010101010', '2020202020', 'Test Bank');


-- Transactions table
insert into transactions (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS, associated_transaction_id, USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 1000.00,  100.00, 900.00, null, 1, 1, 'Bank transfer to your account', 'COMPLETED', '2023-09-04 15:26:15');

insert into transactions (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS, USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 120.00,  12.00, 108.00, 1, 1, 'Bank transfer to your account', 'IN_PROGRESS', '2023-07-02 10:26:15');

insert into transactions (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS,  associated_transaction_id,USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 200.00,  20.00, 180.00, 2, 1, 1, 'Bank transfer to your account', 'FAILED','2023-08-14 15:26:15');

insert into transactions (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS, refunded_amount, associated_transaction_id,USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 0.00,  0.00, 0.00, 200.00, 3,  1, 1, 'Refund to your wallet', 'REFUNDED', '2023-08-14 16:26:15');

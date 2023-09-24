-- Recipients table
insert into recipientEntities (first_name, last_name, routing_number, national_id_number, account_number, bank_name)
values ('Jemimah', 'MacShane', '326047655', '094375570', '2792399946', 'Ledner and Sons Bank');


-- Transactions table
insert into transactionEntities (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS, associated_transaction_id, USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 1000.00,  100.00, 900.00, null, 1, 1, 'Bank transfer to your account', 'COMPLETED', '2023-09-04T15:26:15');


insert into transactionEntities (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS, USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 120.00,  12.00, 108.00, 1, 1, 'Bank transfer to your account', 'IN_PROGRESS', '2023-07-02T10:26:15');

insert into transactionEntities (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS,  associated_transaction_id,USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 200.00,  20.00, 180.00, 2, 1, 1, 'Bank transfer to your account', 'FAILED','2023-08-14T15:26:15');

insert into transactionEntities (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS, refunded_amount, associated_transaction_id,USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 0.00,  0.00, 0.00, 200.00, 3,  1, 1, 'Refund to your wallet', 'REFUNDED', '2023-08-14T16:26:15');


insert into transactionEntities (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS, USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 2000.00,  200.00, 1800.00, 1, 1, 'Bank transfer to your account', 'COMPLETED', '2022-09-05T15:26:15');

insert into transactionEntities (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS,USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 220.00,  22.00, 198.00, 1, 1, 'Bank transfer to your account', 'IN_PROGRESS', '2022-08-03T15:26:15');

insert into transactionEntities (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS, associated_transaction_id, USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 100.00,  10.00, 80.00, 6, 1, 1, 'Bank transfer to your account', 'FAILED', '2022-09-04T15:00:15');

insert into transactionEntities (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS, refunded_amount, associated_transaction_id,USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 0.00,  0.00, 0.00, 100.00, 7, 1, 1, 'Refund to your wallet', 'REFUNDED', '2022-09-04T16:26:15');


insert into transactionEntities (ID, AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS, USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 100, 120.00,  12.00, 108.00, 1, 1, 'Bank transfer to your account', 'IN_PROGRESS', '2022-03-02T10:26:15');
insert into transactionEntities (ID, AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS, USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 101, 130.00,  13.00, 117.00, 1, 1, 'Bank transfer to your account', 'IN_PROGRESS', '2021-02-01T10:26:15');
insert into transactionEntities (ID, AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS, USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 102, 140.00,  14.00, 126.00, 1, 1, 'Bank transfer to your account', 'IN_PROGRESS', '2020-01-08T10:26:15');


--
-- Uncomment to insert and to try the Schedule task.
--
-- insert into transactionEntities (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS,  refunded_amount, associated_transaction_id, USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
-- values ( 120.00,  12.00, 108.00, 0.0, 100, 1, 1, 'Bank transfer to your account', 'FAILED_TO_REFUND', '2022-03-02T10:30:15');
-- insert into transactionEntities (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS,  refunded_amount, associated_transaction_id, USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
-- values ( 130.00,  13.00, 117.00, 0.0, 101, 1, 1, 'Bank transfer to your account', 'FAILED_TO_REFUND', '2021-02-01T10:30:15');
-- insert into transactionEntities (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS,  refunded_amount, associated_transaction_id, USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
-- values ( 140.00,  14.00, 126.00, 0.0, 102, 1, 1, 'Bank transfer to your account', 'FAILED_TO_REFUND', '2020-01-08T10:30:15');

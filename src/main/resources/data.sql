-- Recipients table
insert into recipients (first_name, last_name, routing_number, national_id_number, account_number, bank_name)
values ('Jemimah', 'MacShane', '326047655', '094375570', '2792399946', 'Ledner and Sons Bank');


-- Transactions table
insert into transactions (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS, USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 1000.00,  100.00, 900.00, 1, 1, 'Bank transfer to your account', 'COMPLETED', '2023-09-04T15:26:15');

insert into transactions (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS, USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 200.00,  0.00, 0.00, 1, 1, 'Refund to your wallet', 'REFUNDED', '2023-08-14T16:26:15');

insert into transactions (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS,  USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 200.00,  20.00, 180.00, 1, 1, 'Bank transfer to your account', 'FAILED','2023-08-14T15:26:15');

insert into transactions (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS, USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 120.00,  12.00, 108.00, 1, 1, 'Bank transfer to your account', 'IN_PROGRESS', '2023-07-02T10:26:15');

insert into transactions (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS, USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 2000.00,  200.00, 1800.00, 1, 1, 'Bank transfer to your account', 'COMPLETED', '2022-09-05T15:26:15');

insert into transactions (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS, USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 100.00,  0.00, 0.00, 1, 1, 'Refund to your wallet', 'REFUNDED', '2022-09-04T16:26:15');

insert into transactions (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS, USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 100.00,  10.00, 80.00, 1, 1, 'Bank transfer to your account', 'FAILED', '2022-09-04T15:00:15');

insert into transactions (AMOUNT_SENT, TRANSACTION_FEE, RECIPIENT_GETS, USER_ID, RECIPIENT_ID, MESSAGE, STATUS, CREATED_AT)
values ( 220.00,  22.00, 198.00, 1, 1, 'Bank transfer to your account', 'IN_PROGRESS', '2022-08-03T15:26:15');


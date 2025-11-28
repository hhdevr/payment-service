insert into payment (amount, status, description, created_at, updated_at)
values (10.00, 'PENDING', 'Test payment 1', current_timestamp(), current_timestamp());

insert into payment (amount, status, description, created_at, updated_at)
values (20.50, 'COMPLETED', 'Test payment 2', current_timestamp(), current_timestamp());

insert into payment (amount, status, description, created_at, updated_at)
values (30.00, 'REJECTED', 'Test payment 3', current_timestamp(), current_timestamp());

insert into payment (amount, status, description, created_at, updated_at)
values (40.75, 'CANCELED', 'Test payment 4', current_timestamp(), current_timestamp());

insert into payment (amount, status, description, created_at, updated_at)
values (99.99, 'PENDING', 'Test payment 5', current_timestamp(), current_timestamp());

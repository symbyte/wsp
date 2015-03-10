drop table orders;
drop table orderlist;
create table orderlist(
ordernumber integer primary key not null generated always as identity(start with 1, increment by 1),
total double,
username varchar(255)
);

create table orders(
parentorder integer references orderlist(ordernumber) on delete cascade,
isbn integer references book(isbn) on delete cascade,
quantity integer
);

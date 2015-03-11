drop table orders;
drop table orderlist;
create table orderlist(
ordernumber integer primary key not null generated always as identity(start with 1, increment by 1),
total double,
username varchar(255),
orderdate bigint 
);

create table orders(
parentorder integer references orderlist(ordernumber) on delete cascade,
prodid integer references product(prodid) on delete cascade,
quantity integer
);

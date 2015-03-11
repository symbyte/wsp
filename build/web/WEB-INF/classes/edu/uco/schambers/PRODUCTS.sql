create table product(
prodid integer primary key,
prodname varchar(255),
prodprice double,
quantity integer,
prodtype varchar(1),
picture blob

);
create table book(
prodid integer primary key,
pages integer,
author varchar(255) 
);
create table sharkrepellent(
prodid integer primary key,
volume integer,
effectiveness varchar(255)
);
create table jetpack(
prodid integer primary key,
fuel varchar(255),
engineSize varchar(255)
);
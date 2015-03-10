drop table book;
create table book(
isbn integer primary key,
title varchar(100),
author varchar(100),
price float);

insert into book (isbn,title,author,price)
values(12346,'Do Androids Dream of Electric Sheep?','Philip K. Dick',10.00);
insert into book (isbn,title,author,price) 
values (12345,'If This Isn''t Nice, What Is?','Kurt Vonnegut',14.50);
insert into book(isbn,title,author,price)
values(12347,'Leaves of Grass','Walt Whitman',20.00);
insert into book(isbn,title,author,price)
values(12348,'Feed','M. T. Anderson',12.50);



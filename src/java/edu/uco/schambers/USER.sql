drop table USERS;
drop table GROUPTABLE;
create table USERS(
id integer not null primary key generated
always as identity(start with 1, increment by 1),
USERNAME varchar(255),
firstname varchar(20),
lastname varchar(20),
email varchar(50),
PASSWORD varchar(64), 
phonenum varchar(12),
address varchar(255)
);
create table GROUPTABLE 
(
    ID integer not null generated always as identity
                        (start with 1, increment by 1),
    GROUPNAME varchar(255),
    USERNAME varchar(255),
    primary key (ID)
);
insert into users (username, firstname, lastname, email, password,address,phonenum)
values('admin','Joe','Biden','what@where.gov',
'8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918','1212 place Rd. Hoboken, NJ 74718',
'834-135-6732'); /*password = admin*/

insert into grouptable (groupname, username) values('admingroup', 'admin');

insert into users (username, firstname, lastname, email, password,address,phonenum)
values('user','Frank','Reynolds','when@where.com',
'd74ff0ee8da3b9806b18c877dbf29bbde50b5bd8e4dad7a3a725000feb82e8f1','4123 somewhere ln Philly, PA 72645'
,'unlisted');/*password = pass*/
 
insert into grouptable (groupname, username) values('customergroup', 'user');


insert into users (username, firstname, lastname, email, password,address, phonenum)
values('adminUser','Lord','Vader','the@death.star',
'cccf52f3d03c5072ee64a136f74fc56b27e04f15ffe5d1d277d58ab18d94b327','9999 force st Mos Espa,Tatooine 56255-8837267'
,'142-512-6136');/*password = luke*/
 
insert into grouptable (groupname, username) values('customergroup', 'adminUser');
insert into grouptable (groupname, username) values('admingroup', 'adminUser');
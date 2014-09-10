drop table user if exists;
create table user (
id bigint primary key,
username varchar(500),
password varchar(500)
);

drop table st_blog if exists;
create table st_blog (
tdate datetime,
product varchar(50),
game varchar(50),
game_server varchar(50),
click int,
login int,
pay int
);


insert into user values(1,'badqiu','1');
insert into user values(2,'jane','2');
insert into st_blog values('2013-08-10','yygame','ddt','s1',100,50,150);
insert into st_blog values('2013-08-14','yygame','ddt','s1',100,50,30);
insert into st_blog values('2013-08-15','yygame','ddt','s2',110,40,10);
insert into st_blog values('2013-08-15','yygame','ddt','s3',120,20,60);
insert into st_blog values('2013-08-15','client','sxd','s1',10,20,60);
insert into st_blog values('2013-08-16','webgame','dfw','s1',14,24,60);
insert into st_blog values('2013-08-16','webyygame','dfw','s2',84,74,50);
insert into st_blog values('2013-08-16','webyy','dfw','s3',24,34,40);

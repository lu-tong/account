/*!40101 SET NAMES utf8 */;

create database if not exists account default character set utf8mb4 collate utf8mb4_unicode_ci;
grant all on account.* to 'ufutao'@'%';

create table if not exists account.account_record
(
    uid         bigint primary key,
    channel     char(64) not null,
    order_id       char(64) not null,
    action      char(64),
    account     bigint,
    number      bigint default 0,
    status      smallint default 0,
    modify_time        datetime,
    create_time        datetime,
    unique uni_act(account,channel,order_id)
);

create table if not exists account.account
(
    uid   bigint primary key,
    money bigint default 0
);

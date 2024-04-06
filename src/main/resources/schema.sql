#create database REACT_API_JAVA default char set utf8;
use REACT_API_JAVA;

CREATE TABLE companies
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_name    VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status  VARCHAR(255)
);

create table users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL,
    first_name VARCHAR(100),
    last_name  VARCHAR(100),
    email      VARCHAR(100) UNIQUE ,
    password   VARCHAR(100) NOT NULL,
    company_id BIGINT,
    created    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status     VARCHAR(255),
    FOREIGN KEY (company_id) REFERENCES companies (id)
);

create table equipment
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    equipment_name VARCHAR(100) NOT NULL ,
    serial_number  VARCHAR(100) NOT NULL,
    company_id BIGINT,
    created    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status     VARCHAR(255),
    FOREIGN KEY (company_id) REFERENCES companies (id)
);

create table roles
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status  VARCHAR(255)
);

CREATE TABLE users_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (role_id) REFERENCES roles (id)
);



insert into roles (name)
values ('ROLE_USER'),
       ('ROLE_ADMIN');

insert into users (username, password, email)
values ('user', '$2a$04$Fx/SX9.BAvtPlMyIIqqFx.hLY2Xp8nnhpzvEEVINvVpwIPbA3v/.i', 'user@gmail.com'),
       ('admin', '$2a$04$Fx/SX9.BAvtPlMyIIqqFx.hLY2Xp8nnhpzvEEVINvVpwIPbA3v/.i', 'admin@gmail.com');

insert into users_roles (user_id, role_id)
values (1, 1),
       (2, 2);

insert into  companies (company_name, address)
values ('Lider Electric', 'Zaporizzya'),
       ('Metinvest', 'Mariupol');

#ALTER TABLE companies CHANGE name  company_name VARCHAR(255);
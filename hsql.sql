CREATE MEMORY TABLE PUBLIC.USERS(
UNAME VARCHAR(20) NOT NULL,
PASSWORD VARCHAR(200),
APPLICATION VARCHAR(40) NOT NULL,
SINCE BIGINT,
SALT VARCHAR(40),
SECQ1 VARCHAR(100),
SECQ1ANS VARCHAR(40),
SECQ2 VARCHAR(100),
SECQ2ANS VARCHAR(40),
SECQ3 VARCHAR(100),
SECQ3ANS VARCHAR(40),
FIRSTNAME VARCHAR(40),
LASTNAME VARCHAR(60),
EMAIL VARCHAR(80),
PHONE VARCHAR(20),
PICTURE VARBINARY(5000),
EXTENDPROFILE VARCHAR(1000),
CONSTRAINT USERS_PKEY PRIMARY KEY(UNAME,APPLICATION))
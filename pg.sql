CREATE TABLE USERS(
UNAME character varying NOT NULL,
PASSWORD character varying,
APPLICATION character varying NOT NULL,
SINCE BIGINT,
SALT character varying,
SECQ1 character varying,
SECQ1ANS character varying,
SECQ2 character varying,
SECQ2ANS character varying,
SECQ3 character varying,
SECQ3ANS character varying,
FIRSTNAME character varying,
LASTNAME character varying,
EMAIL character varying,
PHONE character varying,
PICTURE BYTEA,
EXTENDPROFILE character varying,
CONSTRAINT USERS_PKEY PRIMARY KEY(UNAME,APPLICATION));

CREATE MEMORY TABLE PUBLIC.MASECAUTHPROVIDER (
ID INTEGER PRIMARY KEY, 
PROVIDERNAME character varying,
PROVIDERTYPE character varying,
APPLICATION character varying,
CONFIGURATION character varying );

CREATE MEMORY TABLE PUBLIC.MASECGROUPS(
GID BIGSERIAL,
GNAME character varying,
APPLICATION character varying,
DESCRIPTION character varying,
CONSTRAINT GROUPS_PKEY PRIMARY KEY(GNAME,APPLICATION));

CREATE MEMORY TABLE PUBLIC.MASECUSERSGROUP(
ID BIGSERIAL,
GNAME character varying,
UNAME character varying,
APPLICATION character varying,
CONSTRAINT USERSGROUPS_PKEY PRIMARY KEY(GNAME,UNAME));
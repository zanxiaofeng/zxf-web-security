CREATE TABLE CUSTOMER (
    ID VARCHAR(40) NOT NULL,
    NAME VARCHAR(100) NOT NULL,
    TITLE VARCHAR(40) NOT NULL,
    PRIMARY KEY PRIMARY_KEY(ID)
);

INSERT INTO CUSTOMER(ID, NAME, TITLE) VALUES ('c-1','Aby','DEV');
INSERT INTO CUSTOMER(ID, NAME, TITLE) VALUES ('c-2','Ben','DEV');
INSERT INTO CUSTOMER(ID, NAME, TITLE) VALUES ('c-3','ABC','QA');
INSERT INTO CUSTOMER(ID, NAME, TITLE) VALUES ('c-4','DDD','QA');
INSERT INTO CUSTOMER(ID, NAME, TITLE) VALUES ('c-5','Tod','QA');
INSERT INTO CUSTOMER(ID, NAME, TITLE) VALUES ('c-6','Joy','QA');
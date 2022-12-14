# Test Security
http://localhost:8084/file/security?fileName=my.txt
http://localhost:8084/file/security?fileName=my.png
http://localhost:8084/file/security?fileName=my.pdf
http://localhost:8084/file/security?fileName=my.docx
http://localhost:8084/file/security?fileName=my.xlsx
http://localhost:8084/file/security?fileName=sub/sub.txt
http://localhost:8084/file/security?fileName=sub/sub/sub.txt
http://localhost:8084/file/security?fileName=../home.txt

# Test Security by Path
http://localhost:8084/file/security/my.txt
http://localhost:8084/file/security/my.png
http://localhost:8084/file/security/my.pdf
http://localhost:8084/file/security/my.docx
http://localhost:8084/file/security/my.xlsx
http://localhost:8084/file/security/sub/sub.txt
http://localhost:8084/file/security/sub/sub/sub.txt

# Test Un-Security
http://localhost:8084/file/un-security?fileName=my.txt
http://localhost:8084/file/un-security?fileName=sub/sub.txt
http://localhost:8084/file/un-security?fileName=../home.txt

# Test Un-Security by Path
http://localhost:8084/file/un-security/my.txt[200 OK]
http://localhost:8084/file/un-security/sub/sub.txt[404 Not Found]
http://localhost:8084/file/un-security/sub%2Fsub.txt[400 Bad Request]
http://localhost:8084/file/un-security/../home.txt[=>http://localhost:8084/file/home.txt 404 Not Found]
http://localhost:8084/file/un-security/..%2Fhome.txt[400 Bad Request]

# Content Type
- txt=>text/html
- png=>text/html
- pdf=>text/html
- xlsx=>text/html
- docx=>text/html
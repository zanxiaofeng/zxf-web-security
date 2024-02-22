# Articles
- [OWASP Unrestricted File Upload](https://owasp.org/www-community/vulnerabilities/Unrestricted_File_Upload)
- [OWASP File Upload Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html)
- [SEI CERT Oracle Coding Standard for Java](https://wiki.sei.cmu.edu/confluence/display/java/SEI+CERT+Oracle+Coding+Standard+for+Java)
- https://github.com/righettod/document-upload-protection


# Multipart support(There are two concrete implementations included in Spring, as of Spring 3.1)
- org.springframework.web.multipart.commons.CommonsMultipartResolver for Apache Commons FileUpload 
- org.springframework.web.multipart.support.StandardServletMultipartResolver for the Servlet 3.0+ Part API

# Key classes for multipart support
- javax.servlet.MultipartConfigElement
- javax.servlet.annotation.MultipartConfig
- org.springframework.web.multipart.MultipartFile
- org.springframework.web.multipart.MultipartResolver
- org.springframework.web.multipart.MultipartRequest
- org.springframework.web.multipart.MultipartHttpServletRequest
- org.springframework.web.multipart.MultipartFileResource
- org.springframework.web.multipart.MultipartException
- org.springframework.web.multipart.MaxUploadSizeExceededException
- org.springframework.web.multipart.support.MultipartFilter
- org.springframework.web.multipart.support.StandardServletMultipartResolver
- org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest
- org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest
- org.springframework.web.multipart.support.StandardMultipartHttpServletRequest
- org.springframework.web.multipart.commons.CommonsMultipartResolver
- org.springframework.boot.autoconfigure.web.servlet.MultipartProperties
- org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration


# Articles
- [OWASP Unrestricted File Upload](https://owasp.org/www-community/vulnerabilities/Unrestricted_File_Upload)
- [OWASP File Upload Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html)
- [SEI CERT Oracle Coding Standard for Java](https://wiki.sei.cmu.edu/confluence/display/java/SEI+CERT+Oracle+Coding+Standard+for+Java)
- https://github.com/righettod/document-upload-protection


# How to test
- open http://localhost:8085/fileUploader


# Multipart support(There are two concrete implementations included in Spring, as of Spring 3.1)
- org.springframework.web.multipart.commons.CommonsMultipartResolver for Apache Commons FileUpload 
- org.springframework.web.multipart.support.StandardServletMultipartResolver for the Servlet 3.0+ Part API


# SpringBoot处理Multipart FileUpload的三层结构
## Java Web Container层（负责接收Request files到内存或文件中，如果接收过程异常中断要负责删除已经上传的临时文件。）
- org.apache.tomcat.util.http.fileupload.FileItem::void delete()
- org.apache.tomcat.util.http.fileupload.FileItemFactory
- org.apache.tomcat.util.http.fileupload.disk.DiskFileItem::void delete()
- org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory
- org.apache.tomcat.util.http.fileupload.FileUploadBase::List<FileItem> parseRequest(RequestContext ctx) throws FileUploadException
- org.apache.tomcat.util.http.fileupload.FileUpload
- Clean up multipart files stacks
```接收文件出错后删除临时文件
at org.apache.tomcat.util.http.fileupload.disk.DiskFileItem.delete(DiskFileItem.java:422)
at org.apache.tomcat.util.http.fileupload.FileUploadBase.parseRequest(FileUploadBase.java:312)
at org.apache.catalina.connector.Request.parseParts(Request.java:2587)
at org.apache.catalina.connector.Request.getParts(Request.java:2487)
at org.apache.catalina.connector.RequestFacade.getParts(RequestFacade.java:773)
at org.springframework.web.multipart.support.StandardMultipartHttpServletRequest.parseRequest(StandardMultipartHttpServletRequest.java:94)
at org.springframework.web.multipart.support.StandardMultipartHttpServletRequest.<init>(StandardMultipartHttpServletRequest.java:87)
at org.springframework.web.multipart.support.StandardServletMultipartResolver.resolveMultipart(StandardServletMultipartResolver.java:112)
at org.springframework.web.servlet.DispatcherServlet.checkMultipart(DispatcherServlet.java:1228)
at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1061)
at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979)
at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014)
at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:914)
at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:590)
```
```org.apache.tomcat.util.http.fileupload.disk.DiskFileItem.delete::getTempFile()
    protected File getTempFile() {
        if (this.tempFile == null) {
            File tempDir = this.repository;
            if (tempDir == null) {
                tempDir = new File(System.getProperty("java.io.tmpdir"));
            }

            String tempFileName = String.format("upload_%s_%s.tmp", UID, getUniqueId());
            this.tempFile = new File(tempDir, tempFileName);
        }

        return this.tempFile;
    }
```
## Java Web Filter/Servlet层（负责将Container接收到的Request files分发给Application层，并负责在Application层处理完请求后删除上传的临时文件。）
### Spring Web MVC（DispatcherServlet + *MultipartResolver*）
- org.springframework.web.servlet.DispatcherServlet
- Clean up multipart files stacks
```处理完成后删除临时文件
at org.apache.tomcat.util.http.fileupload.disk.DiskFileItem.delete(DiskFileItem.java:424)
at org.apache.catalina.core.ApplicationPart.delete(ApplicationPart.java:53)
at org.springframework.web.multipart.support.StandardServletMultipartResolver.cleanupMultipart(StandardServletMultipartResolver.java:122)
at org.springframework.web.servlet.DispatcherServlet.cleanupMultipart(DispatcherServlet.java:1269)
at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1126)
at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979)
at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014)
at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:914)
at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:590)
```
### Non Spring Web MVC（MultipartFilter + *MultipartResolver*）
- org.springframework.web.multipart.support.MultipartFilter

## Java Web Application层（SpringBoot Controller）
- org.springframework.web.multipart.MultipartFile
- org.springframework.web.multipart.MultipartResolver
- org.springframework.web.multipart.MultipartRequest
- org.springframework.web.multipart.MultipartHttpServletRequest
- org.springframework.web.multipart.MultipartFileResource
- org.springframework.web.multipart.MultipartException
- org.springframework.web.multipart.MaxUploadSizeExceededException
- org.springframework.web.multipart.support.StandardServletMultipartResolver
- org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest
- org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest
- org.springframework.web.multipart.support.StandardMultipartHttpServletRequest
- org.springframework.web.multipart.support.StandardMultipartHttpServletRequest.StandardMultipartFile
- org.springframework.web.multipart.commons.CommonsMultipartResolver
- org.springframework.web.multipart.commons.CommonsMultipartFile

# Java Web Application层打开InputStream而不关闭的问题
- 在Windows系统中，打开InputStream而不关闭，会导致底层打开的文件被占用，也会导致DiskFileItem.delete()不能成功删除文件。
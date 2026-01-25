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

# How the underlay File Descriptor of an unclosed inputStream was closed by JDK
- java.lang.ref.Cleaner
- jdk.internal.ref.CleanerFactory
- sun.nio.ch.FileChannelImpl.Closer
## How FileChannelImpl to register closer for File Descriptor
```code
    private FileChannelImpl(FileDescriptor fd, String path, boolean readable, boolean writable, boolean direct, Object parent) {
        this.fd = fd;
        this.readable = readable;
        this.writable = writable;
        this.parent = parent;
        this.path = path;
        this.direct = direct;
        this.nd = new FileDispatcherImpl();
        if (direct) {
            assert path != null;

            this.alignment = this.nd.setDirectIO(fd, path);
        } else {
            this.alignment = -1;
        }

        this.closer = parent != null ? null : CleanerFactory.cleaner().register(this, new Closer(fd));
    }
```
```stacks
at sun.nio.ch.FileChannelImpl.<init>(FileChannelImpl.java:144)
at sun.nio.ch.FileChannelImpl.open(FileChannelImpl.java:154)
at sun.nio.fs.UnixChannelFactory.newFileChannel(UnixChannelFactory.java:134)
at sun.nio.fs.UnixChannelFactory.newFileChannel(UnixChannelFactory.java:146)
at sun.nio.fs.UnixFileSystemProvider.newByteChannel(UnixFileSystemProvider.java:216)
at java.nio.file.Files.newByteChannel(Files.java:380)
at java.nio.file.Files.newByteChannel(Files.java:432)
at java.nio.file.spi.FileSystemProvider.newInputStream(FileSystemProvider.java:422)
at java.nio.file.Files.newInputStream(Files.java:160)
at org.apache.tomcat.util.http.fileupload.disk.DiskFileItem.getInputStream(DiskFileItem.java:196)
at org.apache.catalina.core.ApplicationPart.getInputStream(ApplicationPart.java:97)
at org.springframework.web.multipart.support.StandardMultipartHttpServletRequest$StandardMultipartFile.getInputStream(StandardMultipartHttpServletRequest.java:261)
at com.zxf.example.controllers.FileUploadController.uploadMultiFile(FileUploadController.java:50)
at jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(NativeMethodAccessorImpl.java:-1)
at jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
at jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
at java.lang.reflect.Method.invoke(Method.java:569)
at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:255)
at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:188)
at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:118)
at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:926)
at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:831)
at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)
at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089)
at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979)
at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014)
at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:914)
at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:590)
at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885)
at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195)
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51)
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)
at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)
at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)
at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167)
at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90)
at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483)
at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:115)
at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93)
at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)
at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344)
at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:397)
at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63)
at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:905)
at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1741)
at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52)
at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1190)
at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659)
at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63)
at java.lang.Thread.run(Thread.java:840)
-----------------------------------------------------------------------------------------
at java.lang.ref.Cleaner.register(Cleaner.java:218)
at sun.nio.ch.FileChannelImpl.<init>(FileChannelImpl.java:145)
at sun.nio.ch.FileChannelImpl.open(FileChannelImpl.java:154)
at sun.nio.fs.UnixChannelFactory.newFileChannel(UnixChannelFactory.java:134)
at sun.nio.fs.UnixChannelFactory.newFileChannel(UnixChannelFactory.java:146)
at sun.nio.fs.UnixFileSystemProvider.newByteChannel(UnixFileSystemProvider.java:216)
at java.nio.file.Files.newByteChannel(Files.java:380)
at java.nio.file.Files.newByteChannel(Files.java:432)
at java.nio.file.spi.FileSystemProvider.newInputStream(FileSystemProvider.java:422)
at java.nio.file.Files.newInputStream(Files.java:160)
at org.apache.tomcat.util.http.fileupload.disk.DiskFileItem.get(DiskFileItem.java:305)
at org.apache.tomcat.util.http.fileupload.disk.DiskFileItem.getString(DiskFileItem.java:328)
at org.apache.catalina.core.ApplicationPart.getString(ApplicationPart.java:124)
at org.apache.catalina.connector.Request.parseParts(Request.java:2613)
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
at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885)
at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195)
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51)
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)
at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)
at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)
at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167)
at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90)
at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483)
at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:115)
at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93)
at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)
at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344)
at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:397)
at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63)
at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:905)
at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1741)
at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52)
at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1190)
at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659)
at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63)
at java.lang.Thread.run(Thread.java:840)
```

## How the Closer was called
```code
    private static class Closer implements Runnable {
        private final FileDescriptor fd;

        Closer(FileDescriptor fd) {
            this.fd = fd;
        }

        public void run() {
            try {
                FileChannelImpl.fdAccess.close(this.fd);
            } catch (IOException ioe) {
                throw new UncheckedIOException("close", ioe);
            }
        }
    }
```
```stacks
at java.io.FileDescriptor$1.close(FileDescriptor.java:88)
at sun.nio.ch.FileChannelImpl$Closer.run(FileChannelImpl.java:115)
at jdk.internal.ref.CleanerImpl$PhantomCleanableRef.performCleanup(CleanerImpl.java:178)
at jdk.internal.ref.PhantomCleanable.clean(PhantomCleanable.java:133)
at sun.nio.ch.FileChannelImpl.implCloseChannel(FileChannelImpl.java:207)
at java.nio.channels.spi.AbstractInterruptibleChannel.close(AbstractInterruptibleChannel.java:112)
at sun.nio.ch.ChannelInputStream.close(ChannelInputStream.java:142)
at org.apache.tomcat.util.http.fileupload.disk.DiskFileItem.get(DiskFileItem.java:307)
at org.apache.tomcat.util.http.fileupload.disk.DiskFileItem.getString(DiskFileItem.java:328)
at org.apache.catalina.core.ApplicationPart.getString(ApplicationPart.java:124)
at org.apache.catalina.connector.Request.parseParts(Request.java:2613)
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
at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885)
at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195)
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51)
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)
at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)
at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)
at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167)
at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90)
at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483)
at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:115)
at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93)
at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)
at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344)
at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:397)
at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63)
at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:905)
at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1741)
at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52)
at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1190)
at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659)
at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63)
at java.lang.Thread.run(Thread.java:840)
--------------------------
java    35047 davis  101r      REG             259,11    102400 8797496 /tmp/upload_2be51a16_cf8e_4c0c_9e1b_d571f252d624_00000002.tmp (deleted)
java    35047 davis  102r      REG             259,11    102400 8797501 /tmp/upload_2be51a16_cf8e_4c0c_9e1b_d571f252d624_00000004.tmp (deleted)
java    35047 davis  103r      REG             259,11    102400 8797387 /tmp/upload_2be51a16_cf8e_4c0c_9e1b_d571f252d624_00000000.tmp (deleted)
------------------------------------jcmd <35047> GC.run ----------------------------------------
at java.io.FileDescriptor$1.close(FileDescriptor.java:88)
at sun.nio.ch.FileChannelImpl$Closer.run(FileChannelImpl.java:115)
at jdk.internal.ref.CleanerImpl$PhantomCleanableRef.performCleanup(CleanerImpl.java:178)
at jdk.internal.ref.PhantomCleanable.clean(PhantomCleanable.java:133)
at jdk.internal.ref.CleanerImpl.run(CleanerImpl.java:142)
at java.lang.Thread.run(Thread.java:840)
at jdk.internal.misc.InnocuousThread.run(InnocuousThread.java:162)

```

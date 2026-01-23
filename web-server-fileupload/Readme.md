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

# Key classes for multipart support
- org.springframework.web.servlet.DispatcherServlet[use MultipartFilter]
- javax.servlet.MultipartConfigElement
- javax.servlet.annotation.MultipartConfig
- org.springframework.web.multipart.MultipartFile
- org.springframework.web.multipart.MultipartResolver
- org.springframework.web.multipart.MultipartRequest
- org.springframework.web.multipart.MultipartHttpServletRequest
- org.springframework.web.multipart.MultipartFileResource
- org.springframework.web.multipart.MultipartException
- org.springframework.web.multipart.MaxUploadSizeExceededException
- org.springframework.web.multipart.support.MultipartFilter[use MultipartFilter]
- org.springframework.web.multipart.support.StandardServletMultipartResolver
- org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest
- org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest
- org.springframework.web.multipart.support.StandardMultipartHttpServletRequest
- org.springframework.web.multipart.support.StandardMultipartHttpServletRequest.StandardMultipartFile
- org.springframework.web.multipart.commons.CommonsMultipartResolver
- org.springframework.web.multipart.commons.CommonsMultipartFile
- org.springframework.boot.autoconfigure.web.servlet.MultipartProperties
- org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration

---

# 问题分析与已知问题

## Multipart 临时文件残留 Root Cause

### 根本原因

Spring StandardMultipartFile 使用延迟清理机制。临时文件的删除依赖于垃圾回收（GC），而 GC 时机不确定，导致临时文件可能在磁盘上长时间残留。

### 具体流程

```
1. 请求到达 → MultipartFilter 解析 multipart 请求
2. 文件 > spring.servlet.multipart.file-size-threshold (10KB) → 写入 ${java.io.tmpdir}
   - 临时文件命名前缀: spring-
   - 例如: spring-8879368143766892387.tmp
3. StandardMultipartFile 持有临时文件引用
4. 请求处理完成 → 临时文件不会立即删除
5. 只有当 StandardMultipartFile 对象被 GC 时才会删除
```

### 配置影响

| 配置项 | 当前值 | 影响 |
|--------|--------|------|
| `spring.servlet.multipart.resolve-lazily` | `false` | 在请求处理早期就解析，失败时无法在控制器中捕获 |
| `spring.servlet.multipart.file-size-threshold` | `10240` (10KB) | 小于此值保存在内存中，大于则写入磁盘 |
| `spring.servlet.multipart.location` | `${java.io.tmpdir}` | 临时文件存储位置 |

## 各端点的临时文件处理行为

| 端点 | 文件处理方式 | 临时文件残留风险 |
|------|-------------|-----------------|
| `/uploadFile` | 使用 try-with-resources 消费 InputStream | 较低（stream 被关闭，但仍等待 GC） |
| `/uploadMultiFile` | 仅存储 MultipartFile 到 Model | **高**（从未消费 InputStream） |
| `/uploadFileWithAdditionalData` | 仅存储 MultipartFile 到 Model | **高**（从未消费 InputStream） |
| `/uploadFilesWithAdditionalData` | 仅存储 MultipartFile[] 到 Model | **高**（从未消费 InputStream） |
| `/exchange1` | 调用 `attachment.getBytes()` | 中等（读取内容后等待 GC） |
| `/exchange2` | 调用 `attachment.getBytes()` | 中等（读取内容后等待 GC） |

### 关键发现

**未消费 InputStream 的影响：**

在 `FileUploadController.java:46-68` 中的四个端点（`/uploadMultiFile`, `/uploadFileWithAdditionalData`, `/uploadFilesWithAdditionalData`）从未调用 `getInputStream()` 或 `getBytes()`。

这意味着：
1. 临时文件的数据从未被读取
2. `StandardMultipartFile` 的临时文件引用未被清理
3. 文件只能依赖 GC 或定时清理任务来删除

## TempFileCleanupConfig 的局限性

`TempFileCleanupConfig` 提供定时清理功能，但存在以下限制：

| 问题 | 描述 |
|------|------|
| 清理延迟 | 首次清理延迟 2 分钟，之后每小时执行一次 |
| 残留时间窗口 | 1 小时内的临时文件不会被清理 |
| 无 shutdown hook | 应用正常/异常关闭时不会主动清理临时文件 |
| 目录依赖 | 依赖 `spring.servlet.multipart.location` 配置正确 |

## Spring Boot 3.x 的 MultipartFile 实现

**重要：** Spring Boot 3.x 使用 Jakarta EE，默认使用 `StandardMultipartFile`（基于 Servlet 3.0+ Part API），**不是** `CommonsMultipartFile`。

- `CommonsMultipartFile` 需要引入 `commons-fileupload` 依赖，且与 Spring Boot 3.x 的 Jakarta 命名空间不兼容
- `StandardMultipartFile` 临时文件前缀为 `spring-`
- `CommonsMultipartFile` 临时文件前缀为 `upload_`（本项目不使用）

## 其他发现的问题

### 1. 线程管理问题

`ExchangeController.java:51-70` 和 `76-91`：
```java
Thread thread = new Thread(() -> { ... });
thread.start();
```

**问题：**
- 直接创建新 Thread，未使用线程池
- 未管理线程生命周期
- 可能导致线程数量失控

### 2. 内存读取效率问题

`FileUploadController.java:24`：
```java
IOUtils.toByteArray(stream)  // 将整个文件读入内存
```

**问题：**
- 对于大文件（接近 1MB 限制）会消耗大量内存
- 已经读取到 byte[]，之后又创建 ByteArrayInputStream，存在冗余

### 3. resolve-lazily=false 的安全隐患

当 `resolve-lazily=false` 时：
- 在控制器方法执行前就解析请求
- 如果文件过大导致 OOM，请求还未进入控制器就失败
- 无法在控制器中优雅地处理 `MaxUploadSizeExceededException`

## 建议修复方案

1. **设置 `resolve-lazily=true`**：延迟解析，在控制器中主动处理异常
2. **主动消费 InputStream**：所有端点都应调用 `getInputStream()` 并正确关闭
3. **添加 shutdown hook**：应用关闭时清理临时文件
4. **使用线程池**：替代直接创建 Thread
5. **考虑使用 `transferTo()`**：将文件移动到最终位置，而非依赖临时文件

## 参考资料

- [Spring Multipart File Upload Documentation](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-multipart.html)
- [Servlet 3.0 Part API](https://docs.oracle.com/javaee/6/tutorial/doc/glhaf.html)

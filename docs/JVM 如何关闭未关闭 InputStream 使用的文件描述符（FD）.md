JVM 通过以下几种机制处理未关闭 InputStream 的文件描述符：

1. 终结器（Finalization）（Java 9+ 已废弃，但仍有效）

```java
public class FileInputStream {
    protected void finalize() throws IOException {
        if (fd != null) {
            if (fd == FileDescriptor.in) return;
            close();
        }
    }
}
```

· Java 9 之前，终结器在垃圾回收时被调用
· 问题：时机不确定 - 可能需要几秒到几小时

2. 清理器（Cleaners）（Java 9+）

现代 Java 使用 Cleaner API（sun.misc.Cleaner 或 java.lang.ref.Cleaner）：

```java
// FileInputStream 使用 Cleaner 的简化示例
public class FileInputStream {
    private final FileDescriptor fd;
    private final Cleaner.Cleanable cleanable;
    
    public FileInputStream(File file) {
        fd = openFile(file);
        cleanable = Cleaner.create()
            .register(this, () -> closeQuietly(fd));
    }
}
```

· 清理器比终结器更可靠
· 当对象变为虚可达（phantom reachable）时运行

3. 虚引用（Phantom Reference）+ 引用队列

· JVM 通过 PhantomReference 跟踪对象
· 当对象只能通过虚引用到达时
· 清理器线程处理引用队列并关闭资源

4. 进程终止

当 JVM 退出时：

· 操作系统关闭所有剩余的文件描述符
· Windows：调用 CloseHandle()
· Unix/Linux：调用 close() 系统调用

重要注意事项：

资源泄漏风险：

```java
// 有风险 - 依赖终结器/清理器
public void readFile() {
    FileInputStream fis = new FileInputStream("file.txt");
    // 如果在 close() 前发生异常，FD 会保持打开状态
    fis.read();
    // fis 可能不会立即关闭！
}

// 更好的做法 - 使用 try-with-resources
public void readFile() {
    try (FileInputStream fis = new FileInputStream("file.txt")) {
        fis.read();
    }
}
```

局限性：

1. 时机不确定：文件描述符可能比预期保持打开更长时间
2. FD 耗尽：在 GC 执行前打开太多 FD 会导致"打开文件过多"错误
3. 性能开销：清理器/终结器线程消耗 CPU 资源
4. Windows 文件锁定：在 FD 关闭前可能阻止其他进程访问文件

最佳实践：

```java
// 始终使用 try-with-resources
try (InputStream is = new FileInputStream("file.txt")) {
    // 使用流
} // 在此处自动关闭

// 或者在 finally 块中显式关闭
InputStream is = null;
try {
    is = new FileInputStream("file.txt");
    // 使用流
} finally {
    if (is != null) {
        try {
            is.close();
        } catch (IOException e) {
            // 记录但不抛出
        }
    }
}
```

关键要点：虽然 JVM 提供了安全网，但永远不要依赖它们来管理资源。始终显式关闭资源或使用 try-with-resources 来避免资源泄漏并确保可预测的行为。
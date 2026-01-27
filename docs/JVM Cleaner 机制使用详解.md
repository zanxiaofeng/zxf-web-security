一、Cleaner 基本概念

Cleaner 是 Java 9 引入的用于替代 finalize() 的资源清理机制，基于 PhantomReference 实现。

```java
// Cleaner 在 java.lang.ref 包中
import java.lang.ref.Cleaner;
```

二、基本使用方式

1. 创建 Cleaner 实例

```java
// 方式1：使用系统 Cleaner（推荐）
Cleaner systemCleaner = Cleaner.create();

// 方式2：创建独立 Cleaner
Cleaner customCleaner = Cleaner.create();

// 方式3：使用全局 Cleaner（Java 17+）
Cleaner globalCleaner = Cleaner.getSystemCleaner();
```

2. 注册清理动作

```java
public class ResourceHolder {
    private final Resource resource; // 需要清理的资源
    private final Cleaner.Cleanable cleanable;
    
    public ResourceHolder(Resource resource) {
        this.resource = resource;
        
        // 注册清理动作
        this.cleanable = Cleaner.create().register(
            this, // 监控的对象
            this::cleanupResource // 清理动作
        );
    }
    
    private void cleanupResource() {
        // 执行资源清理
        resource.close();
        System.out.println("资源已清理");
    }
    
    // 可选：手动触发清理
    public void manualCleanup() {
        if (cleanable != null) {
            cleanable.clean();
        }
    }
}
```

三、完整示例：管理文件资源

示例1：简单的文件清理

```java
import java.lang.ref.Cleaner;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileCleanerExample {
    private static final Cleaner CLEANER = Cleaner.create();
    
    static class FileResource implements Runnable {
        private final Path filePath;
        
        FileResource(Path filePath) {
            this.filePath = filePath;
            System.out.println("创建文件资源: " + filePath);
        }
        
        @Override
        public void run() {
            // 当FileResource对象被回收时执行
            try {
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    System.out.println("已删除文件: " + filePath);
                }
            } catch (IOException e) {
                System.err.println("删除文件失败: " + e.getMessage());
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        // 创建临时文件
        Path tempFile = Files.createTempFile("cleaner-example", ".tmp");
        Files.writeString(tempFile, "测试数据");
        
        // 创建资源对象并注册清理
        FileResource resource = new FileResource(tempFile);
        CLEANER.register(resource, resource);
        
        // 显式置空引用，触发GC
        resource = null;
        
        // 建议GC，但不可依赖
        System.gc();
        
        // 等待一会儿，给Cleaner线程时间执行
        Thread.sleep(1000);
        
        System.out.println("文件是否存在: " + Files.exists(tempFile));
    }
}
```

示例2：数据库连接清理

```java
import java.lang.ref.Cleaner;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnectionManager {
    private static final Cleaner CONNECTION_CLEANER = Cleaner.create();
    
    static class ConnectionWrapper {
        private Connection connection;
        private final String url;
        private final String user;
        private final String password;
        
        ConnectionWrapper(String url, String user, String password) {
            this.url = url;
            this.user = user;
            this.password = password;
        }
        
        Connection getConnection() throws Exception {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
                // 注册清理
                CONNECTION_CLEANER.register(this, new ConnectionCleanup(connection));
            }
            return connection;
        }
        
        static class ConnectionCleanup implements Runnable {
            private final Connection conn;
            
            ConnectionCleanup(Connection conn) {
                this.conn = conn;
            }
            
            @Override
            public void run() {
                try {
                    if (conn != null && !conn.isClosed()) {
                        System.out.println("Cleaner: 自动关闭数据库连接");
                        conn.close();
                    }
                } catch (Exception e) {
                    System.err.println("清理连接失败: " + e.getMessage());
                }
            }
        }
    }
}
```

四、高级用法

1. 批量资源管理

```java
import java.lang.ref.Cleaner;
import java.util.concurrent.ConcurrentHashMap;

public class ResourcePool {
    private static final Cleaner POOL_CLEANER = Cleaner.create();
    private final ConcurrentHashMap<String, PooledResource> resources = new ConcurrentHashMap<>();
    
    static class PooledResource implements Runnable {
        private final String resourceId;
        private final AutoCloseable resource;
        
        PooledResource(String resourceId, AutoCloseable resource) {
            this.resourceId = resourceId;
            this.resource = resource;
        }
        
        @Override
        public void run() {
            try {
                resource.close();
                System.out.println("自动释放资源: " + resourceId);
            } catch (Exception e) {
                System.err.println("释放资源失败: " + e.getMessage());
            }
        }
    }
    
    public void addResource(String id, AutoCloseable resource) {
        PooledResource pooled = new PooledResource(id, resource);
        resources.put(id, pooled);
        POOL_CLEANER.register(pooled, pooled);
    }
    
    public void removeResource(String id) {
        PooledResource pooled = resources.remove(id);
        if (pooled != null) {
            pooled.run(); // 立即执行清理
        }
    }
}
```

2. 状态感知清理

```java
import java.lang.ref.Cleaner;

public class StatefulResource {
    private static final Cleaner CLEANER = Cleaner.create();
    private final ResourceState state;
    private final Cleaner.Cleanable cleanable;
    
    public StatefulResource() {
        this.state = new ResourceState();
        this.cleanable = CLEANER.register(this, new StatefulCleanup(state));
    }
    
    static class ResourceState {
        volatile boolean closed = false;
        volatile Throwable closeError = null;
        
        void close() {
            try {
                // 执行实际关闭逻辑
                System.out.println("关闭资源...");
                closed = true;
            } catch (Throwable t) {
                closeError = t;
            }
        }
    }
    
    static class StatefulCleanup implements Runnable {
        private final ResourceState state;
        
        StatefulCleanup(ResourceState state) {
            this.state = state;
        }
        
        @Override
        public void run() {
            if (!state.closed) {
                System.out.println("Cleaner执行关闭...");
                state.close();
                if (state.closeError != null) {
                    System.err.println("关闭错误: " + state.closeError.getMessage());
                }
            }
        }
    }
    
    public void manualClose() {
        state.close();
        cleanable.clean(); // 从Cleaner中注销
    }
}
```

五、最佳实践和注意事项

1. Cleaner 使用规范

```java
public class CorrectCleanerUsage {
    // 1. 使用静态Cleaner实例（避免每个对象创建Cleaner）
    private static final Cleaner GLOBAL_CLEANER = Cleaner.create();
    
    // 2. 清理动作应该是独立的，不持有外部引用
    static class CleanupTask implements Runnable {
        private final ExternalResource resource;
        
        CleanupTask(ExternalResource resource) {
            // 只持有需要清理的资源引用
            this.resource = resource;
        }
        
        @Override
        public void run() {
            try {
                resource.close();
            } catch (Exception e) {
                // 不要抛出异常，记录日志即可
                Logger.log(e);
            }
        }
    }
    
    // 3. 清理动作应该是幂等的
    static class IdempotentCleanup implements Runnable {
        private volatile boolean cleaned = false;
        private final Lock lock = new ReentrantLock();
        
        @Override
        public void run() {
            if (cleaned) return;
            
            lock.lock();
            try {
                if (!cleaned) {
                    // 执行清理
                    cleaned = true;
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
```

2. 常见陷阱

```java
public class CleanerPitfalls {
    // 错误示例1：清理动作持有对象引用（导致内存泄漏）
    static class BadCleanup implements Runnable {
        private final OuterObject outer; // 持有外部对象引用！
        
        BadCleanup(OuterObject outer) {
            this.outer = outer; // 错误：形成循环引用
        }
        
        @Override public void run() {
            outer.clean();
        }
    }
    
    // 正确做法：只持有需要清理的资源的引用
    static class GoodCleanup implements Runnable {
        private final Closeable resource; // 只持有资源引用
        
        GoodCleanup(Closeable resource) {
            this.resource = resource;
        }
        
        @Override public void run() {
            try {
                resource.close();
            } catch (IOException ignored) {}
        }
    }
    
    // 错误示例2：在清理动作中创建新对象
    static class ExpensiveCleanup implements Runnable {
        @Override
        public void run() {
            // 错误：在清理中分配内存
            byte[] buffer = new byte[1024 * 1024]; // 1MB
            // 清理逻辑...
        }
    }
}
```

3. 性能优化技巧

```java
public class OptimizedCleaner {
    // 1. 使用Cleaner池
    private static final Cleaner[] CLEANER_POOL = new Cleaner[4];
    static {
        for (int i = 0; i < CLEANER_POOL.length; i++) {
            CLEANER_POOL[i] = Cleaner.create();
        }
    }
    
    // 2. 简单的哈希分配策略
    private static Cleaner getCleaner(Object obj) {
        int hash = System.identityHashCode(obj);
        return CLEANER_POOL[hash & (CLEANER_POOL.length - 1)];
    }
    
    // 3. 批量注册优化
    static class BatchRegistration<T extends AutoCloseable> {
        private final List<T> resources = new ArrayList<>();
        private final Cleaner cleaner;
        
        BatchRegistration(Cleaner cleaner) {
            this.cleaner = cleaner;
        }
        
        void addResource(T resource) {
            resources.add(resource);
            cleaner.register(resource, resource::close);
        }
        
        void closeAll() {
            for (T resource : resources) {
                try {
                    resource.close();
                } catch (Exception ignored) {}
            }
            resources.clear();
        }
    }
}
```

六、与 try-with-resources 结合

```java
public class CleanerWithTryResources {
    private static final Cleaner CLEANER = Cleaner.create();
    
    // 自定义AutoCloseable资源
    static class ManagedResource implements AutoCloseable {
        private final String name;
        private final Cleaner.Cleanable cleanable;
        private volatile boolean manuallyClosed = false;
        
        public ManagedResource(String name) {
            this.name = name;
            this.cleanable = CLEANER.register(this, new ResourceCleanup(name));
            System.out.println("创建资源: " + name);
        }
        
        @Override
        public void close() {
            if (!manuallyClosed) {
                System.out.println("手动关闭资源: " + name);
                manuallyClosed = true;
                cleanable.clean(); // 从Cleaner中移除
            }
        }
        
        static class ResourceCleanup implements Runnable {
            private final String resourceName;
            
            ResourceCleanup(String name) {
                this.resourceName = name;
            }
            
            @Override
            public void run() {
                System.out.println("自动清理资源: " + resourceName);
            }
        }
    }
    
    public static void main(String[] args) {
        // 使用try-with-resources优先
        try (ManagedResource r1 = new ManagedResource("Resource1")) {
            System.out.println("使用资源中...");
            // 正常使用，会自动调用close()
        }
        
        // 不使用时，依赖Cleaner
        ManagedResource r2 = new ManagedResource("Resource2");
        r2 = null; // 依赖Cleaner自动清理
        System.gc();
    }
}
```

七、监控和调试

```java
import java.lang.ref.Cleaner;
import java.lang.management.ManagementFactory;

public class CleanerMonitor {
    private static final Cleaner MONITORED_CLEANER = Cleaner.create();
    
    // 监控Cleaner活动
    static class MonitoredCleanup implements Runnable {
        private final String id;
        private final long startTime;
        
        MonitoredCleanup(String id) {
            this.id = id;
            this.startTime = System.currentTimeMillis();
        }
        
        @Override
        public void run() {
            long lifeTime = System.currentTimeMillis() - startTime;
            System.out.printf("资源[%s]生命周期: %dms, 在%s被清理%n",
                id, lifeTime, Thread.currentThread().getName());
            
            // 记录到监控系统
            recordCleanupMetrics(id, lifeTime);
        }
        
        private void recordCleanupMetrics(String id, long lifetime) {
            // 记录到日志或监控系统
        }
    }
    
    // 获取Cleaner线程信息
    public static void printCleanerThreadInfo() {
        Thread.getAllStackTraces().forEach((thread, stack) -> {
            if (thread.getName().contains("Cleaner")) {
                System.out.println("Cleaner线程: " + thread.getName());
                System.out.println("状态: " + thread.getState());
                System.out.println("优先级: " + thread.getPriority());
            }
        });
    }
}
```

八、注意事项总结

1. 不要依赖 Cleaner 进行关键资源管理 - 它只是安全网
2. 清理动作要简单快速 - 避免复杂操作和内存分配
3. 避免在清理动作中抛出异常
4. 清理动作应该是幂等的
5. 优先使用 try-with-resources 或显式 close()
6. 测试 Cleaner 行为 - 在单元测试中验证资源清理

Cleaner 机制为 Java 提供了比 finalize() 更可靠、更高效的资源清理方案，但正确使用它需要遵循最佳实践。
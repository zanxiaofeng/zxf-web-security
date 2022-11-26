# Two type CORS requests
## Simple cross domain request(one request)
- GET, POST
## With preflight cross domain request(two requests, one is preflight and another is real)
- PUT, DELETE, HEAD


# Config
- /my/** Has CORS Config(Disable cross domain request)
- /cors/** Has CORS Config(Allow cross domain request from http://localhost:8080)
- others No CORS config(Allow cross request for any domain and simple request, will block preflight request)

# Core classes
- org.springframework.web.filter.CorsFilter(好像没有用到)
- org.springframework.web.servlet.handler.AbstractHandlerMapping.CorsInterceptor
- org.springframework.web.cors.CorsProcessor
- org.springframework.web.cors.DefaultCorsProcessor
- org.springframework.security.web.access.ExceptionTranslationFilter

# 服务器端如何判断一个请求是否为跨域请求
-　请求带有Origin Header
-　请求中的schema，host，port与Origin Header中的schema，host，port完全一致

# 客户端如何发出跨域请求
- 跨域请求只针对浏览器，因为只有浏览器知道当前域和目标域
- 其他客户端要模拟跨域请求可以在请求中包含Origin Header
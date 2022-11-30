# Two type CORS requests
## Simple cross domain request(one request)
- GET, POST, HEAD
- 使用 Origin 和 Access-Control-Allow-Origin在服务器端(By Origin)或浏览器端(By Access-Control-Allow-Origin)实施访问控制。
## With preflight cross domain request(two requests, one is preflight and another is real)
- PUT, DELETE, HEAD


# Config
- /my/** Has CORS Config(Disable cross domain request)
- /cors/** Has CORS Config(Allow cross domain request from http://localhost:8080)
- others No CORS config(Allow cross request for any domain and simple request, will block preflight request)
- 预检OPTIONS 是 HTTP/1.1 协议中定义的方法，用以从服务器获取更多信息, 使用Access-Control-Request-Method, Access-Control-Request-Headers可以获取url指定服务端资源CORS访问控制信息（Access-Control-Allow-Origin, Access-Control-Allow-Methods, Access-Control-Allow-Headers, Access-Control-Max-Age），并由浏览器对随后的真实请求实施访问控制。

# Core classes
- org.springframework.web.filter.CorsFilter(好像没有用到)
- org.springframework.web.servlet.handler.AbstractHandlerMapping.CorsInterceptor
- org.springframework.web.cors.CorsProcessor
- org.springframework.web.cors.DefaultCorsProcessor
- org.springframework.security.web.access.ExceptionTranslationFilter

# 服务器端如何判断一个请求是否为跨域请求
- 请求带有Origin Header
- 请求中的schema，host，port与Origin Header中的schema，host，port完全一致(不完全需要)

# 客户端如何发出跨域请求
- 跨域请求只针对浏览器，因为只有浏览器知道当前域和目标域
- 其他客户端要模拟跨域请求可以在请求中包含Origin Header
- 对于浏览器自己发出的请求和JS ajax发出的请求处理会有不同

# CORS 请求处理
- 对于CORS请求的处理，需要浏览器和服务器共同协作相互配合才能做到，Server端可以根据自己定义的CORS规则Reject请求，浏览器也可以根据自己的规则在某些情况下阻止读取CORS请求的Response
- 对于CORS请求，服务端能做的决策就是是否要Reject该请求，浏览器能做的决策包括是否可以发出该CORS请求以及是否可以把该CORS请求的Response返回给请求者(JS)
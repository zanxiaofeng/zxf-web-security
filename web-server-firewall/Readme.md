# Core classes
- org.springframework.security.web.FilterChainProxyFilterChainProxy.firewall
- org.springframework.security.web.firewall.HttpFirewall
- org.springframework.security.web.firewall.FirewalledRequest
- org.springframework.security.web.firewall.FirewalledResponse
- org.springframework.security.web.firewall.DefaultHttpFirewall
- org.springframework.security.web.firewall.StrictHttpFirewall
- 

# StrictHttpFirewall
## Request flow
- org.springframework.security.web.firewall.StrictHttpFirewall.getFirewalledRequest(HttpServletRequest request):FirewalledRequest
- org.springframework.security.web.firewall.StrictHttpFirewall.rejectForbiddenHttpMethod(HttpServletRequest request):void
- org.springframework.security.web.firewall.StrictHttpFirewall.rejectedBlocklistedUrls(HttpServletRequest request):void
- org.springframework.security.web.firewall.StrictHttpFirewall.rejectedUntrustedHosts(HttpServletRequest request):void
- org.springframework.security.web.firewall.StrictHttpFirewall.rejectNonPrintableAsciiCharactersInFieldName(String toCheck, String propertyName):void
- org.springframework.security.web.firewall.StrictHttpFirewall.StrictFirewalledRequest
## Response flow
- org.springframework.security.web.firewall.StrictHttpFirewall.getFirewalledResponse(HttpServletResponse response):HttpServletResponse

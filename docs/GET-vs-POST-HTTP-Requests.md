# Difference between HTTP GET and HTTP POST
|                                                   HTTP GET                                                |                                                            HTTP POST                                                |
| :---------------------------------------------------------------------------------------------------------| ------------------------------------------------------------------------------------------------------------------: | 
| In GET method, values are visible in the URL.                                                             | In POST method, values are not visible in the URL.                                                                  |
| GET has a limitation on the length of the values, generally 255 characters.                               | POST has no limitation on the length of the values since they are submitted via the body of HTTP.                   |
| GET performs are better compared to POST because of the simple nature of appending the values in the URL. | It has lower performance as compared to GET method because of time spent in including POST values in the HTTP body. |
| This method supports only string data types.                                                              | This method supports different data types, such as string, numeric, binary, etc.                                    |
| GET results can be bookmarked.                                                                            | POST results can not be bookmarked.                                                                                 |
| GET requests is often cacheable.                                                                          | The POST request is hardly cacheable.                                                                               |
| GET Parameters remain in web browser history.                                                             | Parameters are not saved in the web browser history.                                                                |

# Why use POST vs GET to key application secure
- HTTP Logging on Servers and Proxies
- Proxy Caching of HTTP GET requests
- Exposing information in browser(Address Bar, History, Refresh, Cache, etc)
- HTTP Referer
- Exposing information over the wire(HTTP)
- Search Engine Spiders
- Web Accelerators

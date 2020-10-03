package qlik.qliktest.log

import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingRequestWrapper
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AccessLogFilter : Filter {
    private val logger = KotlinLogging.logger { }

    override fun doFilter(request: ServletRequest, response: ServletResponse, filterChain: FilterChain) {
        val requestWrapper = ContentCachingRequestWrapper(request as HttpServletRequest)
        val startTime = System.currentTimeMillis()
        filterChain.doFilter(requestWrapper, response)
        val endTime = System.currentTimeMillis()
        val status = (response as HttpServletResponse).status
        val method = (requestWrapper as HttpServletRequest).method
        val path = requestWrapper.requestURI
        val query = requestWrapper.queryString
        val responseTime = endTime - startTime

        val pathWithQuery = if (query == null) path else "$path?$query"

        logger.info {
            "source=${LoggingContext.controllerName} eventName=${LoggingContext.controllerMethodName} path=$pathWithQuery method=$method status=$status responseTime=$responseTime logType=access"
        }
        LoggingContext.clear()
    }
}

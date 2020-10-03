package qlik.qliktest.log

import org.slf4j.MDC

object LoggingContext {
    const val CONTROLLER_NAME = "controllerName"
    const val CONTROLLER_METHOD_NAME = "controllerMethodName"

    var controllerName: String?
        get() = MDC.get(CONTROLLER_NAME)
        set(controllerName) = MDC.put(CONTROLLER_NAME, controllerName)

    var controllerMethodName: String?
        get() = MDC.get(CONTROLLER_METHOD_NAME)
        set(controllerMethodName) = MDC.put(CONTROLLER_METHOD_NAME, controllerMethodName)

    fun clear() {
        MDC.clear()
    }
}

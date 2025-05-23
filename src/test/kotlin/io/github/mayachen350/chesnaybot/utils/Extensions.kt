package io.github.mayachen350.chesnaybot.utils

internal fun Any.callPrivateMethod(methodName: String, vararg params: Any): Unit {
    this::class.java.declaredMethods
        .first { it.name == methodName }.apply { isAccessible = true }.invoke(this,params)
}
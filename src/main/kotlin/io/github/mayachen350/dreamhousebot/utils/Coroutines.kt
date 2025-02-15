package io.github.mayachen350.dreamhousebot.utils

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**Inline function which is used  to makes sure bot functions runs apart from the logs, while also separating them.**/
// TODO: Put it everywhere
suspend inline fun functionWithLogs(
    crossinline func: suspend () -> Unit,
    crossinline logFunc: suspend () -> Unit
): Unit {
    coroutineScope {
        launch { func() }
        launch { logFunc() }
    }
}
package io.github.mayachen350.chesnaybot

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/*Run all the setup and onStart steps*/
suspend fun setup() = coroutineScope {
    launch(Dispatchers.IO) { updateRoles() }

}

private fun updateRoles(): Unit {

}
package com.kakaoent.realtime.search.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


object EventFlow {
    private val events = MutableSharedFlow<String>()
    val mutableEvents = events.asSharedFlow()

    fun post(event: String) {
        CoroutineScope(Dispatchers.IO).launch {
            events.emit(event)
        }
    }

    inline fun <reified T> subscribe(): Flow<T> {
        return mutableEvents.filter { it is T }.map { it as T }
    }
}
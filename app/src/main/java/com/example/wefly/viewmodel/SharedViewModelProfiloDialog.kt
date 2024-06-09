package com.example.wefly.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// classe implementata da ProfiloFragment e PopUpInfoUtenteFragment per il trigger di un evento

class SharedViewModelProfiloDialog : ViewModel() {

    private val _event = MutableLiveData<Event<Unit>>()
    val event: LiveData<Event<Unit>> get() = _event

    fun triggerEvent() {
        _event.value = Event(Unit)
    }

}

// Helper class to handle events
open class Event<out T>(private val content: T) {
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}
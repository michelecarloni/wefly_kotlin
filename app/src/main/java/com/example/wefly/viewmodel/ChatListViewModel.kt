package com.example.wefly.viewmodel

import androidx.lifecycle.ViewModel
import com.example.wefly.model.ModelChatList

class ChatListViewModel: ViewModel() {
    fun createChat( url : String, nome : String, cognome : String): ModelChatList{
        return ModelChatList(url, nome, cognome)
    }
}
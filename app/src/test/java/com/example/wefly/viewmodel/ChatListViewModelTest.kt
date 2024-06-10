package com.example.wefly.viewmodel

import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class ChatListViewModelTest{

    private lateinit var chatListViewModel: ChatListViewModel

    @Before
    fun setUp(){
        chatListViewModel = ChatListViewModel()
    }

    @Test
    fun createNewChatList(){
        var chatObject = chatListViewModel.createChat("https/Prova", "michele", "carloni")
        println("URL: ${chatObject.url}")
        println("Nome: ${chatObject.nome}")
        println("Cognome: ${chatObject.cognome}")
    }
}
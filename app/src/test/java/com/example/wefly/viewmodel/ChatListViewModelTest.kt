package com.example.wefly.viewmodel

import org.junit.Before
import org.junit.Test

class ChatListViewModelTest{

    private lateinit var chatListViewModel: ChatListViewModel

    @Before
    fun setUp(){
        chatListViewModel = ChatListViewModel()
    }

    @Test
    fun createNewChatList(){
        var chatObject = chatListViewModel.createChat("https/Prova", "michele", "carloni", "123456789")
        println("URL: ${chatObject.url}")
        println("Nome: ${chatObject.nome}")
        println("Cognome: ${chatObject.cognome}")
        println("userUid: ${chatObject.userUid}")
    }
}
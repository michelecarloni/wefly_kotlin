package com.example.wefly.viewmodel

import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class CittaViewModelTest {

    private lateinit var cittaViewModel: CittaViewModel

    @Before
    fun setUp() {
        cittaViewModel = CittaViewModel()
    }

    @Test
    fun createNewCitta() {
        val cittaObject = cittaViewModel.createCitta("Ancona", "Italy", 30.0, 40.0)

        println("citta: ${cittaObject.citta}")
        println("nazione: ${cittaObject.nazione}")
        println("latitudine: ${cittaObject.lat}")
        println("longitudine: ${cittaObject.lng}")
    }
}

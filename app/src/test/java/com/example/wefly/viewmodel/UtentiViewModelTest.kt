package com.example.wefly.viewmodel

import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class UtentiViewModelTest{

    private lateinit var utentiViewModel : UtentiViewModel

    @Before
    fun setup(){
        utentiViewModel = UtentiViewModel()
    }

    @Test
    fun createNewUtente(){
        var utente = utentiViewModel.createUtente(
            "michele",
            "carloni",
            "+39 3456789012",
            "michelecarloni02@gmail.com",
            "prova123",
            "-Nzw1hj3Id6tqy38g82P, -Nzw1vACQ8hxEAVGIn-0, -NzwGQSCV7WlwMGtzGsq, -Nzwzxha4eQA8rYjieUh, ",
            true,
            false,
            true,
            false,
            true,
            false,
            true,
            false,
            true,
            false
        )

        assertEquals("michele", utente.nome)
        assertEquals("carloni", utente.cognome)
        assertEquals("+39 3456789012", utente.telefono)
        assertEquals("michelecarloni02@gmail.com", utente.email)
        assertEquals("prova123", utente.password)
        assertEquals("-Nzw1hj3Id6tqy38g82P, -Nzw1vACQ8hxEAVGIn-0, -NzwGQSCV7WlwMGtzGsq, -Nzwzxha4eQA8rYjieUh, ", utente.viaggiStr)
        assertEquals(true, utente.scelta1)
        assertEquals(false, utente.scelta2)
        assertEquals(true, utente.scelta3)
        assertEquals(false, utente.scelta4)
        assertEquals(true, utente.scelta5)
        assertEquals(false, utente.scelta6)
        assertEquals(true, utente.scelta7)
        assertEquals(false, utente.scelta8)
        assertEquals(true, utente.scelta9)
        assertEquals(false, utente.scelta10)

    }

}
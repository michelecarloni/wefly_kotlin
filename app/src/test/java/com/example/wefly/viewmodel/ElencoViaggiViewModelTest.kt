package com.example.wefly.viewmodel

import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals

class ElencoViaggiViewModelTest {

    private lateinit var elencoViaggiViewModel: ElencoViaggiViewModel

    @Before
    fun setup() {
        elencoViaggiViewModel = ElencoViaggiViewModel()
    }

    @Test
    fun createViaggio() {
        val viaggio = elencoViaggiViewModel.createViaggio(
            "-Nzw1hj3Id6tqy38g82P",
            "https/prova",
            "viaggio di prova",
            "300",
            "italy",
            "Ancona",
            "2/06/2024",
            "12/06/2024",
            4, 8,
            "part1, part2, part3, ",
            "descrizione1",
            true, false,
            true, false,
            true, false,
            true, false,
            true, false
        )

        assertEquals("-Nzw1hj3Id6tqy38g82P", viaggio.vid)
        assertEquals("https/prova", viaggio.imageUrl)
        assertEquals("viaggio di prova", viaggio.titoloViaggio)
        assertEquals("300", viaggio.budget)
        assertEquals("italy", viaggio.nazione)
        assertEquals("Ancona", viaggio.citta)
        assertEquals("2/06/2024", viaggio.dataPartenza)
        assertEquals("12/06/2024", viaggio.dataRitorno)
        assertEquals(4, viaggio.partecipanti)
        assertEquals(8, viaggio.partecipantiMax)
        assertEquals("part1, part2, part3, ", viaggio.partecipantiStr)
        assertEquals("descrizione1", viaggio.descrizione)
        assertEquals(true, viaggio.scelta1)
        assertEquals(false, viaggio.scelta2)
        assertEquals(true, viaggio.scelta3)
        assertEquals(false, viaggio.scelta4)
        assertEquals(true, viaggio.scelta5)
        assertEquals(false, viaggio.scelta6)
        assertEquals(true, viaggio.scelta7)
        assertEquals(false, viaggio.scelta8)
        assertEquals(true, viaggio.scelta9)
        assertEquals(false, viaggio.scelta10)
    }
}

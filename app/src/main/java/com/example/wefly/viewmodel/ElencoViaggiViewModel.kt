package com.example.wefly.viewmodel

import androidx.lifecycle.ViewModel
import com.example.wefly.model.ModelElencoViaggi

class ElencoViaggiViewModel: ViewModel() {
    fun createViaggio (vid: String, imageUrl : String, titoloViaggio: String, budget: String, nazione : String, citta : String, dataPartenza : String, dataRitorno: String, partecipanti : Int, partecipantiMax : Int, partecipantiStr: String, descrizione : String, scelta1: Boolean, scelta2: Boolean, scelta3: Boolean, scelta4: Boolean, scelta5: Boolean, scelta6: Boolean, scelta7: Boolean, scelta8: Boolean, scelta9: Boolean, scelta10: Boolean): ModelElencoViaggi{
        return ModelElencoViaggi(vid, imageUrl, titoloViaggio, budget, nazione, citta, dataPartenza, dataRitorno, partecipanti, partecipantiMax, partecipantiStr, descrizione, scelta1, scelta2, scelta3, scelta4, scelta5, scelta6, scelta7, scelta8, scelta9, scelta10)
    }
}
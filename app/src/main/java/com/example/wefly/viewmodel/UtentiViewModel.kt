package com.example.wefly.viewmodel

import androidx.lifecycle.ViewModel
import com.example.wefly.model.ModelUtenti

class UtentiViewModel: ViewModel() {
    fun createUtente(nome: String ?= null, cognome: String ?= null, telefono: String ?= null, email: String ?= null, password: String ?= null, viaggiStr : String ?= null, scelta1: Boolean ?= null, scelta2: Boolean ?= null, scelta3: Boolean ?= null, scelta4: Boolean ?= null, scelta5: Boolean ?= null, scelta6: Boolean ?= null, scelta7: Boolean ?= null, scelta8: Boolean ?= null, scelta9: Boolean ?= null, scelta10: Boolean ?= null): ModelUtenti {
        return ModelUtenti(nome, cognome, telefono, email, password, viaggiStr, scelta1, scelta2, scelta3, scelta4, scelta5, scelta6, scelta7, scelta8, scelta9, scelta10)
    }
}
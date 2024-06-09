package com.example.wefly.viewmodel

import androidx.lifecycle.ViewModel
import com.example.wefly.model.ModelCitta

class CittaViewModel: ViewModel() {
    fun createCitta(citta: String, nazione: String, lat: Double, lng: Double): ModelCitta {
        return ModelCitta(citta, nazione, lat, lng)
    }
}
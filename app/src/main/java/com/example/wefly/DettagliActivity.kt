package com.example.wefly

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DettagliActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dettagli)

        val getData = intent.getParcelableExtra<DataElencoViaggi>("android")
        if (getData != null) {
            // val detailImageId : ImageView = findViewById(R.id.title_image)
            val detailTitoloViaggio : TextView = findViewById(R.id.titolo)
            val detailCitta : TextView = findViewById(R.id.citta)
            val detailNazione : TextView = findViewById(R.id.nazione)
            val detailDataPartenza : TextView = findViewById(R.id.partenza)
            val detailDataRitorno : TextView = findViewById(R.id.ritorno)
            val detailBudget : TextView = findViewById(R.id.budget)
            //val detailTipoViaggio : TextView = findViewById(R.id.TipoViaggio)
            val detailPartecipanti : TextView = findViewById(R.id.partecipanti)
            val detailPartecipantiMax : TextView = findViewById(R.id.partecipanti_max)
            //val detailAffinita : TextView = findViewById(R.id.affinita)
            val detailDescrizione : TextView = findViewById(R.id.descrizione)

            //detailImageId.setImageResource(getData.titoloImmagine)
            detailTitoloViaggio.text = getData.titoloViaggio
            detailCitta.text = getData.citta
            detailNazione.text = getData.nazione
            detailDataPartenza.text = getData.dataPartenza
            detailDataRitorno.text = getData.dataRitorno
            detailBudget.text = getData.budget
            //detailTipoViaggio.text = getData.tipoViaggio
            detailPartecipanti.text = getData.partecipanti.toString()
            detailPartecipantiMax.text = getData.partecipantiMax.toString()
            //detailAffinita.text = getData.affinita
            detailDescrizione.text = getData.descrizione

        }

    }
}
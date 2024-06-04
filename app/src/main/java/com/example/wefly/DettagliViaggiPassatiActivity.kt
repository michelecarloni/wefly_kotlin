package com.example.wefly

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DettagliViaggiPassatiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContentView(R.layout.activity_dettagli_viaggi_passati)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val getData = intent.getParcelableExtra<DataViaggiPassati>("passati")
        if (getData != null) {
            // val detailImageIdpassati : ImageView = findViewById(R.id.title_image)
            val detailTitoloViaggiopassati : TextView = findViewById(R.id.titolo)
            val detailCittapassati : TextView = findViewById(R.id.citta)
            val detailNazionepassati : TextView = findViewById(R.id.nazione)
            val detailDataPartenzapassati : TextView = findViewById(R.id.partenza)
            val detailDataRitornopassati : TextView = findViewById(R.id.ritorno)
            val detailBudgetpassati : TextView = findViewById(R.id.budget)
            //val detailTipoViaggiopassati : TextView = findViewById(R.id.TipoViaggio)
            val detailPartecipantipassati : TextView = findViewById(R.id.partecipanti)
            val detailPartecipantiMaxpassati : TextView = findViewById(R.id.partecipanti_max)
            //val detailAffinitapassati : TextView = findViewById(R.id.affinita)
            val detailDescrizionepassati : TextView = findViewById(R.id.descrizione)

            //detailImageIdpassati.setImageResource(getData.titoloImmagine)
            detailTitoloViaggiopassati.text = getData.titoloViaggio
            detailCittapassati.text = getData.citta
            detailNazionepassati.text = getData.nazione
            detailDataPartenzapassati.text = getData.dataPartenza
            detailDataRitornopassati.text = getData.dataRitorno
            detailBudgetpassati.text = getData.budget
            //detailTipoViaggiopassati.text = getData.tipoViaggio
            detailPartecipantipassati.text = getData.partecipanti.toString()
            detailPartecipantiMaxpassati.text = getData.partecipantiMax.toString()
            //detailAffinitapassati.text = getData.affinita
            detailDescrizionepassati.text = getData.descrizione
        }
    }
}
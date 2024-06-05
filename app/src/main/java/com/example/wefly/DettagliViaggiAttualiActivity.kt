package com.example.wefly

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DettagliViaggiAttualiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContentView(R.layout.activity_dettagli_viaggi_attuali)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val getData = intent.getParcelableExtra<DataElencoViaggi>("attuali")
        if (getData != null) {
            // val detailImageIdAttuali : ImageView = findViewById(R.id.title_image)
            val detailTitoloViaggioAttuali : TextView = findViewById(R.id.titolo)
            val detailCittaAttuali : TextView = findViewById(R.id.citta)
            val detailNazioneAttuali : TextView = findViewById(R.id.nazione)
            val detailDataPartenzaAttuali : TextView = findViewById(R.id.partenza)
            val detailDataRitornoAttuali : TextView = findViewById(R.id.ritorno)
            val detailBudgetAttuali : TextView = findViewById(R.id.budget)
            //val detailTipoViaggioAttuali : TextView = findViewById(R.id.TipoViaggio)
            val detailPartecipantiAttuali : TextView = findViewById(R.id.partecipanti)
            val detailPartecipantiMaxAttuali : TextView = findViewById(R.id.partecipanti_max)
            //val detailAffinitaAttuali : TextView = findViewById(R.id.affinita)
            val detailDescrizioneAttuali : TextView = findViewById(R.id.descrizione)

            //detailImageIdAttuali.setImageResource(getData.titoloImmagine)
            detailTitoloViaggioAttuali.text = getData.titoloViaggio
            detailCittaAttuali.text = getData.citta
            detailNazioneAttuali.text = getData.nazione
            detailDataPartenzaAttuali.text = getData.dataPartenza
            detailDataRitornoAttuali.text = getData.dataRitorno
            detailBudgetAttuali.text = getData.budget
            //detailTipoViaggioAttuali.text = getData.tipoViaggio
            detailPartecipantiAttuali.text = getData.partecipanti.toString()
            detailPartecipantiMaxAttuali.text = getData.partecipantiMax.toString()
            //detailAffinitaAttuali.text = getData.affinita
            detailDescrizioneAttuali.text = getData.descrizione
        }
    }
}
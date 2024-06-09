package com.example.wefly.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.wefly.R
import com.example.wefly.firebase.DataFirebase
import com.example.wefly.model.ModelElencoViaggi

class DettagliViaggiAttualiActivity : AppCompatActivity() {



    // scelte dell'utente
    private var scelta1U = false
    private var scelta2U = false
    private var scelta3U = false
    private var scelta4U = false
    private var scelta5U = false
    private var scelta6U = false
    private var scelta7U = false
    private var scelta8U = false
    private var scelta9U = false
    private var scelta10U = false

    // scelte del viaggio
    private var scelta1V = false
    private var scelta2V = false
    private var scelta3V = false
    private var scelta4V = false
    private var scelta5V = false
    private var scelta6V = false
    private var scelta7V = false
    private var scelta8V = false
    private var scelta9V = false
    private var scelta10V = false

    private lateinit var getData : ModelElencoViaggi

    private lateinit var sceltaListV : MutableList<Boolean>
    private lateinit var sceltaListU : MutableList<Boolean>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContentView(R.layout.activity_dettagli_viaggi_attuali)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        getData = intent.getParcelableExtra<ModelElencoViaggi>("attuali")!!
        if (getData != null) {
            val detailImageIdAttuali : ImageView = findViewById(R.id.title_image)
            val detailTitoloViaggioAttuali : TextView = findViewById(R.id.titolo)
            val detailCittaAttuali : TextView = findViewById(R.id.citta)
            val detailNazioneAttuali : TextView = findViewById(R.id.nazione)
            val detailDataPartenzaAttuali : TextView = findViewById(R.id.partenza)
            val detailDataRitornoAttuali : TextView = findViewById(R.id.ritorno)
            val detailBudgetAttuali : TextView = findViewById(R.id.budget)
            val detailTipoViaggioAttuali : TextView = findViewById(R.id.TipoViaggio)
            val detailPartecipantiAttuali : TextView = findViewById(R.id.partecipanti)
            val detailPartecipantiMaxAttuali : TextView = findViewById(R.id.partecipanti_max)
            val detailAffinitaAttuali : TextView = findViewById(R.id.affinita)
            val detailDescrizioneAttuali : TextView = findViewById(R.id.descrizione)

            Glide.with(this) // Use the context from the holder's itemView
                .load(getData.imageUrl)
                .fitCenter() // Corrected the method name
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.placeholder)
                .into(detailImageIdAttuali)

            // setta detailTipoViaggio
            detailTipoViaggioAttuali.text = calcolaTipoViaggio()

            //setta detailAffinita
            detailAffinitaAttuali.text = calcolaAffinita()

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


    private fun calcolaTipoViaggio() : String {
        var tipoViaggio = ""
        sceltaListV = mutableListOf<Boolean>()
        scelta1V = getData.scelta1
        scelta2V = getData.scelta2
        scelta3V = getData.scelta3
        scelta4V = getData.scelta4
        scelta5V = getData.scelta5
        scelta6V = getData.scelta6
        scelta7V = getData.scelta7
        scelta8V = getData.scelta8
        scelta9V = getData.scelta9
        scelta10V = getData.scelta10

        sceltaListV = mutableListOf(scelta1V, scelta2V, scelta3V, scelta4V, scelta5V, scelta6V, scelta7V, scelta8V, scelta9V, scelta10V)

        for (i in sceltaListV.indices) {
            if (sceltaListV[i]) {
                // Get the resource ID dynamically
                val resId = resources.getIdentifier("scelta${i + 1}", "string", packageName)

                // Use getString() to get the actual string value
                if (resId != 0) {
                    val interesse = getString(resId)
                    tipoViaggio += interesse +", "
                }
            }
        }

        tipoViaggio.dropLast(2)
        return tipoViaggio
    }

    private fun calcolaAffinita() : String {

        sceltaListU = mutableListOf<Boolean>()
        val sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)
        var count = 0

        scelta1U = sharedPreferences.getBoolean("scelta1", false)
        scelta2U = sharedPreferences.getBoolean("scelta2", false)
        scelta3U = sharedPreferences.getBoolean("scelta3", false)
        scelta4U = sharedPreferences.getBoolean("scelta4", false)
        scelta5U = sharedPreferences.getBoolean("scelta5", false)
        scelta6U = sharedPreferences.getBoolean("scelta6", false)
        scelta7U = sharedPreferences.getBoolean("scelta7", false)
        scelta8U = sharedPreferences.getBoolean("scelta8", false)
        scelta9U = sharedPreferences.getBoolean("scelta9", false)
        scelta10U = sharedPreferences.getBoolean("scelta10", false)

        sceltaListU = mutableListOf(scelta1U, scelta2U, scelta3U, scelta4U, scelta5U, scelta6U, scelta7U, scelta8U, scelta9U, scelta10U)


        for(i in 0..9){
            if(sceltaListU[i]){
                if(sceltaListU[i] == sceltaListV[i]){
                    count++
                }
            }
        }

        sceltaListU.removeAll { it == false }

        var affinita = ((count.toDouble()/sceltaListU.size.toDouble())*100).toInt()

        if(0 <= affinita && affinita <= 25){
            return getString(R.string.affinita1)
        }
        if(25 < affinita && affinita <= 50){
            return getString(R.string.affinita2)
        }
        if(50 < affinita && affinita <= 75){
            return getString(R.string.affinita3)
        }
        if(75 < affinita && affinita <= 100){
            return getString(R.string.affinita4)
        }

        return ""
    }


}
package com.example.wefly

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ViaggiPassatiActivity : AppCompatActivity() {

    // dichiarazione variables per Viaggi passati

    private lateinit var adapterPassati : AdapterViaggiPassati // dichiarazione dell'adapter
    private lateinit var recyclerViewPassati : RecyclerView // dichiarazione della recyclerView
    private lateinit var viaggiPassatiArrayList : ArrayList<DataViaggiPassati> // ArrayList di objects

    // lateinit var imageIdPassati : Array<Int>
    //lateinit var tipoViaggioPassati : Array<String>
    //lateinit var affinitaPassati : Array<String>

    lateinit var titoloViaggioPassati : Array<String>
    lateinit var cittaPassati : Array<String>
    lateinit var nazionePassati : Array<String>
    lateinit var dataPartenzaPassati : Array<String>
    lateinit var dataRitornoPassati : Array<String>
    lateinit var budgetPassati : Array<String>
    lateinit var partecipantiPassati : Array<Int>
    lateinit var partecipantiMaxPassati : Array<Int>
    lateinit var descrizionePassati : Array<String>
    lateinit var viaggiPassati : Array<String>

    lateinit var scelta1Passati : Array<Boolean>
    lateinit var scelta2Passati : Array<Boolean>
    lateinit var scelta3Passati : Array<Boolean>
    lateinit var scelta4Passati : Array<Boolean>
    lateinit var scelta5Passati : Array<Boolean>
    lateinit var scelta6Passati : Array<Boolean>
    lateinit var scelta7Passati : Array<Boolean>
    lateinit var scelta8Passati : Array<Boolean>
    lateinit var scelta9Passati : Array<Boolean>
    lateinit var scelta10Passati : Array<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContentView(R.layout.activity_viaggi_passati)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //set the text for the tool bar

        val title = findViewById<TextView>(R.id.toolbar_title)
        title.text = "Viaggi Passati"

        dataInitializePassati()
        val layoutManagerPassati = LinearLayoutManager(this)
        recyclerViewPassati = findViewById(R.id.recyclerViewViaggiPassati)
        recyclerViewPassati.layoutManager = layoutManagerPassati
        recyclerViewPassati.setHasFixedSize(true)
        adapterPassati = AdapterViaggiPassati(viaggiPassatiArrayList)
        recyclerViewPassati.adapter = adapterPassati

        adapterPassati.onItemClick = {
            val intentPassati = Intent(this, DettagliViaggiPassatiActivity::class.java)
            intentPassati.putExtra("passati", it)
            startActivity(intentPassati)
        }

    }

    private fun dataInitializePassati(){

        viaggiPassatiArrayList = arrayListOf<DataViaggiPassati>()

        titoloViaggioPassati = arrayOf (
            "Londra",
            "Londra",
            "Londra"
        )

        cittaPassati = arrayOf(
            "Londra",
            "Maldive",
            "Parigi"
        )

        nazionePassati = arrayOf(
            "Regno Unito",
            "Asia",
            "Francia"
        )

        dataPartenzaPassati = arrayOf(
            "20/12/2022",
            "20/12/2022",
            "20/12/2022"
        )

        dataRitornoPassati = arrayOf(
            "30/03/2022",
            "30/03/2022",
            "30/03/2022"
        )

        budgetPassati = arrayOf(
            "€1000",
            "€2000",
            "€3000"
        )

        partecipantiPassati = arrayOf(
            3,
            3,
            3
        )

        partecipantiMaxPassati = arrayOf(
            7,
            7,
            7
        )



        descrizionePassati = arrayOf(
            "Descrizione 1",
            "Descrizione 2",
            "Descrizione 3"
        )

        viaggiPassati = arrayOf(
            "Viaggio 1",
            "Viaggio 2",
            "Viaggio 3"
        )

        scelta1Passati = arrayOf(
            true,
            true,
            true
        )

        scelta2Passati = arrayOf(
            true,
            true,
            true
        )

        scelta3Passati = arrayOf(
            true,
            true,
            true
        )

        scelta4Passati = arrayOf(
            false,
            false,
            false
        )

        scelta5Passati = arrayOf(
            false,
            false,
            false
        )

        scelta6Passati = arrayOf(
            false,
            false,
            false
        )

        scelta7Passati = arrayOf(
            false,
            false,
            false
        )

        scelta8Passati = arrayOf(
            false,
            false,
            false
        )

        scelta9Passati = arrayOf(
            false,
            false,
            false
        )

        scelta10Passati = arrayOf(
            false,
            false,
            false
        )

        for (i in titoloViaggioPassati.indices){

            val viaggiPassati = DataViaggiPassati(titoloViaggioPassati[i], budgetPassati[i], nazionePassati[i], cittaPassati[i], dataPartenzaPassati[i], dataRitornoPassati[i], partecipantiPassati[i], partecipantiMaxPassati[i], descrizionePassati[i], scelta1Passati[i], scelta2Passati[i], scelta3Passati[i], scelta4Passati[i], scelta5Passati[i], scelta6Passati[i], scelta7Passati[i], scelta8Passati[i], scelta9Passati[i], scelta10Passati[i])
            viaggiPassatiArrayList.add(viaggiPassati)
        }

    }
}
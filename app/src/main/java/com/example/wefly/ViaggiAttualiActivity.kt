package com.example.wefly

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class ViaggiAttualiActivity : AppCompatActivity() {

    // dichiarazione variables per Viaggi attuali

    private lateinit var adapterAttuali : AdapterViaggiAttuali // dichiarazione dell'adapter
    private lateinit var recyclerViewAttuali : RecyclerView // dichiarazione della recyclerView
    private lateinit var viaggiAttualiArrayList : ArrayList<DataViaggiAttuali> // ArrayList di objects

    // lateinit var imageIdAttuali : Array<Int>
    // lateinit var affinitaAttuali : Array<String>
    //lateinit var tipoViaggioAttuali : Array<String>

    lateinit var titoloViaggioAttuali : Array<String>
    lateinit var cittaAttuali : Array<String>
    lateinit var nazioneAttuali : Array<String>
    lateinit var dataPartenzaAttuali : Array<String>
    lateinit var dataRitornoAttuali : Array<String>
    lateinit var budgetAttuali : Array<String>
    lateinit var partecipantiAttuali : Array<Int>
    lateinit var partecipantiMaxAttuali : Array<Int>
    lateinit var descrizioneAttuali : Array<String>
    lateinit var viaggiAttuali : Array<String>
    lateinit var scelta1Attuali : Array<Boolean>
    lateinit var scelta2Attuali : Array<Boolean>
    lateinit var scelta3Attuali : Array<Boolean>
    lateinit var scelta4Attuali : Array<Boolean>
    lateinit var scelta5Attuali : Array<Boolean>
    lateinit var scelta6Attuali : Array<Boolean>
    lateinit var scelta7Attuali : Array<Boolean>
    lateinit var scelta8Attuali : Array<Boolean>
    lateinit var scelta9Attuali : Array<Boolean>
    lateinit var scelta10Attuali : Array<Boolean>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContentView(R.layout.activity_viaggi_attuali)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //set the text for the tool bar

        val title = findViewById<TextView>(R.id.toolbar_title)
        title.text = "Viaggi Attuali"

        dataInitializeAttuali()
        val layoutManagerAttuali = LinearLayoutManager(this)
        recyclerViewAttuali = findViewById(R.id.recyclerViewViaggiAttuali)
        recyclerViewAttuali.layoutManager = layoutManagerAttuali
        recyclerViewAttuali.setHasFixedSize(true)
        adapterAttuali = AdapterViaggiAttuali(viaggiAttualiArrayList)
        recyclerViewAttuali.adapter = adapterAttuali

        adapterAttuali.onItemClick = {
            val intentAttuali = Intent(this, DettagliViaggiAttualiActivity::class.java)
            intentAttuali.putExtra("attuali", it)
            startActivity(intentAttuali)
        }

    }

    private fun dataInitializeAttuali(){

        viaggiAttualiArrayList = arrayListOf<DataViaggiAttuali>()

        titoloViaggioAttuali = arrayOf (
            "Londra",
            "Londra",
            "Londra"
        )

        cittaAttuali = arrayOf(
            "Londra",
            "Maldive",
            "Parigi"
        )

        nazioneAttuali = arrayOf(
            "Regno Unito",
            "Asia",
            "Francia"
        )

        dataPartenzaAttuali = arrayOf(
            "20/12/2022",
            "20/12/2022",
            "20/12/2022"
        )

        dataRitornoAttuali = arrayOf(
            "30/03/2022",
            "30/03/2022",
            "30/03/2022"
        )

        budgetAttuali = arrayOf(
            "€1000",
            "€2000",
            "€3000"
        )

        partecipantiAttuali = arrayOf(
            3,
            3,
            3
        )

        partecipantiMaxAttuali = arrayOf(
            7,
            7,
            7
        )

        descrizioneAttuali = arrayOf(
            "Descrizione 1",
            "Descrizione 2",
            "Descrizione 3"
        )

        viaggiAttuali = arrayOf(
            "Viaggio 1",
            "Viaggio 2",
            "Viaggio 3"
        )

        scelta1Attuali = arrayOf(
            true,
            true,
            true
        )

        scelta2Attuali = arrayOf(
            true,
            true,
            true
        )

        scelta3Attuali = arrayOf(
            true,
            true,
            true
        )

        scelta4Attuali = arrayOf(
            false,
            false,
            false
        )

        scelta5Attuali = arrayOf(
            false,
            false,
            false
        )

        scelta6Attuali = arrayOf(
            false,
            false,
            false
        )

        scelta7Attuali = arrayOf(
            false,
            false,
            false
        )

        scelta8Attuali = arrayOf(
            false,
            false,
            false
        )

        scelta9Attuali = arrayOf(
            false,
            false,
            false
        )

        scelta10Attuali = arrayOf(
            false,
            false,
            false
        )

        for (i in titoloViaggioAttuali.indices){

            val viaggiAttuali = DataViaggiAttuali(titoloViaggioAttuali[i], budgetAttuali[i], nazioneAttuali[i], cittaAttuali[i], dataPartenzaAttuali[i], dataRitornoAttuali[i], partecipantiAttuali[i], partecipantiMaxAttuali[i], descrizioneAttuali[i], scelta1Attuali[i], scelta2Attuali[i], scelta3Attuali[i], scelta4Attuali[i], scelta5Attuali[i], scelta6Attuali[i], scelta7Attuali[i], scelta8Attuali[i], scelta9Attuali[i], scelta10Attuali[i])
            viaggiAttualiArrayList.add(viaggiAttuali)
        }

    }
}
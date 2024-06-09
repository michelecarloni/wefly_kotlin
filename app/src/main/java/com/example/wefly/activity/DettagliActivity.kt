package com.example.wefly.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.wefly.R
import com.example.wefly.databinding.ActivityDettagliBinding
import com.example.wefly.firebase.DataFirebase
import com.example.wefly.model.ModelElencoViaggi
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DettagliActivity : AppCompatActivity() {

    // firebase
    private lateinit var firebaseObject: DataFirebase

    // vid = id viaggio scelto
    private lateinit var vid : String
    private lateinit var uid : String
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var navController: NavController
    private lateinit var binding : ActivityDettagliBinding

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


    private lateinit var partecipaBtn : MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDettagliBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseObject = DataFirebase()

        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)

        getData = intent.getParcelableExtra<ModelElencoViaggi>("android")!!
        if (getData != null) {
             val detailImageId : ImageView = findViewById(R.id.title_image)
             val detailTitoloViaggio : TextView = findViewById(R.id.titolo)
             val detailCitta : TextView = findViewById(R.id.citta)
             val detailNazione : TextView = findViewById(R.id.nazione)
             val detailDataPartenza : TextView = findViewById(R.id.partenza)
             val detailDataRitorno : TextView = findViewById(R.id.ritorno)
             val detailBudget : TextView = findViewById(R.id.budget)
             val detailTipoViaggio : TextView = findViewById(R.id.TipoViaggio)
             val detailPartecipanti : TextView = findViewById(R.id.partecipanti)
             val detailPartecipantiMax : TextView = findViewById(R.id.partecipanti_max)
             val detailAffinita : TextView = findViewById(R.id.affinita)
             val detailDescrizione : TextView = findViewById(R.id.descrizione)

            // setta l'immagine
            Glide.with(this) // Use the context from the holder's itemView
                .load(getData.imageUrl)
                .fitCenter() // Corrected the method name
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.placeholder)
                .into(detailImageId)

            // setta detailTipoViaggio
            detailTipoViaggio.text = calcolaTipoViaggio()

            //setta detailAffinita
            detailAffinita.text = calcolaAffinita()

            //detailImageId.setImageResource(getData.titoloImmagine)
            detailTitoloViaggio.text = getData.titoloViaggio
            detailCitta.text = getData.citta
            detailNazione.text = getData.nazione
            detailDataPartenza.text = getData.dataPartenza
            detailDataRitorno.text = getData.dataRitorno
            detailBudget.text = getData.budget
            detailPartecipanti.text = getData.partecipanti.toString()
            detailPartecipantiMax.text = getData.partecipantiMax.toString()
            detailDescrizione.text = getData.descrizione

            vid = getData.vid
            uid = sharedPreferences.getString("uid", "").toString()

            Log.d("VIDVID", vid)
            Log.d("VIDVIDTitoloViaggio", getData.titoloViaggio)
            Log.d("VIDVIDCitta", getData.citta)
            Log.d("VIDVIDNazione", getData.nazione)
            Log.d("VIDVIDDataPartenza", getData.dataPartenza)
            Log.d("VIDVIDDataRitorno", getData.dataRitorno)
            Log.d("VIDVIDBudget", getData.budget)
            Log.d("VIDVIDPartecipanti", getData.partecipanti.toString())
            Log.d("VIDVIDPartecipantiMax", getData.partecipantiMax.toString())
            Log.d("VIDVIDDescrizione", getData.descrizione)


        }


        partecipaBtn = findViewById(R.id.partecipa_btn)
        partecipaBtn.setOnClickListener {

            overwriteDataViaggio() {
                overwriteDataUtente() {

                    val detailPartecipanti = findViewById<TextView>(R.id.partecipanti)
                    // aggiorno la UI prendendo il testo di prima ed aumentandolo di 1
                    detailPartecipanti.text = (detailPartecipanti.text.toString().toInt() + 1).toString()

                    finish()

                }
            }

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

    private fun overwriteDataViaggio(onComplete: () -> Unit) {
        val databaseReference = firebaseObject.getDatabaseViaggi().child(vid)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val partecipanti = snapshot.child("partecipanti").value.toString().toInt() + 1
                    val partecipantiStr = snapshot.child("partecipantiStr").value.toString() + uid + ", "
                    onComplete()

                    Log.d("PPP", partecipanti.toString())
                    Log.d("PPP", partecipantiStr)
                    Log.d("PPP", vid)

                    val viaggiMap = hashMapOf<String, Any>(
                        /*"imageUrl" to "",
                        "titoloViaggio" to snapshot.child("titoloViaggio").value.toString(),
                        "citta" to snapshot.child("citta").value.toString(),
                        "nazione" to snapshot.child("nazione").value.toString(),
                        "dataPartenza" to snapshot.child("dataPartenza").value.toString(),
                        "dataRitorno" to snapshot.child("dataRitorno").value.toString(),
                        "budget" to snapshot.child("budget").value.toString(),
                        "descrizione" to snapshot.child("descrizione").value.toString(),*/
                        "partecipanti" to partecipanti.toString(),
                        //"partecipantiMax" to snapshot.child("partecipantiMax").value.toString(),
                        "partecipantiStr" to partecipantiStr,
                        /*"scelta1" to snapshot.child("scelta1").value,
                        "scelta2" to snapshot.child("scelta2").value,
                        "scelta3" to snapshot.child("scelta3").value,
                        "scelta4" to snapshot.child("scelta4").value,
                        "scelta5" to snapshot.child("scelta5").value,
                        "scelta6" to snapshot.child("scelta6").value,
                        "scelta7" to snapshot.child("scelta7").value,
                        "scelta8" to snapshot.child("scelta8").value,
                        "scelta9" to snapshot.child("scelta9").value,
                        "scelta10" to snapshot.child("scelta10").value*/
                    )
                    onComplete()

                    databaseReference.updateChildren(viaggiMap)//.setValue(viaggiMap)
                    onComplete()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error", "Errore lettura dati")
            }
        })
        onComplete()
    }

    private fun overwriteDataUtente(onComplete: () -> Unit) {
        val databaseReference = firebaseObject.getDatabaseUtenti().child(uid)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var viaggiStr = snapshot.child("viaggiStr").value.toString() + vid + ", "

                    onComplete()
                    val utenteMap = hashMapOf<String, Any>(
                        /*"nome" to snapshot.child("nome").value.toString(),
                        "cognome" to snapshot.child("cognome").value.toString(),
                        "email" to snapshot.child("email").value.toString(),
                        "telefono" to snapshot.child("telefono").value.toString(),
                        "password" to snapshot.child("password").value.toString(),*/
                        "viaggiStr" to viaggiStr,
                        /*"scelta" to snapshot.child("scelta").value,
                        "scelta1" to snapshot.child("scelta1").value,
                        "scelta2" to snapshot.child("scelta2").value,
                        "scelta3" to snapshot.child("scelta3").value,
                        "scelta4" to snapshot.child("scelta4").value,
                        "scelta5" to snapshot.child("scelta5").value,
                        "scelta6" to snapshot.child("scelta6").value,
                        "scelta7" to snapshot.child("scelta7").value,
                        "scelta8" to snapshot.child("scelta8").value,
                        "scelta9" to snapshot.child("scelta9").value,
                        "scelta10" to snapshot.child("scelta10").value*/
                    )

                    onComplete()
                    databaseReference.updateChildren(utenteMap)//.setValue(utenteMap)
                    //updateSharedPreferences(viaggiStr)
                    onComplete()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error", "Errore lettura dati")
            }
        })
        onComplete()
    }

    private fun updateSharedPreferences(viaggiStr : String) {
        val editor = sharedPreferences.edit()
        editor.putString("viaggiStr", viaggiStr)
        editor.apply()
    }

}
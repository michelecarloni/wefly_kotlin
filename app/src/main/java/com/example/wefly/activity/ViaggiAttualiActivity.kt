package com.example.wefly.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wefly.R
import com.example.wefly.adapter.AdapterElencoViaggi
import com.example.wefly.databinding.ActivityViaggiAttualiBinding
import com.example.wefly.firebase.DataFirebase
import com.example.wefly.model.ModelElencoViaggi
import com.example.wefly.utils.ProgressBar
import com.example.wefly.viewmodel.ElencoViaggiViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ViaggiAttualiActivity : AppCompatActivity() {

    // firebase
    private lateinit var firebaseObject: DataFirebase

    // progressBar
    private lateinit var progressBar: ProgressBar

    // viewModel
    private lateinit var ElencoViaggiViewModel : ElencoViaggiViewModel

    private lateinit var binding: ActivityViaggiAttualiBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var adapterAttuali: AdapterElencoViaggi
    private lateinit var recyclerViewAttuali: RecyclerView
    private lateinit var viaggiAttualiArrayList: ArrayList<ModelElencoViaggi>
    private lateinit var viaggiAttualiListStr: MutableList<String>
    private lateinit var viaggiListStr: MutableList<String>

    private lateinit var dialog : Dialog

    private lateinit var vid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViaggiAttualiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ElencoViaggiViewModel = ViewModelProvider(this).get(com.example.wefly.viewmodel.ElencoViaggiViewModel::class.java)
        progressBar = ProgressBar(this)
        firebaseObject = DataFirebase()

        val title = findViewById<TextView>(R.id.toolbar_title)
        title.text = "Viaggi Attuali"



        auth = Firebase.auth
        setAllTravel{}

        val layoutManagerAttuali = LinearLayoutManager(this)
        recyclerViewAttuali = findViewById(R.id.recyclerViewViaggiAttuali)
        recyclerViewAttuali.layoutManager = layoutManagerAttuali
        recyclerViewAttuali.setHasFixedSize(true)
        viaggiAttualiArrayList = ArrayList() // Initialize here
        adapterAttuali = AdapterElencoViaggi(viaggiAttualiArrayList)
        recyclerViewAttuali.adapter = adapterAttuali

        adapterAttuali.onItemClick = {
            val intentAttuali = Intent(this, DettagliViaggiAttualiActivity::class.java)
            intentAttuali.putExtra("attuali", it)
            startActivity(intentAttuali)
        }
    }

    private fun setAllTravel(onComplete: () -> Unit) {

        progressBar.showProgressBar()

        viaggiAttualiArrayList = ArrayList() // lista di viaggi attuali
        viaggiListStr = ArrayList() // lista di stringhe
        viaggiAttualiListStr = ArrayList()

        readViaggiStr { viaggiStr ->
            if (viaggiStr.isNotEmpty()) {
                // Process viaggiStr here
                var viaggiStr = viaggiStr.dropLast(2)
                viaggiListStr = ArrayList(viaggiStr.split(", ").map { it.trim() })
            }
            onComplete()

            // all'interno di viaggiAttualiListStr ci metto tutti gli id di viaggiStr che hanno la dataRitorno maggiore di oggi
            val databaseReferenceV =firebaseObject.getDatabaseViaggi()
            databaseReferenceV.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

                        val currentDate = Calendar.getInstance()
                        currentDate.set(Calendar.HOUR_OF_DAY, 0)
                        currentDate.set(Calendar.MINUTE, 0)
                        currentDate.set(Calendar.SECOND, 0)
                        currentDate.set(Calendar.MILLISECOND, 0)

                        for (viaggiSnapshot in snapshot.children) {
                            val idViaggi = viaggiSnapshot.key.toString()
                            if (viaggiListStr.contains(idViaggi)) {
                                val dataRitornoStr =
                                    viaggiSnapshot.child("dataRitorno").value.toString()

                                try {
                                    val dataRitorno = Calendar.getInstance()
                                    dataRitorno.time = sdf.parse(dataRitornoStr)
                                    if (dataRitorno != null && (dataRitorno.after(currentDate) || dataRitorno.equals(
                                            currentDate
                                        ))
                                    ) {
                                        viaggiAttualiListStr.add(idViaggi)
                                    }
                                } catch (e: Exception) {
                                    Log.e(
                                        "DataParseError",
                                        "Error parsing date: $dataRitornoStr",
                                        e
                                    )
                                }
                            }

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@ViaggiAttualiActivity,
                        "Failed to get user data.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

            for (a in viaggiAttualiListStr) {
                Log.d("OOOO", a)
            }

            databaseReferenceV.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (viaggiSnapshot in snapshot.children) {
                            if (viaggiAttualiListStr.contains(viaggiSnapshot.key)) {
                                val vid = viaggiSnapshot.key.toString() // Extract the vid here
                                val titoloViaggioAttuali = viaggiSnapshot.child("titoloViaggio").value.toString()
                                val cittaAttuali = viaggiSnapshot.child("citta").value.toString()
                                val nazioneAttuali = viaggiSnapshot.child("nazione").value.toString()
                                val dataPartenzaAttuali = viaggiSnapshot.child("dataPartenza").value.toString()
                                val dataRitornoAttuali = viaggiSnapshot.child("dataRitorno").value.toString()
                                val budgetAttuali = viaggiSnapshot.child("budget").value.toString()
                                val partecipantiAttuali = viaggiSnapshot.child("partecipanti").value.toString().toInt()
                                val partecipantiMaxAttuali = viaggiSnapshot.child("partecipantiMax").value.toString().toInt()
                                val partecipantiStrAttuali = viaggiSnapshot.child("partecipantiStr").value.toString()
                                val descrizioneAttuali = viaggiSnapshot.child("descrizione").value.toString()
                                val scelta1Attuali = viaggiSnapshot.child("scelta1").value.toString().toBoolean()
                                val scelta2Attuali = viaggiSnapshot.child("scelta2").value.toString().toBoolean()
                                val scelta3Attuali = viaggiSnapshot.child("scelta3").value.toString().toBoolean()
                                val scelta4Attuali = viaggiSnapshot.child("scelta4").value.toString().toBoolean()
                                val scelta5Attuali = viaggiSnapshot.child("scelta5").value.toString().toBoolean()
                                val scelta6Attuali = viaggiSnapshot.child("scelta6").value.toString().toBoolean()
                                val scelta7Attuali = viaggiSnapshot.child("scelta7").value.toString().toBoolean()
                                val scelta8Attuali = viaggiSnapshot.child("scelta8").value.toString().toBoolean()
                                val scelta9Attuali = viaggiSnapshot.child("scelta9").value.toString().toBoolean()
                                val scelta10Attuali = viaggiSnapshot.child("scelta10").value.toString().toBoolean()

                                // Move the call to getPictureTravel inside this block
                                getPictureTravel(vid, object : OnImageUrlReceivedListener {
                                    override fun onImageUrlReceived(url: String) {

                                        val viaggiAttuali = ElencoViaggiViewModel.createViaggio(vid, url, titoloViaggioAttuali, budgetAttuali, nazioneAttuali, cittaAttuali, dataPartenzaAttuali, dataRitornoAttuali, partecipantiAttuali, partecipantiMaxAttuali, partecipantiStrAttuali, descrizioneAttuali, scelta1Attuali, scelta2Attuali, scelta3Attuali, scelta4Attuali, scelta5Attuali, scelta6Attuali, scelta7Attuali, scelta8Attuali, scelta9Attuali, scelta10Attuali)

                                        viaggiAttualiArrayList.add(viaggiAttuali)
                                        adapterAttuali.notifyDataSetChanged()
                                    }
                                })
                            }
                        }

                        Log.d("FIREBASE", "Data loaded successfully")
                        onComplete()
                    } else {
                        Log.d("FIREBASE", "No data found")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@ViaggiAttualiActivity,
                        "Failed to get user data.",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar.hideProgressBar()
                    onComplete()
                }
            })
            progressBar.hideProgressBar()
        }
    }

    private fun readViaggiStr(onComplete: (String) -> Unit) {
        val sharedPreferences = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        val uid = sharedPreferences.getString("uid", "")
        val databaseReference = firebaseObject.getDatabaseUtenti().child(uid ?: "")
        databaseReference.child("viaggiStr").get().addOnSuccessListener { dataSnapshot ->
            val viaggiStr = dataSnapshot.value.toString()
            onComplete(viaggiStr)
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Error getting data", exception)
            onComplete("") // Return empty string if there's an error
        }
    }

    interface OnImageUrlReceivedListener {
        fun onImageUrlReceived(url: String)
    }

    private fun getPictureTravel(vid: String, listener: OnImageUrlReceivedListener) {
        val storageReference = firebaseObject.getStorageViaggi().child("$vid.jpg")

        storageReference.downloadUrl.addOnSuccessListener { uri ->
            val url = uri.toString()
            listener.onImageUrlReceived(url)
        }.addOnFailureListener { exception ->
            exception.printStackTrace()
            listener.onImageUrlReceived("") // or handle the error as needed
        }
    }



}
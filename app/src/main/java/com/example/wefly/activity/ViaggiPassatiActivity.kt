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
import com.example.wefly.databinding.ActivityViaggiPassatiBinding
import com.example.wefly.firebase.DataFirebase
import com.example.wefly.model.ModelElencoViaggi
import com.example.wefly.utils.ProgressBar
import com.example.wefly.viewmodel.ElencoViaggiViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ViaggiPassatiActivity : AppCompatActivity() {

    // firebase
    private lateinit var firebaseObject: DataFirebase

    // progressBar
    private lateinit var progressBar: ProgressBar

    // viewModel
    private lateinit var ElencoViaggiViewModel : ElencoViaggiViewModel

    private lateinit var binding: ActivityViaggiPassatiBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var adapterPassati: AdapterElencoViaggi
    private lateinit var recyclerViewPassati: RecyclerView
    private lateinit var viaggiPassatiArrayList: ArrayList<ModelElencoViaggi>
    private lateinit var viaggiPassatiListStr: MutableList<String>
    private lateinit var viaggiListStr: MutableList<String>

    private lateinit var dialog : Dialog

    private lateinit var vid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViaggiPassatiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val title = findViewById<TextView>(R.id.toolbar_title)
        title.text = "Viaggi Passati"

        ElencoViaggiViewModel = ViewModelProvider(this).get(com.example.wefly.viewmodel.ElencoViaggiViewModel::class.java)
        progressBar = ProgressBar(this)
        firebaseObject = DataFirebase()

        auth = Firebase.auth
        setAllTravel{}

        val layoutManagerPassati = LinearLayoutManager(this)
        recyclerViewPassati = findViewById(R.id.recyclerViewViaggiPassati)
        recyclerViewPassati.layoutManager = layoutManagerPassati
        recyclerViewPassati.setHasFixedSize(true)
        viaggiPassatiArrayList = ArrayList() // Initialize here
        adapterPassati = AdapterElencoViaggi(viaggiPassatiArrayList)
        recyclerViewPassati.adapter = adapterPassati

        adapterPassati.onItemClick = {
            val intentPassati = Intent(this, DettagliViaggiPassatiActivity::class.java)
            intentPassati.putExtra("passati", it)
            startActivity(intentPassati)
        }
    }

    private fun setAllTravel(onComplete: () -> Unit) {

        progressBar.showProgressBar()

        viaggiPassatiArrayList = ArrayList() // lista di viaggi passati
        viaggiListStr = ArrayList() // lista di stringhe
        viaggiPassatiListStr = ArrayList()

        val sharedPreferences = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        val uid = sharedPreferences.getString("uid", "")
        val databaseReferenceU = firebaseObject.getDatabaseUtenti().child(uid ?: "")


        readViaggiStr { viaggiStr ->
            if (viaggiStr.isNotEmpty()) {
                // Process viaggiStr here
                var viaggiStr = viaggiStr.dropLast(2)
                viaggiListStr = ArrayList(viaggiStr.split(", ").map { it.trim() })
            }
            onComplete()

            // all'interno di viaggiPassatiListStr ci metto tutti gli id di viaggiStr che hanno la dataRitorno maggiore di oggi

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
                                    if (dataRitorno != null && dataRitorno.before(currentDate)) {
                                        viaggiPassatiListStr.add(idViaggi)
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
                        onComplete()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@ViaggiPassatiActivity,
                        "Failed to get user data.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

            databaseReferenceV.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (viaggiSnapshot in snapshot.children) {
                            if (viaggiPassatiListStr.contains(viaggiSnapshot.key.toString())) {
                                val vid = viaggiSnapshot.key.toString() // Extract the vid here
                                val titoloViaggioPassati = viaggiSnapshot.child("titoloViaggio").value.toString()
                                val cittaPassati = viaggiSnapshot.child("citta").value.toString()
                                val nazionePassati = viaggiSnapshot.child("nazione").value.toString()
                                val dataPartenzaPassati = viaggiSnapshot.child("dataPartenza").value.toString()
                                val dataRitornoPassati = viaggiSnapshot.child("dataRitorno").value.toString()
                                val budgetPassati = viaggiSnapshot.child("budget").value.toString()
                                val partecipantiPassati = viaggiSnapshot.child("partecipanti").value.toString().toInt()
                                val partecipantiMaxPassati = viaggiSnapshot.child("partecipantiMax").value.toString().toInt()
                                val partecipantiStrPassati = viaggiSnapshot.child("partecipantiStr").value.toString()
                                val descrizionePassati = viaggiSnapshot.child("descrizione").value.toString()
                                val scelta1Passati = viaggiSnapshot.child("scelta1").value.toString().toBoolean()
                                val scelta2Passati = viaggiSnapshot.child("scelta2").value.toString().toBoolean()
                                val scelta3Passati = viaggiSnapshot.child("scelta3").value.toString().toBoolean()
                                val scelta4Passati = viaggiSnapshot.child("scelta4").value.toString().toBoolean()
                                val scelta5Passati = viaggiSnapshot.child("scelta5").value.toString().toBoolean()
                                val scelta6Passati = viaggiSnapshot.child("scelta6").value.toString().toBoolean()
                                val scelta7Passati = viaggiSnapshot.child("scelta7").value.toString().toBoolean()
                                val scelta8Passati = viaggiSnapshot.child("scelta8").value.toString().toBoolean()
                                val scelta9Passati = viaggiSnapshot.child("scelta9").value.toString().toBoolean()
                                val scelta10Passati = viaggiSnapshot.child("scelta10").value.toString().toBoolean()

                                // Move the call to getPictureTravel inside this block
                                getPictureTravel(vid, object : OnImageUrlReceivedListener {
                                    override fun onImageUrlReceived(url: String) {

                                        val viaggiPassati = ElencoViaggiViewModel.createViaggio(vid, url, titoloViaggioPassati, budgetPassati, nazionePassati, cittaPassati, dataPartenzaPassati, dataRitornoPassati, partecipantiPassati, partecipantiMaxPassati, partecipantiStrPassati, descrizionePassati, scelta1Passati, scelta2Passati, scelta3Passati, scelta4Passati, scelta5Passati, scelta6Passati, scelta7Passati, scelta8Passati, scelta9Passati, scelta10Passati)

                                        viaggiPassatiArrayList.add(viaggiPassati)
                                        adapterPassati.notifyDataSetChanged()
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
                        this@ViaggiPassatiActivity,
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
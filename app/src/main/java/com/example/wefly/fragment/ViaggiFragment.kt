package com.example.wefly.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wefly.activity.DettagliActivity
import com.example.wefly.R
import com.example.wefly.adapter.AdapterElencoViaggi
import com.example.wefly.databinding.FragmentViaggiBinding
import com.example.wefly.firebase.DataFirebase
import com.example.wefly.model.ModelElencoViaggi
import com.example.wefly.utils.ProgressBar
import com.example.wefly.viewmodel.ElencoViaggiViewModel
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ViaggiFragment : Fragment() {

    // firebase
    private lateinit var firebaseObject: DataFirebase

    // ProgressBar
    private lateinit var progressBar : ProgressBar

    // viewModel
    private lateinit var ElencoViaggiViewModel : ElencoViaggiViewModel

    private lateinit var binding: FragmentViaggiBinding
    private lateinit var navController: NavController

    private lateinit var adapter: AdapterElencoViaggi
    private lateinit var recyclerView: RecyclerView
    private lateinit var viaggiArrayList: ArrayList<ModelElencoViaggi>
    //private lateinit var dataPickerBtn: MaterialButton

    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference

    private lateinit var nazioneRicevuta :  String
    private lateinit var viaggiListStr : ArrayList<String>

    private lateinit var dialog : Dialog

    private lateinit var vid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViaggiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nazioneRicevuta = arguments?.getString("nazione").toString()

        ElencoViaggiViewModel = ViewModelProvider(this).get(com.example.wefly.viewmodel.ElencoViaggiViewModel::class.java)

        progressBar = ProgressBar(this@ViaggiFragment.requireContext())
        firebaseObject = DataFirebase()

        // Set the text for the toolbar
        val title = view.findViewById<TextView>(R.id.toolbar_title)
        title.text = "Viaggi"


        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        viaggiArrayList = ArrayList()
        adapter = AdapterElencoViaggi(viaggiArrayList)
        recyclerView.adapter = adapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

        adapter.onItemClick = {
            val intent = Intent(requireContext(), DettagliActivity::class.java)
            intent.putExtra("android", it)
            val navController = findNavController() // Assuming nav_host_fragment is your NavHostFragment
            navController.navigateUp()
            startActivity(intent)
        }

        fetchDataFromFirebase{}

    }

    private fun fetchDataFromFirebase(onComplete: () -> Unit) {
        progressBar.showProgressBar()

        databaseReference = firebaseObject.getDatabaseViaggi()

        // leggo viaggiStr dell'utente e la metto in una lista di stringhe

        viaggiListStr = ArrayList()

        readViaggiStr { viaggiStr ->
            if (viaggiStr.isNotEmpty()) {
                // Process viaggiStr here
                var viaggiStr = viaggiStr.dropLast(2)
                viaggiListStr = ArrayList(viaggiStr.split(", ").map { it.trim() })
            }
            onComplete()


            Log.d("viaggiVF", viaggiListStr.toString())

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (viaggiSnapshot in snapshot.children) {
                            val nazione = viaggiSnapshot.child("nazione").value.toString()
                            if (nazione == nazioneRicevuta) {

                                //check sulla dataPartenza
                                val dataPartenzaStr = viaggiSnapshot.child("dataPartenza").value.toString()

                                val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

                                // prendo la current date
                                val currentDate = Calendar.getInstance()
                                currentDate.set(Calendar.HOUR_OF_DAY, 0)
                                currentDate.set(Calendar.MINUTE, 0)
                                currentDate.set(Calendar.SECOND, 0)
                                currentDate.set(Calendar.MILLISECOND, 0)

                                // creo data inizio come calendar
                                val dataPartenza = Calendar.getInstance()
                                dataPartenza.time = sdf.parse(dataPartenzaStr)
                                if(dataPartenza != null && dataPartenza.after(currentDate)){

                                    //check se i partecipanti sono gia al completo quindi partecipanti < partecipantiMax
                                    val partecipanti = viaggiSnapshot.child("partecipanti").value.toString().toInt()
                                    val partecipantiMax = viaggiSnapshot.child("partecipantiMax").value.toString().toInt()

                                    if(partecipanti < partecipantiMax){
                                        if (!viaggiListStr.contains(viaggiSnapshot.key.toString())) {
                                            val vid = viaggiSnapshot.key.toString() // Extract the vid here

                                            // Move the code to fetch the picture inside this block
                                            getPictureTravel(vid, object :
                                                OnImageUrlReceivedListener {
                                                override fun onImageUrlReceived(url: String) {
                                                    Log.d("VIDVIDVF", vid) // Log the correct vid
                                                    val titoloViaggio = viaggiSnapshot.child("titoloViaggio").value.toString()
                                                    val citta = viaggiSnapshot.child("citta").value.toString()
                                                    val dataPartenza = viaggiSnapshot.child("dataPartenza").value.toString()
                                                    val dataRitorno = viaggiSnapshot.child("dataRitorno").value.toString()
                                                    val budget = viaggiSnapshot.child("budget").value.toString()
                                                    val partecipanti = viaggiSnapshot.child("partecipanti").value.toString().toInt()
                                                    val partecipantiMax = viaggiSnapshot.child("partecipantiMax").value.toString().toInt()
                                                    val partecipantiStr = viaggiSnapshot.child("partecipantiStr").value.toString()
                                                    val descrizione = viaggiSnapshot.child("descrizione").value.toString()
                                                    val scelta1 = viaggiSnapshot.child("scelta1").value.toString().toBoolean()
                                                    val scelta2 = viaggiSnapshot.child("scelta2").value.toString().toBoolean()
                                                    val scelta3 = viaggiSnapshot.child("scelta3").value.toString().toBoolean()
                                                    val scelta4 = viaggiSnapshot.child("scelta4").value.toString().toBoolean()
                                                    val scelta5 = viaggiSnapshot.child("scelta5").value.toString().toBoolean()
                                                    val scelta6 = viaggiSnapshot.child("scelta6").value.toString().toBoolean()
                                                    val scelta7 = viaggiSnapshot.child("scelta7").value.toString().toBoolean()
                                                    val scelta8 = viaggiSnapshot.child("scelta8").value.toString().toBoolean()
                                                    val scelta9 = viaggiSnapshot.child("scelta9").value.toString().toBoolean()
                                                    val scelta10 = viaggiSnapshot.child("scelta10").value.toString().toBoolean()

                                                    val viaggiAttuali = ElencoViaggiViewModel.createViaggio(vid, url, titoloViaggio, budget, nazione, citta, dataPartenza, dataRitorno, partecipanti, partecipantiMax, partecipantiStr, descrizione, scelta1, scelta2, scelta3, scelta4, scelta5, scelta6, scelta7, scelta8, scelta9, scelta10)

                                                    viaggiArrayList.add(viaggiAttuali)
                                                    adapter.notifyDataSetChanged()
                                                }
                                            })
                                        }
                                    }

                                }

                            }
                        }

                        progressBar.hideProgressBar()
                        onComplete()
                        Log.d("FIREBASE", "Data loaded successfully")
                    } else {
                        Log.d("FIREBASE", "No data found")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to get user data.",
                        Toast.LENGTH_SHORT
                    ).show()

                    progressBar.hideProgressBar()
                    onComplete()
                }
            })

        }
    }

    private fun readViaggiStr(onComplete: (String) -> Unit) {
        val sharedPreferences = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
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
        storageReference = firebaseObject.getStorageViaggi().child("$vid.jpg")

        storageReference.downloadUrl.addOnSuccessListener { uri ->
            val url = uri.toString()
            listener.onImageUrlReceived(url)
        }.addOnFailureListener { exception ->
            exception.printStackTrace()
            listener.onImageUrlReceived("") // or handle the error as needed
        }
    }

    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<ModelElencoViaggi>()
            for (item in viaggiArrayList) {
                if (item.titoloViaggio.lowercase(Locale.ROOT).contains(query, true) ||
                    item.citta.lowercase(Locale.ROOT).contains(query, true)) {
                    filteredList.add(item)
                }
            }
            adapter.setFilteredList(filteredList)
        }
    }



}
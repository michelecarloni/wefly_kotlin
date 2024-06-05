package com.example.wefly

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar
import java.util.Locale

class ViaggiFragment : Fragment() {

    private lateinit var adapter: AdapterElencoViaggi
    private lateinit var recyclerView: RecyclerView
    private lateinit var viaggiArrayList: ArrayList<DataElencoViaggi>
    private lateinit var searchView: SearchView
    private lateinit var dataPickerBtn: MaterialButton
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clearFragmentState()

        /*// Initialize Firebase Database and Storage
        database = FirebaseDatabase.getInstance().getReference("ViaggiTotali")
        storage = FirebaseStorage.getInstance()

        viaggiArrayList = ArrayList()
        adapter = AdapterElencoViaggi()*/
    }

    private fun clearFragmentState() {
        val sharedPreferences = requireActivity().getSharedPreferences("popUpFiltersPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_viaggi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inflate the layout for the fab filter button
        val filtersFab = view.findViewById<View>(R.id.filters_fab)
        filtersFab.setOnClickListener {
            val filtersPopUp = popUpFiltersFragment()
            filtersPopUp.show(this.parentFragmentManager, "FiltersPopUp")
        }

        // Inflate the layout for dataPickerBtn
        dataPickerBtn = view.findViewById(R.id.data_picker_btn)
        dataPickerBtn.setOnClickListener {
            val picker = MaterialDatePicker.Builder.dateRangePicker()
                .setTheme(R.style.ThemeMaterialCalendar)
                .setTitleText("Seleziona la data")
                .build()

            picker.show(this.parentFragmentManager, "TAG")
            picker.addOnPositiveButtonClickListener { selection ->
                val startDateMillis = selection.first
                val endDateMillis = selection.second
                logDateRange(startDateMillis, endDateMillis)
            }
            picker.addOnNegativeButtonClickListener { picker.dismiss() }
        }

        // Set the text for the toolbar
        val title = view.findViewById<TextView>(R.id.toolbar_title)
        title.text = "Viaggi"


        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = AdapterElencoViaggi(viaggiArrayList)
        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

        /*adapter.onItemClick = {
            val intent = Intent(requireContext(), DettagliActivity::class.java)
            intent.putExtra("android", it)
            startActivity(intent)
        }*/

        fetchDataFromFirebase()
    }

    private fun logDateRange(startDateMillis: Long, endDateMillis: Long) {
        val startDateCalendar = Calendar.getInstance().apply { timeInMillis = startDateMillis }
        val endDateCalendar = Calendar.getInstance().apply { timeInMillis = endDateMillis }
        val dateStartDay = startDateCalendar.get(Calendar.DAY_OF_MONTH)
        val dateStartMonth = startDateCalendar.get(Calendar.MONTH) + 1
        val dateStartYear = startDateCalendar.get(Calendar.YEAR)
        val dateEndDay = endDateCalendar.get(Calendar.DAY_OF_MONTH)
        val dateEndMonth = endDateCalendar.get(Calendar.MONTH) + 1
        val dateEndYear = endDateCalendar.get(Calendar.YEAR)
        Log.d("SendMessage", "Ricevuta $dateStartDay/$dateStartMonth/$dateStartYear")
        Log.d("SendMessage", "Ricevuta $dateEndDay/$dateEndMonth/$dateEndYear")
    }

    private fun fetchDataFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viaggiArrayList.clear()
                for (dataSnapshot in snapshot.children) {
                    val data = dataSnapshot.getValue(DataElencoViaggi::class.java)
                    if (data != null) {
                        viaggiArrayList.add(data)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read data", error.toException())
            }
        })
    }

    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<DataElencoViaggi>()
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





/*package com.example.wefly

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.slider.RangeSlider
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Currency
import java.util.Locale
import java.util.TimeZone


class ViaggiFragment : Fragment(){

    // dichiarazione variables recycler view
    private lateinit var adapter : AdapterElencoViaggi // dichiarazione dell'adapter
    private lateinit var recyclerView : RecyclerView // dichiarazione della recyclerView
    private lateinit var viaggiArrayList : ArrayList<DataElencoViaggi> // ArrayList di objects



    // lateinit var imageId : Array<Int>
    // lateinit var tipoViaggio : Array<String>
    // lateinit var affinita : Array<String>
    lateinit var titoloViaggio : Array<String>
    lateinit var citta : Array<String>
    lateinit var nazione : Array<String>
    lateinit var dataPartenza : Array<String>
    lateinit var dataRitorno : Array<String>
    lateinit var budget : Array<String>
    lateinit var partecipanti : Array<Int>
    lateinit var partecipantiMax : Array<Int>
    lateinit var descrizione : Array<String>
    lateinit var viaggi : Array<String>

    lateinit var scelta1 : Array<Boolean>
    lateinit var scelta2 : Array<Boolean>
    lateinit var scelta3 : Array<Boolean>
    lateinit var scelta4 : Array<Boolean>
    lateinit var scelta5 : Array<Boolean>
    lateinit var scelta6 : Array<Boolean>
    lateinit var scelta7 : Array<Boolean>
    lateinit var scelta8 : Array<Boolean>
    lateinit var scelta9 : Array<Boolean>
    lateinit var scelta10 : Array<Boolean>

    // dichiarazione searchView
    private lateinit var searchView : SearchView

    // dichiarazione dataPickerBtn
    private lateinit var dataPickerBtn : MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clearFragmentState()
    }

    private fun clearFragmentState() {
        val sharedPreferences = requireActivity().getSharedPreferences("popUpFiltersPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_viaggi, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // DA RIGUARDARE
        val bundle = arguments

        val checkbox1 = bundle?.getBoolean("checkbox1")
        val checkbox2 = bundle?.getBoolean("checkbox2")
        val checkbox3 = bundle?.getBoolean("checkbox3")
        val checkbox4 = bundle?.getBoolean("checkbox4")

        Log.d("checkbox1", checkbox1.toString())
        Log.d("checkbox2", checkbox2.toString())
        Log.d("checkbox3", checkbox3.toString())
        Log.d("checkbox4", checkbox4.toString())

        //inflate the layout for the fab filter button
        val filtersFab = view.findViewById<View>(R.id.filters_fab)

        filtersFab.setOnClickListener {
            val filtersPopUp = popUpFiltersFragment()
            filtersPopUp.show(this.parentFragmentManager, "FiltersPopUp")
        }

        //inflate the layout for dataPickerBtn
        dataPickerBtn = view.findViewById(R.id.data_picker_btn)

        dataPickerBtn.setOnClickListener {
            val picker = MaterialDatePicker.Builder.dateRangePicker()
                .setTheme(R.style.ThemeMaterialCalendar)
                .setTitleText("Seleziona la data")
                .build()

            picker.show(this.parentFragmentManager, "TAG")

            picker.addOnPositiveButtonClickListener {
                    selection ->
                val startDateMillis = selection.first
                val endDateMillis = selection.second

                // Convert milliseconds to Calendar objects
                val startDateCalendar = Calendar.getInstance().apply {
                    timeInMillis = startDateMillis
                }
                val endDateCalendar = Calendar.getInstance().apply {
                    timeInMillis = endDateMillis
                }

                // Extract day, month, and year from Calendar objects
                val dateStartDay = startDateCalendar.get(Calendar.DAY_OF_MONTH)
                val dateStartMonth = startDateCalendar.get(Calendar.MONTH) + 1
                val dateStartYear = startDateCalendar.get(Calendar.YEAR)

                val dateEndDay = endDateCalendar.get(Calendar.DAY_OF_MONTH)
                val dateEndMonth = endDateCalendar.get(Calendar.MONTH) + 1
                val dateEndYear = endDateCalendar.get(Calendar.YEAR)

                Log.d("SendMessage", "Ricevuta $dateStartDay/$dateStartMonth/$dateStartYear")
                Log.d("SendMessage", "Ricevuta $dateEndDay/$dateEndMonth/$dateEndYear")
            }

            picker.addOnNegativeButtonClickListener{
                picker.dismiss()
            }
        }


        //set the text for the tool bar
        val title = view.findViewById<TextView>(R.id.toolbar_title)
        title.text = "Viaggi"

        searchView = view.findViewById(R.id.searchView)

        dataPartenzaInitialize()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = AdapterElencoViaggi(viaggiArrayList)
        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

    }

    private fun convertTimeToDate(time: Long): String {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utc.timeInMillis = time
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return format.format((utc.time))
    }

    private fun filterList(query: String?) {

        if (query != null){
            val filteredList = ArrayList<DataElencoViaggi>()
            for (i in viaggiArrayList){
                if(i.titoloViaggio.lowercase(Locale.ROOT).contains(query, ignoreCase = true) || i.citta.lowercase(Locale.ROOT).contains(query, ignoreCase = true)){
                    filteredList.add(i)
                }
            }

            if(!filteredList.isEmpty()){
                adapter.setFilteredList(filteredList)
            }
            else{
                //Toast.makeText(context, "No Data Found..", Toast.LENGTH_SHORT).show()
            }
        }

        adapter.onItemClick = {
            val intent = Intent(requireContext(), DettagliActivity::class.java)
            intent.putExtra("android", it)
            startActivity(intent)
        }

    }

    private fun dataPartenzaInitialize(){

        viaggiArrayList = arrayListOf<DataElencoViaggi>()

        titoloViaggio = arrayOf (
            "titoloViaggio 1",
            "titoloViaggio 2",
            "titoloViaggio 3",
            "titoloViaggio 4",
            "titoloViaggio 5",
            "titoloViaggio 6",
            "titoloViaggio 7"
        )

        citta = arrayOf(
            "Londra",
            "Maldive",
            "Parigi",
            "Dubai",
            "New York",
            "Lisbona",
            "Roma"
        )

        nazione = arrayOf(
            "Regno Unito",
            "Asia",
            "Francia",
            "Emirati Arabi",
            "USA",
            "Portogallo",
            "Italia"
        )

        dataPartenza = arrayOf(
            "20/03/2022",
            "20/03/2022",
            "20/03/2022",
            "20/03/2022",
            "20/03/2022",
            "20/03/2022",
            "20/03/2022"
        )

        dataRitorno = arrayOf(
            "30/03/2022",
            "30/03/2022",
            "30/03/2022",
            "30/03/2022",
            "30/03/2022",
            "30/03/2022",
            "30/03/2022"
        )

        budget = arrayOf(
            "1000",
            "2000",
            "3000",
            "4000",
            "5000",
            "6000",
            "7000",
        )



        partecipanti = arrayOf (
            4,
            4,
            4,
            4,
            4,
            4,
            4
        )

        partecipantiMax = arrayOf(
            6,
            6,
            6,
            6,
            6,
            6,
            6
        )

        descrizione = arrayOf(
            "Descrizione 1",
            "Descrizione 2",
            "Descrizione 3",
            "Descrizione 4",
            "Descrizione 5",
            "Descrizione 6",
            "Descrizione 7",
        )

        viaggi = arrayOf(
            "Viaggio 1",
            "Viaggio 2",
            "Viaggio 3",
            "Viaggio 4",
            "Viaggio 5",
            "Viaggio 6",
            "Viaggio 7"
        )

        scelta1 = arrayOf(
            true,
            true,
            true,
            true,
            true,
            true,
            true
        )

        scelta2 = arrayOf(
            true,
            true,
            true,
            true,
            true,
            true,
            true
        )

        scelta3 = arrayOf(
            true,
            true,
            true,
            true,
            true,
            true,
            true
        )

        scelta4 = arrayOf(
            false,
            false,
            false,
            false,
            false,
            false,
            false
        )

        scelta5 = arrayOf(
            false,
            false,
            false,
            false,
            false,
            false,
            false
        )

        scelta6 = arrayOf(
            false,
            false,
            false,
            false,
            false,
            false,
            false
        )

        scelta7 = arrayOf(
            false,
            false,
            false,
            false,
            false,
            false,
            false
        )

        scelta8 = arrayOf(
            false,
            false,
            false,
            false,
            false,
            false,
            false
        )

        scelta9 = arrayOf(
            false,
            false,
            false,
            false,
            false,
            false,
            false
        )

        scelta10 = arrayOf(
            false,
            false,
            false,
            false,
            false,
            false,
            false
        )

        // maybe in this fro "titoloViaggio" is not the right whereby iterate

        for (i in titoloViaggio.indices){

            val viaggi = DataElencoViaggi(titoloViaggio[i], budget[i], nazione[i], citta[i], dataPartenza[i], dataRitorno[i], partecipanti[i], partecipantiMax[i], descrizione[i], scelta1[i], scelta2[i], scelta3[i], scelta4[i], scelta5[i], scelta6[i], scelta7[i], scelta8[i], scelta9[i], scelta10[i])
            viaggiArrayList.add(viaggi)
        }

    }


}*/
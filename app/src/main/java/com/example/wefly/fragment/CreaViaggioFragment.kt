package com.example.wefly.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.wefly.R
import com.example.wefly.model.ModelCitta
import com.example.wefly.utils.ProgressBar
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.*
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

class CreaViaggioFragment : Fragment() {

    private lateinit var progressBar: ProgressBar

    // dichiarazione dataPickerBtn
    private lateinit var dataPickerBtn: MaterialButton
    private lateinit var listCitta: MutableList<ModelCitta>

    private lateinit var titoloViaggio: String
    private lateinit var budget: String
    private lateinit var nazione: String
    private lateinit var citta: String
    private lateinit var dataPartenza: String
    private lateinit var dataRitorno: String

    private var dateStartDay = ""
    private var dateStartMonth = ""
    private var dateStartYear = ""
    private var dateEndDay = ""
    private var dateEndMonth = ""
    private var dateEndYear = ""

    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Clear state when the app starts
        clearFragmentState()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crea_viaggio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inflate the layout for dataPickerBtn
        dataPickerBtn = view.findViewById(R.id.data_picker_btn)

        progressBar = ProgressBar(this@CreaViaggioFragment.requireContext())

        dataPickerBtn.setOnClickListener {
            val picker = MaterialDatePicker.Builder.dateRangePicker()
                .setTheme(R.style.ThemeMaterialCalendar)
                .setTitleText("Seleziona la data")
                .build()

            picker.show(this.parentFragmentManager, "TAG")

            picker.addOnPositiveButtonClickListener { selection ->
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
                dateStartDay = startDateCalendar.get(Calendar.DAY_OF_MONTH).toString()
                dateStartMonth = (startDateCalendar.get(Calendar.MONTH) + 1).toString()
                dateStartYear = startDateCalendar.get(Calendar.YEAR).toString()

                dateEndDay = endDateCalendar.get(Calendar.DAY_OF_MONTH).toString()
                dateEndMonth = (endDateCalendar.get(Calendar.MONTH) + 1).toString()
                dateEndYear = endDateCalendar.get(Calendar.YEAR).toString()

                Log.d("SendMessage", "Ricevuta $dateStartDay/$dateStartMonth/$dateStartYear")
                Log.d("SendMessage", "Ricevuta $dateEndDay/$dateEndMonth/$dateEndYear")
            }

            picker.addOnNegativeButtonClickListener {
                picker.dismiss()
            }
        }

        val title = view.findViewById<TextView>(R.id.toolbar_title)
        title.text = "Crea Viaggio"

        val navController = findNavController()

        val avantiBtn = view.findViewById<MaterialButton>(R.id.avanti_btn)
        avantiBtn.setOnClickListener {
            val titoloText = view.findViewById<EditText>(R.id.titolo_et)
            titoloViaggio = titoloText.text.toString()

            val budgetText = view.findViewById<EditText>(R.id.budget_et)
            budget = budgetText.text.toString()

            dataPartenza = "$dateStartDay/$dateStartMonth/$dateStartYear"
            dataRitorno = "$dateEndDay/$dateEndMonth/$dateEndYear"

            val nazioneText = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewNazione)
            nazione = nazioneText.text.toString()

            val cittaText = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewCitta)
            citta = cittaText.text.toString()

            if (titoloViaggio.isEmpty() || budget.isEmpty() || (nazione.contains("Nazione") || nazione == "") || (citta.contains("Città") || citta == "") || dataPartenza == "" || dataRitorno == "") {
                Toast.makeText(context, "Compila tutti i campi!", Toast.LENGTH_SHORT).show()
            } else {
                // Create the bundle and put the data
                val bundle = Bundle().apply {
                    putString("titoloViaggio", titoloViaggio)
                    putString("budget", budget)
                    putString("nazione", nazione)
                    putString("citta", citta)
                    putString("dataPartenza", dataPartenza)
                    putString("dataRitorno", dataRitorno)
                }

                // Navigate to the next fragment with the bundle
                navController.navigate(R.id.action_navigation_crea_viaggio_to_creaViaggio2Fragment, bundle)
            }

        }

    }

    override fun onResume() {
        super.onResume()

        CoroutineScope(Dispatchers.Main).launch {
            progressBar.showProgressBar()
            listCitta = withContext(Dispatchers.IO) {
                readCSVFile()
            }

            // First, create a set of unique nations from the listCitta
            val nationSet = listCitta.map { it.nazione }.toSet()

            // Populate the nation dropdown
            val nationAdapter = ArrayAdapter(requireContext(),
                R.layout.dropdown_item_nazione, nationSet.toTypedArray())
            val autoCompleteTextViewNazione = view?.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewNazione)
            autoCompleteTextViewNazione?.setAdapter(nationAdapter)

            // Add an onItemClickListener to get the selected nation
            autoCompleteTextViewNazione?.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
                // After selecting the nation, update the city dropdown based on the selected nation
                if (autoCompleteTextViewNazione != null) {
                    nazione = autoCompleteTextViewNazione.text.toString()

                } else {
                    Toast.makeText(context, "Seleziona una nazione", Toast.LENGTH_SHORT).show()
                }

                updateCityDropdown()
            }

            // Populate the city dropdown initially with all cities
            updateCityDropdown()

            // Add an onItemClickListener to get the selected city
            val autoCompleteTextViewCitta = view?.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewCitta)
            autoCompleteTextViewCitta?.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
                if (autoCompleteTextViewCitta != null) {
                    citta = autoCompleteTextViewCitta.text.toString()
                } else {
                    Toast.makeText(context, "Seleziona una citta", Toast.LENGTH_SHORT).show()
                }
            }
            progressBar.hideProgressBar()
        }
    }

    private fun updateCityDropdown() {
        val selectedNation = view?.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewNazione)?.text.toString()

        // Filter cities based on the selected nation
        val filteredCities = listCitta.filter { it.nazione == selectedNation }.map { it.citta }.toTypedArray()

        // Populate the city dropdown with filtered cities
        val cityAdapter = ArrayAdapter(requireContext(),
            R.layout.dropdown_item_citta, filteredCities)
        val autoCompleteTextViewCitta = view?.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewCitta)
        autoCompleteTextViewCitta?.setAdapter(cityAdapter)
    }

    private suspend fun readCSVFile(): MutableList<ModelCitta> {
        return withContext(Dispatchers.IO) {
            val inputStream = requireContext().assets.open("cities.csv")
            val bufferReader = BufferedReader(InputStreamReader(inputStream))
            val csvParser = CSVParser.parse(bufferReader, CSVFormat.DEFAULT)

            val cittaList = mutableListOf<ModelCitta>()
            csvParser.forEach {
                it?.let {
                    val citta = ModelCitta(
                        citta = it.get(0),
                        nazione = it.get(1),
                        lat = it.get(2).toDouble(),
                        lng = it.get(3).toDouble(),
                    )
                    cittaList.add(citta)
                }
            }

            cittaList
        }
    }

    private fun clearFragmentState() {
        val sharedPreferenceCreaViaggio = requireActivity().getSharedPreferences("CreaViaggio2Prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferenceCreaViaggio.edit()
        editor.clear()
        editor.apply()
    }

    private fun convertTimeToDate(time: Long): String {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utc.timeInMillis = time
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return format.format(utc.time)
    }

}
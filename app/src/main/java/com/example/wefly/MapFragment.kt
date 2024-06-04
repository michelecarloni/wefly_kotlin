package com.example.wefly

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Calendar

class MapFragment : Fragment() {

    // dichiarazione dataPickerBtn
    private lateinit var dataPickerBtn : MaterialButton

    private lateinit var map: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Configurazione di osmdroid
        Configuration.getInstance().userAgentValue = requireActivity().applicationContext.packageName



        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        map = rootView.findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val title = rootView.findViewById<TextView>(R.id.toolbar_title)
        title.text = "Mappa"

        // Imposta il punto iniziale e il livello di zoom
        val startPoint = GeoPoint(48.8583, 2.2944) // Coordinate di esempio (Torre Eiffel)
        val mapController = map.controller
        mapController.setZoom(5.0)
        mapController.setCenter(startPoint)

        // Aggiungi un marker
        val marker = Marker(map)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(marker)



        return rootView
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
    }

    override fun onResume() {
        super.onResume()
        map.onResume() // needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onPause() {
        super.onPause()
        map.onPause() // needed for compass, my location overlays, v6.0.0 and up
    }
}
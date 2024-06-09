package com.example.wefly.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.wefly.R
import com.example.wefly.databinding.FragmentMapBinding
import com.example.wefly.firebase.DataFirebase
import com.example.wefly.model.ModelCitta
import com.example.wefly.utils.ProgressBar
import com.example.wefly.viewmodel.CittaViewModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale



class MapFragment : Fragment() {

    // firebase
    private lateinit var firebaseObject: DataFirebase

    // ProgressBar
    private lateinit var progressBar : ProgressBar

    private lateinit var CittaViewModel : CittaViewModel

    private var isFirstResume = false

    private lateinit var binding: FragmentMapBinding

    private lateinit var navController: NavController

    private lateinit var mapNazioni: MutableMap<String, String>

    private lateinit var nazione: String
    private lateinit var vid: String

    private lateinit var map: MapView

    private lateinit var viaggiListStr: ArrayList<String>

    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Configuration.getInstance().userAgentValue = requireActivity().applicationContext.packageName
        binding = FragmentMapBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        CittaViewModel = ViewModelProvider(this@MapFragment).get(com.example.wefly.viewmodel.CittaViewModel::class.java)
        progressBar = ProgressBar(this@MapFragment.requireContext())
        firebaseObject = DataFirebase()

        map = binding.root.findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        val mapController = map.controller
        mapController.setZoom(5.5)
        map.minZoomLevel = 4.5
        map.maxZoomLevel = 8.0
        map.onResume()

        mapNazioni = mutableMapOf()

        val title = binding.root.findViewById<TextView>(R.id.toolbar_title)
        title.text = "Mappa"

        progressBar.showProgressBar()

        lifecycleScope.launch {
            readCSVFile()
            readTravel()
            createGeoPoints()
            progressBar.hideProgressBar()
        }
    }

    private suspend fun readCSVFile() {
        withContext(Dispatchers.IO) {
            val inputStream = requireContext().assets.open("cities.csv")
            val bufferReader = BufferedReader(InputStreamReader(inputStream))
            val csvParser = CSVParser.parse(bufferReader, CSVFormat.DEFAULT)

            csvParser.forEach {
                it?.let {
                    val citta = CittaViewModel.createCitta(it.get(0), it.get(1), it.get(2).toDouble(), it.get(3).toDouble())
                    mapNazioni[citta.nazione] = "${citta.lat} ${citta.lng} ${0}"
                }
            }
        }
    }

    private suspend fun readTravel() {

        //scorro tutti i viaggi. Per ogni viaggio leggo la nazione. Se la nazione corrisponde ad una chiave di mapNazioni,
        // allora faccio un altro check: la data di Partenza deve essere maggiore della data attuale -> i partecipanti devono essere < dei partecipanti max
        // se il viaggio non è nella viaggiList dell'utente allora
        // incremento l'ultimo valore (count) di value. L'ultimo valore di value corrisponde al numero di viaggi attualmente creati per quella nazioni
        // ed a cui è possibile partecipare per quella nazione

        withContext(Dispatchers.IO) {
            val databaseReference = firebaseObject.getDatabaseViaggi()

            viaggiListStr = ArrayList()

            val viaggiStr = readViaggiStr()
            if (viaggiStr.isNotEmpty()) {
                viaggiListStr = ArrayList(viaggiStr.dropLast(2).split(", ").map { it.trim() })
            }

            val snapshot = databaseReference.get().await()
            if (snapshot.exists()) {
                for (travel in snapshot.children) {
                    vid = travel.key.toString()

                    for (key in mapNazioni.keys) {
                        nazione = travel.child("nazione").value.toString()

                        if (key == nazione) {
                            val dataPartenzaStr = travel.child("dataPartenza").value.toString()
                            val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

                            val currentDate = Calendar.getInstance()
                            currentDate.set(Calendar.HOUR_OF_DAY, 0)
                            currentDate.set(Calendar.MINUTE, 0)
                            currentDate.set(Calendar.SECOND, 0)
                            currentDate.set(Calendar.MILLISECOND, 0)

                            val dataPartenza = Calendar.getInstance()
                            dataPartenza.time = sdf.parse(dataPartenzaStr)

                            if (dataPartenza != null && dataPartenza.after(currentDate)) {
                                val partecipanti = travel.child("partecipanti").value.toString().toInt()
                                val partecipantiMax = travel.child("partecipantiMax").value.toString().toInt()

                                if (partecipanti < partecipantiMax) {
                                    if (!viaggiListStr.contains(vid)) {
                                        val valueListStr = mapNazioni[key]!!.split(" ")
                                        val count = valueListStr[2].toInt() + 1
                                        mapNazioni[key] = "${valueListStr[0]} ${valueListStr[1]} ${count}"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun readViaggiStr(): String {
        return withContext(Dispatchers.IO) {
            val sharedPreferences = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
            val uid = sharedPreferences.getString("uid", "")
            val databaseReference = firebaseObject.getDatabaseViaggi().child(uid ?: "")
            try {
                val dataSnapshot = databaseReference.child("viaggiStr").get().await()
                dataSnapshot.value.toString()
            } catch (e: Exception) {
                Log.e("Firebase", "Error getting data", e)
                ""
            }
        }
    }

    private fun addMarkerToMap(geoPoint: GeoPoint, nazione: String, count: Int) {
        val marker = Marker(map)
        marker.position = geoPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        val inflater = LayoutInflater.from(requireContext())
        val markerView = inflater.inflate(R.layout.marker_layout, null)
        val markerImage = markerView.findViewById<ImageView>(R.id.marker_image)
        val markerText = markerView.findViewById<TextView>(R.id.marker_text)

        markerImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.pin))
        markerText.text = count.toString()

        marker.icon = BitmapDrawable(resources, createDrawableFromView(requireContext(), markerView))

        marker.title = nazione
        map.overlays.add(marker)
        map.invalidate()

        marker.setOnMarkerClickListener { _, _ ->
            goDialog(nazione)
            true
        }
    }

    private fun createDrawableFromView(context: Context, view: View): Bitmap {
        val displayMetrics = DisplayMetrics()
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun createGeoPoints() {
        val mapController = map.controller
        for (nazione in mapNazioni.keys) {
            val valueListStr = mapNazioni[nazione]!!.split(" ")
            val lat = valueListStr[0].toDouble()
            val lng = valueListStr[1].toDouble()
            val count = valueListStr[2].toInt()
            if (count > 0) {
                val startPoint = GeoPoint(lat, lng)
                mapController.setCenter(startPoint)
                addMarkerToMap(startPoint, nazione, count)
            }
        }
    }

    private fun goDialog(nazione: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Stai andando in $nazione")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                val bundle = Bundle().apply {
                    putString("nazione", nazione)
                }
                navController.navigate(R.id.action_map_viaggi_to_navigation_viaggi, bundle)
            }
            .setNegativeButton("Annulla") { dialog, _ ->
                dialog.dismiss()
            }

        val alert = dialogBuilder.create()
        alert.show()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}
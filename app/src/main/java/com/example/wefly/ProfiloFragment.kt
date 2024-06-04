package com.example.wefly

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.wefly.databinding.FragmentProfiloBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import java.io.IOException

class ProfiloFragment : Fragment() {

    private lateinit var binding: FragmentProfiloBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var utente: DataUtenti
    private lateinit var uid: String
    private lateinit var listaInteressi: ArrayList<String>
    private lateinit var sharedPreferences: SharedPreferences

    // Variable to decide whether to set the content or load the content
    private var sceltaBoolean: Boolean = false

    private val REQUEST_PERMISSIONS_CODE = 1001
    private val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize SharedPreferences
        sharedPreferences =
            requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using the binding variable
        binding = FragmentProfiloBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check and request permissions
        if (!hasPermissions(requireContext(), REQUIRED_PERMISSIONS)) {
            requestPermissions()
        }

        // Set the text for the toolbar
        val title = view.findViewById<TextView>(R.id.toolbar_title)
        title.text = "Profilo"

        if (!sharedPreferences.getBoolean("sceltaBoolean", false)) {
            // Set the content
            setContent()
        } else {
            // Load saved content
            loadSavedContent()
        }

        val viaggiAttualiBtn = view.findViewById<MaterialButton>(R.id.viaggi_attuali_btn)
        viaggiAttualiBtn.setOnClickListener {
            val intentViaggiAttuali = Intent(context, ViaggiAttualiActivity::class.java)
            startActivity(intentViaggiAttuali)
        }

        val viaggiPassatiBtn = view.findViewById<MaterialButton>(R.id.viaggi_passati_btn)
        viaggiPassatiBtn.setOnClickListener {
            val intentViaggiPassati = Intent(context, ViaggiPassatiActivity::class.java)
            startActivity(intentViaggiPassati)
        }
    }

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        requestPermissions(REQUIRED_PERMISSIONS, REQUEST_PERMISSIONS_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED })) {
                    // Permissions granted, proceed with your functionality
                    Log.d("Permissions", "All permissions granted")
                } else {
                    // Permissions denied, show a message to the user
                    Log.d("Permissions", "Permissions denied")
                    // Optionally, you can disable the functionality that depends on these permissions
                }
            }
        }
    }

    private fun setContent() {
        val name = arguments?.getString("nome")
        val surname = arguments?.getString("cognome")
        val imageUriString = arguments?.getString("imageUri")

        Log.d("NOME: ", "$name")
        Log.d("COGNOME: ", "$surname")
        Log.d("IMAGEURI: ", "$imageUriString")

        sceltaBoolean = true

        // Save variables to SharedPreferences
        with(sharedPreferences.edit()) {
            putString("name", name)
            putString("surname", surname)
            putString("imageUriString", imageUriString)
            putBoolean("sceltaBoolean", sceltaBoolean)
            apply()
        }

        binding.nomeUtente.setText(name)
        binding.cognomeUtente.setText(surname)

        val imageUri = imageUriString?.let { Uri.parse(it) }
        binding.profilePicture.setImageURI(imageUri)

        setInteressi()
    }

    private fun setInteressi() {
        listaInteressi = arrayListOf()

        var stringaInteressi = ""

        val list = booleanArrayOf(
            arguments?.getBoolean("scelta1") ?: false,
            arguments?.getBoolean("scelta2") ?: false,
            arguments?.getBoolean("scelta3") ?: false,
            arguments?.getBoolean("scelta4") ?: false,
            arguments?.getBoolean("scelta5") ?: false,
            arguments?.getBoolean("scelta6") ?: false,
            arguments?.getBoolean("scelta7") ?: false,
            arguments?.getBoolean("scelta8") ?: false,
            arguments?.getBoolean("scelta9") ?: false,
            arguments?.getBoolean("scelta10") ?: false
        )

        val editor = sharedPreferences.edit()
        for (i in list.indices) {
            if (list[i]) {
                // Get the resource ID dynamically
                val resId =
                    resources.getIdentifier("scelta${i + 1}", "string", context?.packageName)

                // Use getString() to get the actual string value
                if (resId != 0) {
                    val interesse = getString(resId)
                    stringaInteressi += interesse
                    listaInteressi.add(interesse)
                }
                if (i != list.lastIndex) {
                    stringaInteressi += ", "
                }

                // Save each interest as a boolean
                editor.putBoolean("scelta${i + 1}", list[i])
            }
        }
        editor.apply()

        binding.interessiUtente.setText(stringaInteressi)
    }

    private fun loadSavedContent() {
        val name = sharedPreferences.getString("name", "")
        val surname = sharedPreferences.getString("surname", "")
        val imageUriString = sharedPreferences.getString("imageUriString", "")

        binding.nomeUtente.setText(name)
        binding.cognomeUtente.setText(surname)

        val imageUri = imageUriString?.let { Uri.parse(it) }
        binding.profilePicture.setImageURI(imageUri)

        loadInteressi()
    }

    private fun loadInteressi() {
        listaInteressi = arrayListOf()

        var stringaInteressi = ""

        val list = booleanArrayOf(
            sharedPreferences.getBoolean("scelta1", false),
            sharedPreferences.getBoolean("scelta2", false),
            sharedPreferences.getBoolean("scelta3", false),
            sharedPreferences.getBoolean("scelta4", false),
            sharedPreferences.getBoolean("scelta5", false),
            sharedPreferences.getBoolean("scelta6", false),
            sharedPreferences.getBoolean("scelta7", false),
            sharedPreferences.getBoolean("scelta8", false),
            sharedPreferences.getBoolean("scelta9", false),
            sharedPreferences.getBoolean("scelta10", false)
        )

        for (i in list.indices) {
            if (list[i]) {
                // Get the resource ID dynamically
                val resId =
                    resources.getIdentifier("scelta${i + 1}", "string", context?.packageName)

                // Use getString() to get the actual string value
                if (resId != 0) {
                    val interesse = getString(resId)
                    stringaInteressi += interesse
                    listaInteressi.add(interesse)
                }
                if (i != list.lastIndex) {
                    stringaInteressi += ", "
                }
            }
        }

        binding.interessiUtente.setText(stringaInteressi)
    }

}
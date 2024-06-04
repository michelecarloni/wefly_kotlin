package com.example.wefly

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.wefly.databinding.FragmentCreaViaggio2Binding
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class CreaViaggio2Fragment : Fragment() {
    private lateinit var binding: FragmentCreaViaggio2Binding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null

    private var scelta1: Boolean = false
    private var scelta2: Boolean = false
    private var scelta3: Boolean = false
    private var scelta4: Boolean = false
    private var scelta5: Boolean = false
    private var scelta6: Boolean = false
    private var scelta7: Boolean = false
    private var scelta8: Boolean = false
    private var scelta9: Boolean = false
    private var scelta10: Boolean = false

    private var descrizione: String = ""
    private var partecipantiMax: Int = 1

    private lateinit var checkBox1: MaterialCheckBox
    private lateinit var checkBox2: MaterialCheckBox
    private lateinit var checkBox3: MaterialCheckBox
    private lateinit var checkBox4: MaterialCheckBox
    private lateinit var checkBox5: MaterialCheckBox
    private lateinit var checkBox6: MaterialCheckBox
    private lateinit var checkBox7: MaterialCheckBox
    private lateinit var checkBox8: MaterialCheckBox
    private lateinit var checkBox9: MaterialCheckBox
    private lateinit var checkBox10: MaterialCheckBox

    private lateinit var descrizioneBox: EditText
    private lateinit var partecipantiMaxBox: EditText

    // variables for data sent
    private lateinit var titoloViaggio: String
    private lateinit var budget: String
    private lateinit var nazione: String
    private lateinit var citta: String
    private lateinit var dataPartenza: String
    private lateinit var dataRitorno: String

    private lateinit var cambiaImmagineBtn: MaterialButton
    private lateinit var pictureTravelImageView: ImageView

    private val REQUEST_PERMISSIONS_CODE = 1001
    private val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA
    )

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            requireActivity().getSharedPreferences("CreaViaggio2Prefs", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreaViaggio2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check and request permissions
        if (!hasPermissions(requireContext(), REQUIRED_PERMISSIONS)) {
            requestPermissions()
        }

        cambiaImmagineBtn = view.findViewById(R.id.change_image_btn)
        pictureTravelImageView = view.findViewById(R.id.picture_travel)

        auth = Firebase.auth

        cambiaImmagineBtn.setOnClickListener {
            pickImageGallery()
        }

        titoloViaggio = arguments?.getString("titoloViaggio") ?: ""
        budget = arguments?.getString("budget") ?: ""
        nazione = arguments?.getString("nazione") ?: ""
        citta = arguments?.getString("citta") ?: ""
        dataPartenza = arguments?.getString("dataPartenza") ?: ""
        dataRitorno = arguments?.getString("dataRitorno") ?: ""

        Log.d("TITOLO VIAGGIO", "$titoloViaggio")
        Log.d("BUDGET", "$budget")
        Log.d("NAZIONE", "$nazione")
        Log.d("CITTA", "$citta")
        Log.d("DATA INIZIO", "$dataPartenza")
        Log.d("DATA FINE", "$dataRitorno")

        // Restore the data from SharedPreferences
        restoreState()

        // Set all the variables
        checkBox1 = view.findViewById(R.id.scelta1)
        checkBox1.isChecked = scelta1
        checkBox2 = view.findViewById(R.id.scelta2)
        checkBox2.isChecked = scelta2
        checkBox3 = view.findViewById(R.id.scelta3)
        checkBox3.isChecked = scelta3
        checkBox4 = view.findViewById(R.id.scelta4)
        checkBox4.isChecked = scelta4
        checkBox5 = view.findViewById(R.id.scelta5)
        checkBox5.isChecked = scelta5
        checkBox6 = view.findViewById(R.id.scelta6)
        checkBox6.isChecked = scelta6
        checkBox7 = view.findViewById(R.id.scelta7)
        checkBox7.isChecked = scelta7
        checkBox8 = view.findViewById(R.id.scelta8)
        checkBox8.isChecked = scelta8
        checkBox9 = view.findViewById(R.id.scelta9)
        checkBox9.isChecked = scelta9
        checkBox10 = view.findViewById(R.id.scelta10)
        checkBox10.isChecked = scelta10
        descrizioneBox = view.findViewById(R.id.textArea)
        descrizioneBox.setText(descrizione)
        partecipantiMaxBox = view.findViewById(R.id.partecipantiMax)
        partecipantiMaxBox.setText(partecipantiMax.toString())

        // Set the text for the toolbar
        val title = view.findViewById<TextView>(R.id.toolbar_title)
        title.text = "Crea Viaggio"

        val navController = findNavController()

        val indietroBtn = view.findViewById<MaterialButton>(R.id.indietro_btn)
        indietroBtn.setOnClickListener {
            // Save the state before navigating up
            saveState()
            navController.navigateUp()
        }

        val confermaBtn = view.findViewById<MaterialButton>(R.id.conferma_btn)
        confermaBtn.setOnClickListener {
            val uid = auth.currentUser?.uid
            Toast.makeText(
                this@CreaViaggio2Fragment.requireContext(),
                "Attendi...",
                Toast.LENGTH_SHORT
            ).show()
            saveData(
                uid,
                titoloViaggio,
                budget,
                nazione,
                citta,
                dataPartenza,
                dataRitorno,
                descrizioneBox.text.toString(),
                partecipantiMaxBox.text.toString().toInt(),
                checkBox1.isChecked,
                checkBox2.isChecked,
                checkBox3.isChecked,
                checkBox4.isChecked,
                checkBox5.isChecked,
                checkBox6.isChecked,
                checkBox7.isChecked,
                checkBox8.isChecked,
                checkBox9.isChecked,
                checkBox10.isChecked,
            )
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, CompletaProfiloActivity.IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CompletaProfiloActivity.IMAGE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            imageUri = data?.data
            pictureTravelImageView.setImageURI(imageUri)
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

    private fun saveData(
        uid: String?,
        titoloViaggio: String?,
        budget: String?,
        nazione: String?,
        citta: String?,
        dataPartenza: String?,
        dataRitorno: String?,
        descrizione: String?,
        partecipantiMax: Int?,
        scelta1: Boolean?,
        scelta2: Boolean?,
        scelta3: Boolean?,
        scelta4: Boolean?,
        scelta5: Boolean?,
        scelta6: Boolean?,
        scelta7: Boolean?,
        scelta8: Boolean?,
        scelta9: Boolean?,
        scelta10: Boolean?
    ) {
        databaseReference =
            FirebaseDatabase.getInstance("https://wefly-d50f7-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("ViaggiTotali")

        // Generate a unique key for the new travel entry
        val uniqueKey = databaseReference.push().key

        val viaggio = DataElencoViaggi(
            titoloViaggio ?: "",
            budget ?: "",
            nazione ?: "",
            citta ?: "",
            dataPartenza ?: "",
            dataRitorno ?: "",
            1,
            partecipantiMax ?: 1,
            descrizione ?: "",
            scelta1 ?: false,
            scelta2 ?: false,
            scelta3 ?: false,
            scelta4 ?: false,
            scelta5 ?: false,
            scelta6 ?: false,
            scelta7 ?: false,
            scelta8 ?: false,
            scelta9 ?: false,
            scelta10 ?: false
        )

        if (uid != null && uniqueKey != null) {
            databaseReference.child(uniqueKey).setValue(viaggio).addOnSuccessListener {
                uploadProfileImage(uid, uniqueKey)
            }.addOnFailureListener {
                Toast.makeText(
                    this@CreaViaggio2Fragment.requireContext(),
                    "Errore",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this@CreaViaggio2Fragment.requireContext(), "Errore", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun uploadProfileImage(uid: String, uniqueKey: String) {
        if (imageUri != null) {
            storageReference = FirebaseStorage.getInstance()
                .getReference("ViaggiTotali/$uniqueKey.jpg")
            storageReference.putFile(imageUri!!).addOnSuccessListener {
                Toast.makeText(
                    this@CreaViaggio2Fragment.requireContext(),
                    "Profile picture uploaded",
                    Toast.LENGTH_SHORT
                ).show()
                // Show success dialog
                goDialog()
            }.addOnFailureListener {
                Toast.makeText(
                    this@CreaViaggio2Fragment.requireContext(),
                    "Failed to upload profile picture",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // Handle default image case
            pictureTravelImageView.isDrawingCacheEnabled = true
            pictureTravelImageView.buildDrawingCache()
            val bitmap = (pictureTravelImageView.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            storageReference = FirebaseStorage.getInstance()
                .getReference("ViaggiTotali/$uniqueKey.jpg")
            val uploadTask = storageReference.putBytes(data)
            uploadTask.addOnSuccessListener {
                Toast.makeText(
                    this@CreaViaggio2Fragment.requireContext(),
                    "Default profile picture uploaded",
                    Toast.LENGTH_SHORT
                ).show()
                // Show success dialog
                val notificationHelper = NotificationHelper(requireContext())
                if (titoloViaggio != null) {
                    notificationHelper.NotificationCreateTravel(titoloViaggio)
                }
                goDialog()
            }.addOnFailureListener {
                Toast.makeText(
                    this@CreaViaggio2Fragment.requireContext(),
                    "Failed to upload default profile picture",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



    private fun goDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Viaggio Creato con successo")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        val alert = dialogBuilder.create()
        alert.show()
    }

    private fun saveState() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("scelta1", checkBox1.isChecked)
        editor.putBoolean("scelta2", checkBox2.isChecked)
        editor.putBoolean("scelta3", checkBox3.isChecked)
        editor.putBoolean("scelta4", checkBox4.isChecked)
        editor.putBoolean("scelta5", checkBox5.isChecked)
        editor.putBoolean("scelta6", checkBox6.isChecked)
        editor.putBoolean("scelta7", checkBox7.isChecked)
        editor.putBoolean("scelta8", checkBox8.isChecked)
        editor.putBoolean("scelta9", checkBox9.isChecked)
        editor.putBoolean("scelta10", checkBox10.isChecked)
        editor.putString("descrizione", descrizioneBox.text.toString())
        editor.putInt("partecipantiMax", partecipantiMaxBox.text.toString().toInt())
        editor.apply()
    }

    private fun restoreState() {
        scelta1 = sharedPreferences.getBoolean("scelta1", false)
        scelta2 = sharedPreferences.getBoolean("scelta2", false)
        scelta3 = sharedPreferences.getBoolean("scelta3", false)
        scelta4 = sharedPreferences.getBoolean("scelta4", false)
        scelta5 = sharedPreferences.getBoolean("scelta5", false)
        scelta6 = sharedPreferences.getBoolean("scelta6", false)
        scelta7 = sharedPreferences.getBoolean("scelta7", false)
        scelta8 = sharedPreferences.getBoolean("scelta8", false)
        scelta9 = sharedPreferences.getBoolean("scelta9", false)
        scelta10 = sharedPreferences.getBoolean("scelta10", false)
        descrizione = sharedPreferences.getString("descrizione", "") ?: ""
        partecipantiMax = sharedPreferences.getInt("partecipantiMax", 1)
    }
}
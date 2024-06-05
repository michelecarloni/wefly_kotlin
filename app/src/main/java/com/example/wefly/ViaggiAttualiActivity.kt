package com.example.wefly

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wefly.databinding.ActivitySignInBinding
import com.example.wefly.databinding.ActivityViaggiAttualiBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.OutputStream

class ViaggiAttualiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViaggiAttualiBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference

    // dichiarazione variables per Viaggi attuali

    private lateinit var adapterAttuali: AdapterElencoViaggi // dichiarazione dell'adapter
    private lateinit var recyclerViewAttuali: RecyclerView // dichiarazione della recyclerView
    private lateinit var viaggiAttualiArrayList: ArrayList<DataElencoViaggi> // ArrayList di objects

    private lateinit var viaggio: DataElencoViaggi

    private lateinit var tid : String
    private var imageUri: Uri? = null

    // lateinit var imageIdAttuali : Array<Int>
    // lateinit var affinitaAttuali : Array<String>
    //lateinit var tipoViaggioAttuali : Array<String>

    /*lateinit var titoloViaggioAttuali : Array<String>
    lateinit var cittaAttuali : Array<String>
    lateinit var nazioneAttuali : Array<String>
    lateinit var dataPartenzaAttuali : Array<String>
    lateinit var dataRitornoAttuali : Array<String>
    lateinit var budgetAttuali : Array<String>
    lateinit var partecipantiAttuali : Array<Int>
    lateinit var partecipantiMaxAttuali : Array<Int>
    lateinit var descrizioneAttuali : Array<String>
    lateinit var viaggiAttuali : Array<String>
    lateinit var scelta1Attuali : Array<Boolean>
    lateinit var scelta2Attuali : Array<Boolean>
    lateinit var scelta3Attuali : Array<Boolean>
    lateinit var scelta4Attuali : Array<Boolean>
    lateinit var scelta5Attuali : Array<Boolean>
    lateinit var scelta6Attuali : Array<Boolean>
    lateinit var scelta7Attuali : Array<Boolean>
    lateinit var scelta8Attuali : Array<Boolean>
    lateinit var scelta9Attuali : Array<Boolean>
    lateinit var scelta10Attuali : Array<Boolean>*/

    lateinit var titoloViaggioAttuali: String
    lateinit var cittaAttuali: String
    lateinit var nazioneAttuali: String
    lateinit var dataPartenzaAttuali: String
    lateinit var dataRitornoAttuali: String
    lateinit var budgetAttuali: String
    var partecipantiAttuali: Int? = 0
    var partecipantiMaxAttuali: Int? = 0
    lateinit var descrizioneAttuali: String
    lateinit var viaggiAttuali: String
    var scelta1Attuali: Boolean? = false
    var scelta2Attuali: Boolean? = false
    var scelta3Attuali: Boolean? = false
    var scelta4Attuali: Boolean? = false
    var scelta5Attuali: Boolean? = false
    var scelta6Attuali: Boolean? = false
    var scelta7Attuali: Boolean? = false
    var scelta8Attuali: Boolean? = false
    var scelta9Attuali: Boolean? = false
    var scelta10Attuali: Boolean? = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()

        binding = ActivityViaggiAttualiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //set the text for the tool bar

        val title = findViewById<TextView>(R.id.toolbar_title)
        title.text = "Viaggi Attuali"

        auth = Firebase.auth
        databaseReference = FirebaseDatabase.getInstance("https://wefly-d50f7-default-rtdb.europe-west1.firebasedatabase.app").getReference("ViaggiTotali")
        setAllTravel()


        // dataInitializeAttuali()
        val layoutManagerAttuali = LinearLayoutManager(this)
        recyclerViewAttuali = findViewById(R.id.recyclerViewViaggiAttuali)
        recyclerViewAttuali.layoutManager = layoutManagerAttuali
        recyclerViewAttuali.setHasFixedSize(true)
        adapterAttuali = AdapterElencoViaggi(viaggiAttualiArrayList)
        recyclerViewAttuali.adapter = adapterAttuali

        adapterAttuali.onItemClick = {
            val intentAttuali = Intent(this, DettagliViaggiAttualiActivity::class.java)
            intentAttuali.putExtra("attuali", it)
            startActivity(intentAttuali)
        }

    }

    private fun setAllTravel() {
        // Initialize the array list
        viaggiAttualiArrayList = ArrayList()

        val repositoryRef = databaseReference

        repositoryRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (viaggiSnapshot in snapshot.children) {

                        tid = viaggiSnapshot.key.toString()
                        Log.d("TID", "tid: $tid")

                        // Read all the attributes
                        val titoloViaggioAttuali = viaggiSnapshot.child("titoloViaggio").value.toString()
                        val cittaAttuali = viaggiSnapshot.child("citta").value.toString()
                        val nazioneAttuali = viaggiSnapshot.child("nazione").value.toString()
                        val dataPartenzaAttuali = viaggiSnapshot.child("dataPartenza").value.toString()
                        val dataRitornoAttuali = viaggiSnapshot.child("dataRitorno").value.toString()
                        val budgetAttuali = viaggiSnapshot.child("budget").value.toString()
                        val partecipantiAttuali = viaggiSnapshot.child("partecipanti").value.toString().toInt()
                        val partecipantiMaxAttuali = viaggiSnapshot.child("partecipantiMax").value.toString().toInt()
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

                        val viaggiAttuali = DataElencoViaggi(
                            titoloViaggioAttuali, budgetAttuali, nazioneAttuali, cittaAttuali,
                            dataPartenzaAttuali, dataRitornoAttuali, partecipantiAttuali, partecipantiMaxAttuali,
                            descrizioneAttuali, scelta1Attuali, scelta2Attuali, scelta3Attuali, scelta4Attuali,
                            scelta5Attuali, scelta6Attuali, scelta7Attuali, scelta8Attuali, scelta9Attuali, scelta10Attuali
                        )

                        getPicturetravel()

                        viaggiAttualiArrayList.add(viaggiAttuali)
                    }

                    // Notify adapter about data changes
                    adapterAttuali.notifyDataSetChanged()
                    Log.d("FIREBASE", "Data loaded successfully")
                } else {
                    Log.d("FIREBASE", "No data found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViaggiAttualiActivity, "Failed to get user data.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getPicturetravel() {
        storageReference = FirebaseStorage.getInstance("gs://wefly-d50f7.appspot.com").reference.child("ViaggiTotali/$tid.jpg")
        val localFile = File.createTempFile("tempImage", "jpg")
        storageReference.getFile(localFile).addOnSuccessListener { taskSnapshot ->
            // Image successfully downloaded, decode it
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)

            // Check the orientation of the image and rotate if necessary
            val rotatedBitmap = rotateBitmapIfNeeded(bitmap, localFile.absolutePath)

            // Insert the rotated bitmap into the MediaStore and get the content URI
            val contentUri = insertImageToMediaStore(rotatedBitmap)

            imageUri = contentUri


        }.addOnFailureListener {
            Toast.makeText(this, "Error downloading image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun rotateBitmapIfNeeded(bitmap: Bitmap, imagePath: String): Bitmap {
        val exif = ExifInterface(imagePath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val rotationAngle = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
        return if (rotationAngle != 0) {
            val matrix = Matrix().apply { postRotate(rotationAngle.toFloat()) }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }
    }

    private fun insertImageToMediaStore(bitmap: Bitmap): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "profile_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/WeFly")
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        if (uri != null) {
            try {
                val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
                outputStream.use {
                    if (it != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        return uri
    }


}






    /*private fun dataInitializeAttuali(){

        viaggiAttualiArrayList = arrayListOf<DataElencoViaggi>()

        titoloViaggioAttuali = arrayOf (
            "Londra",
            "Londra",
            "Londra"
        )

        cittaAttuali = arrayOf(
            "Londra",
            "Maldive",
            "Parigi"
        )

        nazioneAttuali = arrayOf(
            "Regno Unito",
            "Asia",
            "Francia"
        )

        dataPartenzaAttuali = arrayOf(
            "20/12/2022",
            "20/12/2022",
            "20/12/2022"
        )

        dataRitornoAttuali = arrayOf(
            "30/03/2022",
            "30/03/2022",
            "30/03/2022"
        )

        budgetAttuali = arrayOf(
            "€1000",
            "€2000",
            "€3000"
        )

        partecipantiAttuali = arrayOf(
            3,
            3,
            3
        )

        partecipantiMaxAttuali = arrayOf(
            7,
            7,
            7
        )

        descrizioneAttuali = arrayOf(
            "Descrizione 1",
            "Descrizione 2",
            "Descrizione 3"
        )

        viaggiAttuali = arrayOf(
            "Viaggio 1",
            "Viaggio 2",
            "Viaggio 3"
        )

        scelta1Attuali = arrayOf(
            true,
            true,
            true
        )

        scelta2Attuali = arrayOf(
            true,
            true,
            true
        )

        scelta3Attuali = arrayOf(
            true,
            true,
            true
        )

        scelta4Attuali = arrayOf(
            false,
            false,
            false
        )

        scelta5Attuali = arrayOf(
            false,
            false,
            false
        )

        scelta6Attuali = arrayOf(
            false,
            false,
            false
        )

        scelta7Attuali = arrayOf(
            false,
            false,
            false
        )

        scelta8Attuali = arrayOf(
            false,
            false,
            false
        )

        scelta9Attuali = arrayOf(
            false,
            false,
            false
        )

        scelta10Attuali = arrayOf(
            false,
            false,
            false
        )

        for (i in titoloViaggioAttuali.indices){

            val viaggiAttuali = DataElencoViaggi(titoloViaggioAttuali[i], budgetAttuali[i], nazioneAttuali[i], cittaAttuali[i], dataPartenzaAttuali[i], dataRitornoAttuali[i], partecipantiAttuali[i], partecipantiMaxAttuali[i], descrizioneAttuali[i], scelta1Attuali[i], scelta2Attuali[i], scelta3Attuali[i], scelta4Attuali[i], scelta5Attuali[i], scelta6Attuali[i], scelta7Attuali[i], scelta8Attuali[i], scelta9Attuali[i], scelta10Attuali[i])
            viaggiAttualiArrayList.add(viaggiAttuali)
        }

    }*/
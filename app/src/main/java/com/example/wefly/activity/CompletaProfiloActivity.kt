package com.example.wefly.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.wefly.utils.NotificationHelper
import com.example.wefly.R
import com.example.wefly.databinding.ActivityCompletaProfiloBinding
import com.example.wefly.firebase.DataFirebase
import com.example.wefly.model.ModelUtenti
import com.example.wefly.utils.ProgressBar
import com.example.wefly.viewmodel.UtentiViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference

class CompletaProfiloActivity : AppCompatActivity() {

    // firebase
    private lateinit var firebaseObject: DataFirebase

    // ProgressBar
    private lateinit var progressBar: ProgressBar

    // ViewModel
    private lateinit var UtentiViewModel : UtentiViewModel

    private lateinit var binding: ActivityCompletaProfiloBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReferences: DatabaseReference
    private lateinit var storageReferences: StorageReference
    private var imageUri: Uri? = null
    private lateinit var dialog : Dialog
    //private var imageUrl = ""

    // variables for getting data from SignUp
    private lateinit var uid: String
    private lateinit var nome: String
    private lateinit var cognome: String
    private lateinit var telefono: String
    private lateinit var email: String
    private lateinit var password: String
    private var viaggiStr = ""

    // variables for the checkbox
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

    // private var checkGoogle = false

    // for the image picker
    private lateinit var cambiaImmagineBtn: MaterialButton
    private lateinit var profileImage: ImageView

    companion object {
        const val IMAGE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCompletaProfiloBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = ProgressBar(this)

        UtentiViewModel = ViewModelProvider(this).get(com.example.wefly.viewmodel.UtentiViewModel::class.java)

        firebaseObject = DataFirebase()

        //take the data from the checkboxes
        checkBox1 = findViewById(R.id.scelta1)
        checkBox2 = findViewById(R.id.scelta2)
        checkBox3 = findViewById(R.id.scelta3)
        checkBox4 = findViewById(R.id.scelta4)
        checkBox5 = findViewById(R.id.scelta5)
        checkBox6 = findViewById(R.id.scelta6)
        checkBox7 = findViewById(R.id.scelta7)
        checkBox8 = findViewById(R.id.scelta8)
        checkBox9 = findViewById(R.id.scelta9)
        checkBox10 = findViewById(R.id.scelta10)

        // Take all the data sent
        uid = intent.getStringExtra("uid") ?: ""
        nome = intent.getStringExtra("nome") ?: ""
        cognome = intent.getStringExtra("cognome") ?: ""
        telefono = intent.getStringExtra("telefono") ?: ""
        email = intent.getStringExtra("email") ?: ""
        password = intent.getStringExtra("password") ?: ""
        //imageUrl = intent.getStringExtra("imageUrl") ?: ""

        cambiaImmagineBtn = findViewById(R.id.change_image_btn)
        profileImage = findViewById(R.id.profile_picture)

        auth = Firebase.auth

        cambiaImmagineBtn.setOnClickListener {
            // checkGoogle = false
            pickImageGallery()
        }

        binding.registratiBtn.setOnClickListener {
            progressBar.showProgressBar()
            val uid = auth.currentUser?.uid
            Toast.makeText(this, "Attendi...", Toast.LENGTH_SHORT).show()
            saveUserData(uid, nome, cognome, telefono, email, password, viaggiStr, checkBox1.isChecked, checkBox2.isChecked, checkBox3.isChecked, checkBox4.isChecked, checkBox5.isChecked, checkBox6.isChecked, checkBox7.isChecked, checkBox8.isChecked, checkBox9.isChecked, checkBox10.isChecked)
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            imageUri = data?.data
            profileImage.setImageURI(imageUri)
        }
    }

    private fun saveUserData(uid: String?, nome: String?, cognome: String?, telefono: String?, email: String?, password: String?, viaggiStr: String?, scelta1: Boolean?, scelta2: Boolean?, scelta3: Boolean?, scelta4: Boolean?, scelta5: Boolean?, scelta6: Boolean?, scelta7: Boolean?, scelta8: Boolean?, scelta9: Boolean?, scelta10: Boolean?) {
        databaseReferences = firebaseObject.getDatabaseUtenti()
        val utente = ModelUtenti(nome, cognome, telefono, email, password, viaggiStr, scelta1, scelta2, scelta3, scelta4, scelta5, scelta6, scelta7, scelta8, scelta9, scelta10)
        if (uid != null) {
            databaseReferences.child(uid).setValue(utente).addOnSuccessListener {
                uploadProfileImage(uid)
                saveUserDataLocally(uid, nome, cognome, telefono, email, password, viaggiStr, scelta1, scelta2, scelta3, scelta4, scelta5, scelta6, scelta7, scelta8, scelta9, scelta10)
                val notificationHelper = NotificationHelper(this)
                if (nome != null) {
                    notificationHelper.NotificationCompleteRegistration(nome)
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Errore", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Errore", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadProfileImage(uid: String) {
        // se imageUri == null allora o l'immagine è stata caricata da google oppure l'utente non ha selezionato l'immagine
        if (imageUri == null) {
                // If not, set the imageUri to the default image
                imageUri = Uri.parse("android.resource://" + packageName + "/" + R.drawable.profile_picture)
        }

        Log.d( "imageUriCompleta", imageUri.toString())

        // Proceed with uploading the image (whether it's the user's chosen image or the default one)
        storageReferences = firebaseObject.getStorageUtenti().child("${auth.currentUser?.uid}.jpg")
        Log.d("UidUser", auth.currentUser?.uid.toString())
        storageReferences.putFile(imageUri!!).addOnSuccessListener {
            Toast.makeText(this, "Profile picture uploaded", Toast.LENGTH_SHORT).show()
            // Start the next activity after image upload is successful
            progressBar.hideProgressBar()
            startActivity(Intent(this, MainActivity::class.java))
        }.addOnFailureListener {
            progressBar.hideProgressBar()
            Toast.makeText(this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserDataLocally(uid: String, nome: String?, cognome: String?, telefono: String?, email: String?, password: String?, viaggiStr: String?, scelta1: Boolean?, scelta2: Boolean?, scelta3: Boolean?, scelta4: Boolean?, scelta5: Boolean?, scelta6: Boolean?, scelta7: Boolean?, scelta8: Boolean?, scelta9: Boolean?, scelta10: Boolean?) {
        val sharedPreferences = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("uid", uid)
        editor.putString("nome", nome)
        editor.putString("cognome", cognome)
        editor.putString("telefono", telefono)
        editor.putString("email", email)
        editor.putString("password", password)
        editor.putString("viaggiStr", viaggiStr)
        editor.putBoolean("scelta1", scelta1 ?: false)
        editor.putBoolean("scelta2", scelta2 ?: false)
        editor.putBoolean("scelta3", scelta3 ?: false)
        editor.putBoolean("scelta4", scelta4 ?: false)
        editor.putBoolean("scelta5", scelta5 ?: false)
        editor.putBoolean("scelta6", scelta6 ?: false)
        editor.putBoolean("scelta7", scelta7 ?: false)
        editor.putBoolean("scelta8", scelta8 ?: false)
        editor.putBoolean("scelta9", scelta9 ?: false)
        editor.putBoolean("scelta10", scelta10 ?: false)

        if (imageUri != null) {
            editor.putString("imageUri", imageUri.toString())
        }

        editor.apply()
    }


}
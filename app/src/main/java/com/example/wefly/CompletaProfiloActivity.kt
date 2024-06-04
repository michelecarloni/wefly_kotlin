package com.example.wefly

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wefly.databinding.ActivityCompletaProfiloBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CompletaProfiloActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompletaProfiloBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null

    // variables for getting data from SignUp
    private lateinit var uid: String
    private lateinit var nome: String
    private lateinit var cognome: String
    private lateinit var telefono: String
    private lateinit var email: String
    private lateinit var password: String

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

        cambiaImmagineBtn = findViewById(R.id.change_image_btn)
        profileImage = findViewById(R.id.profile_picture)

        auth = Firebase.auth

        cambiaImmagineBtn.setOnClickListener {
            pickImageGallery()
        }

        binding.registratiBtn.setOnClickListener {

            val uid = auth.currentUser?.uid
            Toast.makeText(this, "Attendi...", Toast.LENGTH_SHORT).show()
            saveUserData(uid, nome, cognome, telefono, email, password, checkBox1.isChecked, checkBox2.isChecked, checkBox3.isChecked, checkBox4.isChecked, checkBox5.isChecked, checkBox6.isChecked, checkBox7.isChecked, checkBox8.isChecked, checkBox9.isChecked, checkBox10.isChecked)

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

    private fun saveUserData(uid: String?, nome: String?, cognome: String?, telefono: String?, email: String?, password: String?, scelta1: Boolean?, scelta2: Boolean?, scelta3: Boolean?, scelta4: Boolean?, scelta5: Boolean?, scelta6: Boolean?, scelta7: Boolean?, scelta8: Boolean?, scelta9: Boolean?, scelta10: Boolean?) {
        databaseReference = FirebaseDatabase.getInstance("https://wefly-d50f7-default-rtdb.europe-west1.firebasedatabase.app").getReference("Utenti")
        val utente = DataUtenti(nome, cognome, telefono, email, password, scelta1, scelta2, scelta3, scelta4, scelta5, scelta6, scelta7, scelta8, scelta9, scelta10)
        if (uid != null) {
            databaseReference.child(uid).setValue(utente).addOnSuccessListener {
                uploadProfileImage(uid)
                val notificationHelper = NotificationHelper(this)
                if (nome != null) {
                    notificationHelper.NotificationCompleteRegistration(nome)
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Errore", Toast.LENGTH_SHORT).show()
            }
        } else{
            Toast.makeText(this, "Errore", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadProfileImage(uid: String) {
        if (imageUri != null) {
            storageReference = FirebaseStorage.getInstance().getReference("Utenti/"+auth.currentUser?.uid+".jpg")
            storageReference.putFile(imageUri!!).addOnSuccessListener {
                Toast.makeText(this, "Profile picture uploaded", Toast.LENGTH_SHORT).show()
                // Start the next activity after image upload is successful
                changeActivity()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun changeActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("uid", uid)
        intent.putExtra("nome", nome)
        intent.putExtra("cognome", cognome)
        intent.putExtra("telefono", telefono)
        intent.putExtra("email", email)
        intent.putExtra("password", password)
        intent.putExtra("scelta1", checkBox1.isChecked)
        intent.putExtra("scelta2", checkBox2.isChecked)
        intent.putExtra("scelta3", checkBox3.isChecked)
        intent.putExtra("scelta4", checkBox4.isChecked)
        intent.putExtra("scelta5", checkBox5.isChecked)
        intent.putExtra("scelta6", checkBox6.isChecked)
        intent.putExtra("scelta7", checkBox7.isChecked)
        intent.putExtra("scelta8", checkBox8.isChecked)
        intent.putExtra("scelta9", checkBox9.isChecked)
        intent.putExtra("scelta10", checkBox10.isChecked)

        Log.d("COMPLETA PROFILO | UID", intent.getStringExtra("uid") ?: "null")
        Log.d("COMPLETA PROFILO | NOME", intent.getStringExtra("nome") ?: "null")
        Log.d("COMPLETA PROFILO | COGNOME", intent.getStringExtra("cognome") ?: "null")

        if (imageUri != null) {
            intent.putExtra("imageUri", imageUri.toString())
        }

        startActivity(intent)
    }
}
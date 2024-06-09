package com.example.wefly.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wefly.R
import com.example.wefly.databinding.ActivitySignInBinding
import com.example.wefly.firebase.DataFirebase
import com.example.wefly.model.ModelUtenti
import com.example.wefly.utils.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class SignInActivity : AppCompatActivity() {

    // firebase
    private lateinit var firebaseObject: DataFirebase

    // progressBar
    private lateinit var progressBar: ProgressBar

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var dialog : Dialog

    private lateinit var utente: ModelUtenti

    // variables to store the data
    private lateinit var uid: String
    private lateinit var nome: String
    private lateinit var cognome: String
    private lateinit var telefono: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var viaggiStr : String
    private var imageUri: Uri? = null

    private var scelta1 = false
    private var scelta2 = false
    private var scelta3 = false
    private var scelta4 = false
    private var scelta5 = false
    private var scelta6 = false
    private var scelta7 = false
    private var scelta8 = false
    private var scelta9 = false
    private var scelta10 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = ProgressBar(this)

        firebaseObject = DataFirebase()

        auth = Firebase.auth

        binding.loginBtn.setOnClickListener {
            progressBar.showProgressBar()
            auth.signInWithEmailAndPassword(binding.emailAddress.text.toString(), binding.password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser

                        auth = FirebaseAuth.getInstance()
                        uid = auth.currentUser?.uid.toString()
                        Toast.makeText(this, "uid: $uid", Toast.LENGTH_SHORT).show()
                        databaseReference = firebaseObject.getDatabaseUtenti()

                        if (uid.isNotEmpty()) {
                            getUserData()
                        } else {
                            Toast.makeText(this, "Errore", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        progressBar.hideProgressBar()
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }

    private fun getUserData() {
        databaseReference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                Log.d("ENTERED", "OOO")

                utente = snapshot.getValue(ModelUtenti::class.java)!!
                nome = utente.nome!!
                cognome = utente.cognome!!
                telefono = utente.telefono!!
                email = utente.email!!
                password = utente.password!!
                viaggiStr = utente.viaggiStr!!
                scelta1 = utente.scelta1!!
                scelta2 = utente.scelta2!!
                scelta3 = utente.scelta3!!
                scelta4 = utente.scelta4!!
                scelta5 = utente.scelta5!!
                scelta6 = utente.scelta6!!
                scelta7 = utente.scelta7!!
                scelta8 = utente.scelta8!!
                scelta9 = utente.scelta9!!
                scelta10 = utente.scelta10!!

                Log.d("UID:", uid)
                Log.d("Nome:", nome)
                Log.d("Cognome:", cognome)
                Log.d("Telefono:", telefono)
                Log.d("Email:", email)
                Log.d("Password:", password)
                Log.d("Viaggi:", viaggiStr)
                Log.d("Scelta 1:", scelta1.toString())
                Log.d("Scelta 2:", scelta2.toString())
                Log.d("Scelta 3:", scelta3.toString())
                Log.d("Scelta 4:", scelta4.toString())
                Log.d("Scelta 5:", scelta5.toString())
                Log.d("Scelta 6:", scelta6.toString())
                Log.d("Scelta 7:", scelta7.toString())
                Log.d("Scelta 8:", scelta8.toString())
                Log.d("Scelta 9:", scelta9.toString())
                Log.d("Scelta 10:", scelta10.toString())

                getUserProfile()
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.hideProgressBar()
                Toast.makeText(this@SignInActivity, "Failed to get user data.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUserProfile() {
        storageReference = firebaseObject.getStorageUtenti().child("$uid.jpg")
        val localFile = File.createTempFile("tempImage", "jpg")
        storageReference.getFile(localFile).addOnSuccessListener {

            // Image successfully downloaded, decode it
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)

            // Convert bitmap to URI
            val contentUri = Uri.fromFile(localFile)
            imageUri = contentUri

            saveUserDataLocally(uid, nome, cognome, telefono, email, password, viaggiStr, scelta1, scelta2, scelta3, scelta4, scelta5, scelta6, scelta7, scelta8, scelta9, scelta10)

        }.addOnFailureListener {
            progressBar.hideProgressBar()
            Toast.makeText(this, "Error downloading image", Toast.LENGTH_SHORT).show()
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
        progressBar.hideProgressBar()
        startActivity(Intent(this, MainActivity::class.java))
    }

}
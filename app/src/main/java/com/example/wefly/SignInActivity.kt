package com.example.wefly

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wefly.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.OutputStream

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference

    private lateinit var utente: DataUtenti

    // variables to store the data
    private lateinit var uid: String
    private lateinit var nome: String
    private lateinit var cognome: String
    private lateinit var telefono: String
    private lateinit var email: String
    private lateinit var password: String
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

        auth = Firebase.auth

        binding.loginBtn.setOnClickListener {
            auth.signInWithEmailAndPassword(binding.emailAddress.text.toString(), binding.password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser

                        auth = FirebaseAuth.getInstance()
                        uid = auth.currentUser?.uid.toString()
                        Toast.makeText(this, "uid: $uid", Toast.LENGTH_SHORT).show()
                        databaseReference = FirebaseDatabase.getInstance("https://wefly-d50f7-default-rtdb.europe-west1.firebasedatabase.app")
                            .getReference("Utenti")

                        if (uid.isNotEmpty()) {
                            getUserData()
                        } else {
                            Toast.makeText(this, "Errore", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        // If sign in fails, display a message to the user.
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
                utente = snapshot.getValue(DataUtenti::class.java)!!
                nome = utente.nome!!
                cognome = utente.cognome!!
                telefono = utente.telefono!!
                email = utente.email!!
                password = utente.password!!
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

                getUserProfile()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SignInActivity, "Failed to get user data.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUserProfile() {
        storageReference = FirebaseStorage.getInstance("gs://wefly-d50f7.appspot.com").reference.child("Utenti/$uid.jpg")
        val localFile = File.createTempFile("tempImage", "jpg")
        storageReference.getFile(localFile).addOnSuccessListener { taskSnapshot ->
            // Image successfully downloaded, decode it
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)

            // Check the orientation of the image and rotate if necessary
            val rotatedBitmap = rotateBitmapIfNeeded(bitmap, localFile.absolutePath)

            // Insert the rotated bitmap into the MediaStore and get the content URI
            val contentUri = insertImageToMediaStore(rotatedBitmap)

            imageUri = contentUri

            sendData() // Call sendData only after the URI is fetched
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

    private fun sendData() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("uid", uid)
        intent.putExtra("nome", nome)
        intent.putExtra("cognome", cognome)
        intent.putExtra("telefono", telefono)
        intent.putExtra("email", email)
        intent.putExtra("password", password)
        intent.putExtra("scelta1", scelta1)
        intent.putExtra("scelta2", scelta2)
        intent.putExtra("scelta3", scelta3)
        intent.putExtra("scelta4", scelta4)
        intent.putExtra("scelta5", scelta5)
        intent.putExtra("scelta6", scelta6)
        intent.putExtra("scelta7", scelta7)
        intent.putExtra("scelta8", scelta8)
        intent.putExtra("scelta9", scelta9)
        intent.putExtra("scelta10", scelta10)

        if (imageUri != null) {
            intent.putExtra("imageUri", imageUri.toString())
        }

        startActivity(intent)
    }
}

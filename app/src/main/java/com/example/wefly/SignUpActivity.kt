package com.example.wefly

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wefly.databinding.ActivitySignInBinding
import com.example.wefly.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.continuaBtn.setOnClickListener{
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            val passwordConfirm = binding.editConfermaPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa i campi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != passwordConfirm) {
                Toast.makeText(this, "Le password non coincidono", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val uid = auth.currentUser?.uid
                        val intent = Intent(this, CompletaProfiloActivity::class.java)
                        intent.putExtra("uid", uid)
                        intent.putExtra("nome", binding.editTextNome.text.toString())
                        intent.putExtra("cognome", binding.editTextCognome.text.toString())
                        intent.putExtra("telefono", binding.editTextTelefono.text.toString())
                        intent.putExtra("email", email)
                        intent.putExtra("password", password)

                        startActivity(intent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        // If sign in fails, display a message to the user.
                        val errorCode = (task.exception as FirebaseAuthException).errorCode
                        when (errorCode) {
                            "ERROR_INVALID_EMAIL" -> Toast.makeText(
                                this,
                                "The email address is badly formatted.",
                                Toast.LENGTH_LONG
                            ).show()

                            "ERROR_EMAIL_ALREADY_IN_USE" -> Toast.makeText(
                                this,
                                "The email address is already in use by another account.",
                                Toast.LENGTH_LONG
                            ).show()

                            "ERROR_WEAK_PASSWORD" -> Toast.makeText(
                                this,
                                "The password is too weak.",
                                Toast.LENGTH_LONG
                            ).show()

                            else -> Toast.makeText(
                                this,
                                "Authentication failed: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            //send all the data to the next activity

        }

        binding.goBack.setOnClickListener {
            val intentGoBack = Intent(this, RegisterActivity::class.java)
            intentGoBack.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            finish()
            startActivity(intentGoBack)
        }



    }
}
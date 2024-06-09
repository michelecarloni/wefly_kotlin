package com.example.wefly.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.wefly.R
import com.example.wefly.databinding.ActivityRegisterBinding
import com.example.wefly.firebase.DataFirebase
import com.example.wefly.utils.ProgressBar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class RegisterActivity : AppCompatActivity() {

    // firebase
    private lateinit var firebaseObject: DataFirebase

    // progressBar
    private lateinit var progressBar: ProgressBar

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var sharedPreference: SharedPreferences
    private lateinit var sharedPreferenceCreaViaggio: SharedPreferences
    private lateinit var dialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        //Firebase.auth.signOut()

        Log.d("CurrentUser", auth.currentUser.toString())

        progressBar = ProgressBar(this)

        firebaseObject = DataFirebase()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreference = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        sharedPreferenceCreaViaggio = getSharedPreferences("CreaViaggio2Prefs", Context.MODE_PRIVATE)


        // Configura il GoogleSignInClient
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.accediEmailBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.accediBtn.setOnClickListener{
            startActivity(Intent(this, SignInActivity::class.java))
        }
        binding.loginGoogleBtn.setOnClickListener {
            /*googleSignInClient.signOut().addOnCompleteListener {
                signInWithGoogle()
            }*/
            signInWithGoogle()
        }

    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result ->
        Log.d("RESULT CODE", "Result code: ${result.resultCode}")
        Log.d("RESULT OK", "Result ok: ${RESULT_OK}")
        if(result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        }
    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUi(account.idToken!!)
            }
        }
    }




    private fun updateUi(idToken: String) {
        Log.d("ENTROOOO", "I'm in")
        progressBar.showProgressBar()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        checkUser(user) { userExists ->
                            if (!userExists) {
                                // If user exists, proceed with CompletaProfiloActivity
                                val account = GoogleSignIn.getLastSignedInAccount(this)
                                if (account != null) {
                                    // Extract user information
                                    val nome = account.givenName
                                    val cognome = account.familyName
                                    val email = account.email

                                    // Start the next activity and pass the user information
                                    val intent = Intent(this, CompletaProfiloActivity::class.java).apply {
                                        putExtra("nome", nome)
                                        putExtra("cognome", cognome)
                                        putExtra("email", email)
                                    }
                                    progressBar.hideProgressBar()
                                    startActivity(intent)
                                    finish()
                                } else {
                                    // If account is null
                                    Toast.makeText(this, "Account is null.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // If user is new, start new activity

                                startNewActivity()
                            }
                        }
                    } else {
                        Toast.makeText(this, "User is null after sign in.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // If sign in fails
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun checkUser(user: FirebaseUser?, callback: (Boolean) -> Unit) {
        val uid = user?.uid
        var check = false

        val databaseReference = firebaseObject.getDatabaseUtenti()

        databaseReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (user in snapshot.children) {
                        if (user.key == uid) {
                            check = true
                            break
                        }
                    }
                }
                callback(check)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    private fun startNewActivity() {
        val user = auth.currentUser
        if (user != null) {
            val databaseReference = firebaseObject.getDatabaseUtenti().child(user.uid)
            val storageReference = firebaseObject.getStorageUtenti().child("${user.uid}.jpg")

            val imageUriTask = storageReference.downloadUrl
            val userDataTask = databaseReference.get()

            Tasks.whenAllComplete(imageUriTask, userDataTask).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val imageUri = imageUriTask.result.toString()
                    val dataSnapshot = userDataTask.result

                    val nome = dataSnapshot.child("nome").getValue(String::class.java)
                    val cognome = dataSnapshot.child("cognome").getValue(String::class.java)
                    val email = dataSnapshot.child("email").getValue(String::class.java)
                    val telefono = dataSnapshot.child("telefono").getValue(String::class.java)
                    val password = dataSnapshot.child("password").getValue(String::class.java)
                    val viaggiStr = dataSnapshot.child("viaggi").getValue(String::class.java)
                    val scelta1 = dataSnapshot.child("scelta1").getValue(Boolean::class.java)
                    val scelta2 = dataSnapshot.child("scelta2").getValue(Boolean::class.java)
                    val scelta3 = dataSnapshot.child("scelta3").getValue(Boolean::class.java)
                    val scelta4 = dataSnapshot.child("scelta4").getValue(Boolean::class.java)
                    val scelta5 = dataSnapshot.child("scelta5").getValue(Boolean::class.java)
                    val scelta6 = dataSnapshot.child("scelta6").getValue(Boolean::class.java)
                    val scelta7 = dataSnapshot.child("scelta7").getValue(Boolean::class.java)
                    val scelta8 = dataSnapshot.child("scelta8").getValue(Boolean::class.java)
                    val scelta9 = dataSnapshot.child("scelta9").getValue(Boolean::class.java)
                    val scelta10 = dataSnapshot.child("scelta10").getValue(Boolean::class.java)

                    Log.d("imageUriRegister", imageUri.toString())

                    setSharedPreferences(
                        user.uid, nome, cognome, email, telefono, password, viaggiStr,
                        scelta1, scelta2, scelta3, scelta4, scelta5, scelta6, scelta7,
                        scelta8, scelta9, scelta10, imageUri
                    )
                } else {
                    // Handle the error if necessary
                    task.exception?.let {
                        Log.e("FirebaseError", "Error: ${it.message}")
                    }
                    Toast.makeText(this, "Failed to load user data.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setSharedPreferences(uid: String?, nome: String?, cognome: String?, email: String?, telefono: String?, password: String?, viaggiStr: String?, scelta1: Boolean?, scelta2: Boolean?, scelta3: Boolean?, scelta4: Boolean?, scelta5: Boolean?, scelta6: Boolean?, scelta7: Boolean?, scelta8: Boolean?, scelta9: Boolean?, scelta10: Boolean?, imageUri: String?){
        val editor = sharedPreference.edit()
        editor.putString("uid", uid)
        editor.putString("nome", nome)
        editor.putString("cognome", cognome)
        editor.putString("email", email)
        editor.putString("telefono", telefono)
        editor.putString("password", password)
        editor.putString("viaggi", viaggiStr)
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
        editor.putString("imageUri", imageUri)
        editor.apply()

        progressBar.hideProgressBar()
        startActivity(Intent(this, MainActivity::class.java))
    }



    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }
    companion object {
        private const val TAG = "RegisterActivity"
    }


}
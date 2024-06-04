package com.example.wefly

import android.content.Context
import android.content.pm.PackageManager
import android.media.RouteListingPreference
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.wefly.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.checkbox.MaterialCheckBox
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private var imageUri: String? = null

    // variables for getting data from SignUp
    private lateinit var uid: String
    private lateinit var nome: String
    private lateinit var cognome: String
    private lateinit var telefono: String
    private lateinit var email: String
    private lateinit var password: String

    private val REQUEST_PERMISSIONS_CODE = 1001
    private val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissions()
        }

        // enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // Get the NavController
        navController = navHostFragment.navController

        val navView:BottomNavigationView = binding.bottomNavigation

        var item = navView.menu.findItem(R.id.navigation_profilo)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.map_viaggi -> {
                    navController.navigate(R.id.map_viaggi)
                    true
                }
                R.id.navigation_crea_viaggio -> {
                    navController.navigate(R.id.navigation_crea_viaggio)
                    true
                }
                R.id.navigation_chat -> {
                    navController.navigate(R.id.navigation_chat)
                    true
                }
                R.id.navigation_profilo -> {
                    sendDataToProfilo()
                    true
                }


                else -> false
            }
        }
    }

    private fun sendDataToProfilo(){

        uid = intent.getStringExtra("uid") ?: ""
        nome = intent.getStringExtra("nome") ?: ""
        cognome = intent.getStringExtra("cognome") ?: ""
        telefono = intent.getStringExtra("telefono") ?: ""
        email = intent.getStringExtra("email") ?: ""
        password = intent.getStringExtra("password") ?: ""
        var scelta1 = intent.getBooleanExtra("scelta1", false)
        var scelta2 = intent.getBooleanExtra("scelta2", false)
        var scelta3 = intent.getBooleanExtra("scelta3", false)
        var scelta4 = intent.getBooleanExtra("scelta4", false)
        var scelta5 = intent.getBooleanExtra("scelta5", false)
        var scelta6 = intent.getBooleanExtra("scelta6", false)
        var scelta7 = intent.getBooleanExtra("scelta7", false)
        var scelta8 = intent.getBooleanExtra("scelta8", false)
        var scelta9 = intent.getBooleanExtra("scelta9", false)
        var scelta10 = intent.getBooleanExtra("scelta10", false)
        imageUri = intent.getStringExtra("imageUri") ?: ""


        val bundle = Bundle().apply{
            putString("uid", uid)
            putString("nome", nome)
            putString("cognome", cognome)
            putString("telefono", telefono)
            putString("email", email)
            putString("password", password)
            putBoolean("scelta1", scelta1)
            putBoolean("scelta2", scelta2)
            putBoolean("scelta3", scelta3)
            putBoolean("scelta4", scelta4)
            putBoolean("scelta5", scelta5)
            putBoolean("scelta6", scelta6)
            putBoolean("scelta7", scelta7)
            putBoolean("scelta8", scelta8)
            putBoolean("scelta9", scelta9)
            putBoolean("scelta10", scelta10)
            putString("imageUri", imageUri)

        }
        Log.d("BUNDLE", bundle.getString("nome") ?: "null")

        navController.navigate(R.id.navigation_profilo, bundle)
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

}
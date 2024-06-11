package com.example.wefly.activity

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.wefly.utils.KeyConstant
import com.example.wefly.R
import com.example.wefly.databinding.ActivityMainBinding
import com.example.wefly.utils.ProgressBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zegocloud.zimkit.services.ZIMKit
import im.zego.zim.entity.ZIMError
import im.zego.zim.enums.ZIMErrorCode

class MainActivity : AppCompatActivity() {

    // ProgressBar
    private lateinit var progressBar: ProgressBar

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private lateinit var sharedPreferences: SharedPreferences

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

        progressBar = ProgressBar(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val navView: BottomNavigationView = binding.bottomNavigation
        navView.setupWithNavController(navController)

        // Set listener to handle navigation for each item in BottomNavigationView
        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.map_viaggi -> {
                    navController.navigate(R.id.map_viaggi)
                    true
                }

                R.id.navigation_crea_viaggio -> {
                    navController.navigate(R.id.navigation_crea_viaggio)
                    true
                }

                R.id.navigation_chat -> {
                    progressBar.showProgressBar()
                    initZegocloud()

                    sharedPreferences = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
                    val uid = sharedPreferences.getString("uid", "")
                    val nomeCognome = sharedPreferences.getString("nome", "") + " " + sharedPreferences.getString("cognome", "")
                    val userAvatar = ""
                    connectUser(uid, nomeCognome, userAvatar)

                    //navController.navigate(R.id.navigation_chat)
                    true
                }

                R.id.navigation_profilo -> {
                    navController.navigate(R.id.navigation_profilo)
                    true
                }

                else -> false
            }
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
                    Log.d("Permissions", "All permissions granted")
                } else {
                    Log.d("Permissions", "Permissions denied")
                }
            }
        }
    }

    private fun initZegocloud(){
        ZIMKit.initWith(this.application, KeyConstant.appId, KeyConstant.appSign)
        ZIMKit.initNotifications()
    }

    fun connectUser(userId: String?, userName: String?, userAvatar: String?) {
        // Logs in.
        ZIMKit.connectUser(
            userId, userName, userAvatar
        ) { errorInfo: ZIMError ->
            if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                // Operation after successful login. You will be redirected to other modules only after successful login. In this sample code, you will be redirected to the conversation module.
                toChatListActivity()
            } else {
            }
        }
    }

    private fun toChatListActivity() {
        // Redirect to the conversation list (Activity) you created.
        //val intent = Intent(this, ConversationActivity::class.java)
        //startActivity(intent)
        progressBar.hideProgressBar()
        navController.navigate(R.id.navigation_chat)
    }
}
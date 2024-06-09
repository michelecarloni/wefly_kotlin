package com.example.wefly.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.wefly.R
import com.example.wefly.activity.RegisterActivity
import com.example.wefly.viewmodel.SharedViewModelProfiloDialog
import com.example.wefly.activity.ViaggiAttualiActivity
import com.example.wefly.activity.ViaggiPassatiActivity
import com.example.wefly.databinding.FragmentProfiloBinding
import com.example.wefly.utils.ProgressBar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfiloFragment : Fragment() {

    // progressBar
    private lateinit var progressBar: ProgressBar

    private lateinit var binding: FragmentProfiloBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth

    private lateinit var sharedViewModel: SharedViewModelProfiloDialog

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var dialog : Dialog

    private val REQUEST_PERMISSIONS_CODE = 1001
    private val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using the binding variable
        binding = FragmentProfiloBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModelProfiloDialog::class.java)
        progressBar = ProgressBar(this@ProfiloFragment.requireContext())

        // Check and request permissions
        if (!hasPermissions(requireContext(), REQUIRED_PERMISSIONS)) {
            requestPermissions()
        }

        // Set the text for the toolbar
        val title = view.findViewById<TextView>(R.id.toolbar_title)
        title.text = "Profilo"



        // Load saved content
        CoroutineScope(Dispatchers.Main).launch {
            loadContent()
        }

        val viaggiAttualiBtn = view.findViewById<MaterialButton>(R.id.viaggi_attuali_btn)
        viaggiAttualiBtn.setOnClickListener {
            val intentViaggiAttuali = Intent(context, ViaggiAttualiActivity::class.java)
            startActivity(intentViaggiAttuali)
        }

        val viaggiPassatiBtn = view.findViewById<MaterialButton>(R.id.viaggi_passati_btn)
        viaggiPassatiBtn.setOnClickListener {
            val intentViaggiPassati = Intent(context, ViaggiPassatiActivity::class.java)
            startActivity(intentViaggiPassati)
        }

        val editStateBtn = view.findViewById<ImageView>(R.id.edit_state_btn)
        editStateBtn.setOnClickListener {
            val infoUtentePopUp = PopUpInfoUtenteFragment()
            infoUtentePopUp.show(this.parentFragmentManager, "InfoUtentePopUp")
        }

        sharedViewModel.event.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    loadContent()
                }
            }
        })

        binding.logoutBtn.setOnClickListener{
            goDialog()
        }
    }

    private fun goDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Sei sicuro di voler disconnetterti?")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()

                //clean all the user

                auth = Firebase.auth
                Firebase.auth.signOut()

                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

                googleSignInClient.signOut().addOnCompleteListener {
                    // Clear shared preferences
                    sharedPreferences.edit().clear().apply()

                    // Navigate back to RegisterActivity
                    val intent = Intent(context, RegisterActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }




            }
            .setNegativeButton("Annulla") { dialog, _ ->
                dialog.dismiss()
            }

        val alert = dialogBuilder.create()
        alert.show()
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

    private suspend fun loadContent() {
        progressBar.showProgressBar()

        withContext(Dispatchers.IO) {
            val name = sharedPreferences.getString("nome", "")
            val surname = sharedPreferences.getString("cognome", "")
            val imageUriString = sharedPreferences.getString("imageUri", "")
            val uid = sharedPreferences.getString("uid", "")

            Log.d("UID3", uid.toString())

            withContext(Dispatchers.Main) {
                binding.nomeUtente.setText(name)
                binding.cognomeUtente.setText(surname)
            }

            withContext(Dispatchers.Main) {
                if (imageUriString!!.contains("https://firebasestorage")) {
                    Glide.with(requireContext())
                        .load(imageUriString)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.profile_picture)
                        .into(binding.profilePicture)
                } else {
                    val imageUri = imageUriString?.let { Uri.parse(it) }
                    binding.profilePicture.setImageURI(imageUri)
                }
            }

            withContext(Dispatchers.Main) {
                loadInteressi()
            }
        }

        progressBar.hideProgressBar()
    }

    private fun loadInteressi() {
        val listaInteressi = arrayListOf<String>()
        val list = booleanArrayOf(
            sharedPreferences.getBoolean("scelta1", false),
            sharedPreferences.getBoolean("scelta2", false),
            sharedPreferences.getBoolean("scelta3", false),
            sharedPreferences.getBoolean("scelta4", false),
            sharedPreferences.getBoolean("scelta5", false),
            sharedPreferences.getBoolean("scelta6", false),
            sharedPreferences.getBoolean("scelta7", false),
            sharedPreferences.getBoolean("scelta8", false),
            sharedPreferences.getBoolean("scelta9", false),
            sharedPreferences.getBoolean("scelta10", false)
        )

        var stringaInteressi = ""

        for (i in list.indices) {
            if (list[i]) {
                // Get the resource ID dynamically
                val resId = resources.getIdentifier("scelta${i + 1}", "string", context?.packageName)

                // Use getString() to get the actual string value
                if (resId != 0) {
                    val interesse = getString(resId)
                    stringaInteressi += interesse
                    listaInteressi.add(interesse)
                }
                if (i != list.lastIndex) {
                    stringaInteressi += ", "
                }
            }
        }

        binding.interessiUtente.setText(stringaInteressi)
        progressBar.hideProgressBar()
    }


}
package com.example.wefly.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.wefly.activity.CompletaProfiloActivity
import com.example.wefly.R
import com.example.wefly.viewmodel.SharedViewModelProfiloDialog
import com.example.wefly.databinding.FragmentPopUpInfoUtenteBinding
import com.example.wefly.firebase.DataFirebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class PopUpInfoUtenteFragment : DialogFragment() {

    // firebase
    private lateinit var firebaseObject: DataFirebase

    private lateinit var binding: FragmentPopUpInfoUtenteBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null

    private lateinit var sharedViewModel: SharedViewModelProfiloDialog

    private lateinit var sharedPreferences: SharedPreferences

    // variabili per salvare lo stato nel database
    private lateinit var uid: String
    private lateinit var nome: String
    private lateinit var cognome: String
    private lateinit var telefono: String
    private lateinit var email : String
    private lateinit var password : String
    private lateinit var viaggiStr : String
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPopUpInfoUtenteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModelProfiloDialog::class.java)
        firebaseObject = DataFirebase()

        sharedPreferences = requireActivity().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        databaseReference = firebaseObject.getDatabaseUtenti()

        loadState()

        binding.changeImageBtn.setOnClickListener{
            pickImageGallery()
        }

        binding.salvaBtn.setOnClickListener {
            saveLocalState()
            saveStateOnDatabase()
            sharedViewModel.triggerEvent()
            dismiss()
        }
    }

    private fun loadState(){
        val sharedPreferences = requireActivity().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        val imageUriString = sharedPreferences.getString("imageUri", "")

        if(imageUriString!!.contains("https://firebasestorage")){
            Glide.with(this)
                .load(imageUriString)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.profile_picture)
                .into(binding.profilePicture)
        }
        else{
            val imageUri = imageUriString?.let { Uri.parse(it) }
            binding.profilePicture.setImageURI(imageUri)
        }


        binding.nomeEditText.setText(sharedPreferences.getString("nome", ""))
        binding.cognomeEditText.setText(sharedPreferences.getString("cognome", ""))
        binding.telefonoEditText.setText(sharedPreferences.getString("telefono", ""))
        binding.scelta1.isChecked = sharedPreferences.getBoolean("scelta1", false)
        binding.scelta2.isChecked = sharedPreferences.getBoolean("scelta2", false)
        binding.scelta3.isChecked = sharedPreferences.getBoolean("scelta3", false)
        binding.scelta4.isChecked = sharedPreferences.getBoolean("scelta4", false)
        binding.scelta5.isChecked = sharedPreferences.getBoolean("scelta5", false)
        binding.scelta6.isChecked = sharedPreferences.getBoolean("scelta6", false)
        binding.scelta7.isChecked = sharedPreferences.getBoolean("scelta7", false)
        binding.scelta8.isChecked = sharedPreferences.getBoolean("scelta8", false)
        binding.scelta9.isChecked = sharedPreferences.getBoolean("scelta9", false)
        binding.scelta10.isChecked = sharedPreferences.getBoolean("scelta10", false)

    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, CompletaProfiloActivity.IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CompletaProfiloActivity.IMAGE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            imageUri = data?.data
            binding.profilePicture.setImageURI(imageUri)
        }
    }

    private fun saveLocalState(){

        val editor = sharedPreferences.edit()

        uid = sharedPreferences.getString("uid", "") ?: ""
        email = sharedPreferences.getString("email", "") ?: ""
        password = sharedPreferences.getString("password", "") ?: ""
        viaggiStr = sharedPreferences.getString("viaggiStr", "") ?: ""

        nome = binding.nomeEditText.text.toString()
        cognome = binding.cognomeEditText.text.toString()
        telefono = binding.telefonoEditText.text.toString()
        scelta1 = binding.scelta1.isChecked
        scelta2 = binding.scelta2.isChecked
        scelta3 = binding.scelta3.isChecked
        scelta4 = binding.scelta4.isChecked
        scelta5 = binding.scelta5.isChecked
        scelta6 = binding.scelta6.isChecked
        scelta7 = binding.scelta7.isChecked
        scelta8 = binding.scelta8.isChecked
        scelta9 = binding.scelta9.isChecked
        scelta10 = binding.scelta10.isChecked


        editor.putString("nome", nome)
        editor.putString("cognome", cognome)
        editor.putString("telefono", telefono)
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

    private fun saveStateOnDatabase() {
        val userMap = mapOf(
            "nome" to nome,
            "cognome" to cognome,
            "telefono" to telefono,
            "email" to email,
            "password" to password,
            "viaggiStr" to viaggiStr,
            "scelta1" to scelta1,
            "scelta2" to scelta2,
            "scelta3" to scelta3,
            "scelta4" to scelta4,
            "scelta5" to scelta5,
            "scelta6" to scelta6,
            "scelta7" to scelta7,
            "scelta8" to scelta8,
            "scelta9" to scelta9,
            "scelta10" to scelta10
        )

        databaseReference.child(uid).setValue(userMap)

        if (imageUri != null) {
            storageReference = firebaseObject.getStorageUtenti().child("${uid}.jpg")
            storageReference.putFile(imageUri!!)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.window?.setLayout(width, height)
            it.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

}
package com.example.wefly.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.wefly.R
import com.example.wefly.adapter.AdapterMessage
import com.example.wefly.firebase.DataFirebase
import com.example.wefly.model.ModelMessage
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class ChatViaggioFragment : Fragment() {

    // firebase
    private lateinit var firebaseObject: DataFirebase

    val args: ChatViaggioFragmentArgs by navArgs()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var adapter : AdapterMessage
    var modelMessage: ArrayList<ModelMessage>?=null
    var senderRoom:String?=null
    var receiverRoom:String?=null
    var senderUid:String?=null
    var receiverUid:String?=null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view=inflater.inflate(R.layout.fragment_chat_viaggio, container, false)

        val url = args.url
        val nome=args.nome
        val cognome=args.cognome

        firebaseObject = DataFirebase()

        Log.d("urlChatViaggio",url)

        databaseReference = firebaseObject.getDatabaseChats()
        storageReference = firebaseObject.getStorageUtenti()

        val img=view.findViewById<ImageView>(R.id.profileImage)
        val txtNome=view.findViewById<TextView>(R.id.nome_txt)
        val txtCognome=view.findViewById<TextView>(R.id.cognome_txt)

        Glide.with(this)
            .load(url)
            .fitCenter() // Corrected the method name
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.placeholder)
            .into(img)

        txtNome.text=nome
        txtCognome.text=cognome

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inflate the layout for this fragment

    }

}
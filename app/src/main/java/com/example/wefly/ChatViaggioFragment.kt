package com.example.wefly

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.example.wefly.adapter.MessageAdapter
import com.example.wefly.model.Message
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class ChatViaggioFragment : Fragment() {
    val args: ChatViaggioFragmentArgs by navArgs()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var adapter : MessageAdapter
    var message: ArrayList<Message>?=null
    var senderRoom:String?=null
    var receiverRoom:String?=null
    var senderUid:String?=null
    var receiverUid:String?=null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val titoloViaggio=args.titoloViaggio
        val immagineViaggio=args.immagineViaggio
        val view=inflater.inflate(R.layout.fragment_chat_viaggio, container, false)
        databaseReference =
            FirebaseDatabase.getInstance("https://wefly-d50f7-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("Chats")
        storageReference = FirebaseStorage.getInstance()
            .getReference("ViaggiTotali")
        val img=view.findViewById<ImageView>(R.id.travelImage)
        val txt=view.findViewById<TextView>(R.id.travelTitle)
        img.setImageResource(immagineViaggio)
        txt.text=titoloViaggio

        return view
    }

}
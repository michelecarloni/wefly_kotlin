package com.example.wefly.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wefly.R
import com.example.wefly.adapter.AdapterChatList
import com.example.wefly.firebase.DataFirebase
import com.example.wefly.model.ModelChatList
import com.example.wefly.utils.ProgressBar
import com.example.wefly.viewmodel.ChatListViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class ChatListFragment : Fragment() {

    // firebase
    private lateinit var firebaseObject: DataFirebase

    // ProgressBar
    private lateinit var progressBar : ProgressBar

    // ViewModel
    private lateinit var ChatListViewModel: ChatListViewModel

    // dichiarazione variables
    private lateinit var adapter : AdapterChatList // dichiarazione dell'adapter
    private lateinit var recyclerView : RecyclerView // dichiarazione della recyclerView
    private lateinit var chatArrayList : ArrayList<ModelChatList> // ArrayList di objects
    private lateinit var navController: NavController

    private lateinit var uid : String

    private lateinit var viaggiStrArrayList : MutableList<String>
    private lateinit var partecipantiStrArrayList : MutableList<String>

    private lateinit var dialog : Dialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ChatListViewModel = ViewModelProvider(this@ChatListFragment).get(com.example.wefly.viewmodel.ChatListViewModel::class.java)
        progressBar = ProgressBar(this@ChatListFragment.requireContext())
        firebaseObject = DataFirebase()

        //set the text for the tool bar
        val title = view.findViewById<TextView>(R.id.toolbar_title)
        title.text = "Chat"

        navController=findNavController()
        dataInitialize()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recyclerViewChat)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = AdapterChatList(navController,chatArrayList)
        recyclerView.adapter = adapter

    }



    private fun dataInitialize(){
        progressBar.showProgressBar()
        var sharedPreferences = requireActivity().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        chatArrayList = arrayListOf<ModelChatList>()
        viaggiStrArrayList = mutableListOf()
        partecipantiStrArrayList = mutableListOf()

        uid = sharedPreferences.getString("uid", "").toString()

        val databaseReferences = firebaseObject.getDatabaseUtenti().child(uid)

        databaseReferences.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val viaggiStr = snapshot.child("viaggiStr").value.toString()
                    createViaggiStrList(viaggiStr){
                        createPartecipantiStrList{
                            readUsers()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun createViaggiStrList(viaggiStr: String,onComplete:() -> Unit){
        viaggiStr.dropLast(2)
        viaggiStrArrayList = ArrayList(viaggiStr.split(", "))
        onComplete()
    }

    private fun createPartecipantiStrList(onComplete:() -> Unit){
        val databaseReferences = firebaseObject.getDatabaseViaggi()

        var partecipantiStrTot = ""

        databaseReferences.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(viaggio in snapshot.children){
                        if(viaggiStrArrayList.contains(viaggio.key.toString())) {
                            val partecipantiStr = viaggio.child("partecipantiStr").value.toString()
                            partecipantiStrTot += partecipantiStr
                            Log.d("partecipantiStrTot", "$partecipantiStrTot")
                        }
                    }
                    partecipantiStrTot.dropLast(2)

                    partecipantiStrArrayList = ArrayList(partecipantiStrTot.split(", "))

                    // rimuovi tutti i duplicati
                    partecipantiStrArrayList = ArrayList(partecipantiStrArrayList.toSet())

                    // rimuovo l'uid dell'utente attualmente loggato dalla lista (sennò l'utente loggato avrebbe la chat con se stesso)
                    partecipantiStrArrayList.remove(uid)

                    Log.d("partecipantiStrArraylist", "$partecipantiStrArrayList")
                    onComplete()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })



    }


    private fun readUsers(){
        val databaseReferences = firebaseObject.getDatabaseUtenti()

        databaseReferences.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(user in snapshot.children){
                        if(partecipantiStrArrayList.contains(user.key.toString())){

                            val userUid = user.key.toString()
                            val nome = user.child("nome").value.toString()
                            val cognome = user.child("cognome").value.toString()
                            getPictureTravel(userUid, object : OnImageUrlReceivedListener {
                                override fun onImageUrlReceived(url: String) {
                                    //val user = ModelChatList(url, nome, cognome)
                                    val user = ModelChatList(url, nome, cognome)
                                    chatArrayList.add(user)
                                    Log.d("UserRead", "$userUid")
                                    adapter.notifyDataSetChanged()
                                }

                            })

                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        progressBar.hideProgressBar()
    }

    interface OnImageUrlReceivedListener {
        fun onImageUrlReceived(url: String)
    }

    private fun getPictureTravel(uid: String, listener: OnImageUrlReceivedListener) {
        val storageReference = firebaseObject.getStorageUtenti().child("$uid.jpg")

        storageReference.downloadUrl.addOnSuccessListener { uri ->
            val url = uri.toString()
            listener.onImageUrlReceived(url)
        }.addOnFailureListener { exception ->
            exception.printStackTrace()
            listener.onImageUrlReceived("") // or handle the error as needed
        }
    }


}
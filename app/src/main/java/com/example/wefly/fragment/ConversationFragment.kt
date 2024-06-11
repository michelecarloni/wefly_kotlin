package com.example.wefly.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
import com.google.firebase.database.ValueEventListener
import com.zegocloud.zimkit.common.ZIMKitRouter
import com.zegocloud.zimkit.common.enums.ZIMKitConversationType

class ConversationFragment : Fragment() {

    val args: ConversationFragmentArgs by navArgs()

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
        return inflater.inflate(R.layout.fragment_conversation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = args.userUid
        ZIMKitRouter.toMessageActivity(this.requireContext(), uid, ZIMKitConversationType.ZIMKitConversationTypePeer)

    }


}
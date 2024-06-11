package com.example.wefly.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.wefly.model.ModelChatList
import com.example.wefly.R
import com.example.wefly.fragment.ChatListFragmentDirections

class AdapterChatList (private val navController: NavController, private val newList : ArrayList<ModelChatList>) : RecyclerView.Adapter<AdapterChatList.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_chat,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = newList[position]

        Glide.with(holder.itemView.context) // Use the context from the holder's itemView
            .load(currentItem.url)
            .fitCenter() // Corrected the method name
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.placeholder)
            .into(holder.immagine)

        holder.nome.text = currentItem.nome
        holder.cognome.text = currentItem.cognome

        holder.itemView.setOnClickListener {
            val action = ChatListFragmentDirections.actionNavigationChatToConversationFragment(currentItem.url,currentItem.nome,currentItem.cognome, currentItem.userUid)
            navController.navigate(action)
        }

    }

    override fun getItemCount(): Int {
        return newList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val immagine : ImageView = itemView.findViewById(R.id.title_image)
        val nome : TextView = itemView.findViewById(R.id.nomeTxt)
        val cognome : TextView = itemView.findViewById(R.id.cognomeTxt)

    }

}
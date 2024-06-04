package com.example.wefly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterChatList (private val newList : ArrayList<DataChatList>) : RecyclerView.Adapter<AdapterChatList.MyViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = newList[position]
        holder.immagineProfilo.setImageResource(currentItem.immagineProfilo)
        holder.titoloViaggio.text = currentItem.titoloViaggio
    }

    override fun getItemCount(): Int {
        return newList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val immagineProfilo : ImageView = itemView.findViewById(R.id.title_image)
        val titoloViaggio : TextView = itemView.findViewById(R.id.titolo)

    }

}
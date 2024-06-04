package com.example.wefly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterElencoViaggi (private var newList : ArrayList<DataElencoViaggi>) : RecyclerView.Adapter<AdapterElencoViaggi.MyViewHolder>(){

    var onItemClick: ((DataElencoViaggi) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = newList[position]
        //holder.titoloImmagine.setImageResource(currentItem.titoloImmagine)
        holder.titoloViaggio.text = currentItem.titoloViaggio
        holder.citta.text = currentItem.citta
        holder.dataPartenza.text = currentItem.dataPartenza
        holder.budget.text = currentItem.budget

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentItem)
        }

    }

    override fun getItemCount(): Int {
        return newList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //val titoloImmagine : ImageView = itemView.findViewById(R.id.title_image)
        val titoloViaggio : TextView = itemView.findViewById(R.id.titolo)
        val citta : TextView = itemView.findViewById(R.id.citta)
        val dataPartenza : TextView = itemView.findViewById(R.id.partenza)
        val budget : TextView = itemView.findViewById(R.id.budget)

    }

    fun setFilteredList(newList: ArrayList<DataElencoViaggi>) {
        this.newList = newList
        notifyDataSetChanged()
    }


}
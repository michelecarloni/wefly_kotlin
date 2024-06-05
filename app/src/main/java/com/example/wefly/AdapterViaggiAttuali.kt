import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wefly.R

class AdapterViaggiAttuali(private val newList: ArrayList<DataViaggiAttuali>) :
    RecyclerView.Adapter<AdapterViaggiAttuali.MyViewHolder>() {

    var onItemClick: ((DataViaggiAttuali) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_viaggi_attuali, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = newList[position]
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
        val titoloViaggio: TextView = itemView.findViewById(R.id.titolo)
        val citta: TextView = itemView.findViewById(R.id.citta)
        val dataPartenza: TextView = itemView.findViewById(R.id.partenza)
        val budget: TextView = itemView.findViewById(R.id.budget)
    }
}






/*package com.example.wefly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterViaggiAttuali (private val newList : ArrayList<DataViaggiAttuali>) : RecyclerView.Adapter<AdapterViaggiAttuali.MyViewHolder>(){

    var onItemClick : ((DataViaggiAttuali) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_viaggi_attuali,
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

}*/
package com.example.wefly.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.wefly.model.ModelElencoViaggi
import com.example.wefly.R

class AdapterElencoViaggi (private var newList : ArrayList<ModelElencoViaggi>) : RecyclerView.Adapter<AdapterElencoViaggi.MyViewHolder>(){

    var onItemClick: ((ModelElencoViaggi) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = newList[position]

        Glide.with(holder.itemView.context) // Use the context from the holder's itemView
            .load(currentItem.imageUrl)
            .fitCenter() // Corrected the method name
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.placeholder)
            .into(holder.imageView)

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
        val imageView : ImageView = itemView.findViewById(R.id.title_image)
        val titoloViaggio : TextView = itemView.findViewById(R.id.titolo)
        val citta : TextView = itemView.findViewById(R.id.citta)
        val dataPartenza : TextView = itemView.findViewById(R.id.partenza)
        val budget : TextView = itemView.findViewById(R.id.budget)

    }

    fun setFilteredList(newList: ArrayList<ModelElencoViaggi>) {
        this.newList = newList
        notifyDataSetChanged()
    }


}
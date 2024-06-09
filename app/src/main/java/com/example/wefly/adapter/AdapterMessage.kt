package com.example.wefly.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wefly.R
import com.example.wefly.databinding.SendMsgBinding
import com.example.wefly.model.ModelMessage
import com.google.firebase.auth.FirebaseAuth

class AdapterMessage(
    var context: Context,
    modelMessages:ArrayList<ModelMessage>?,
    senderRoom:String,
    receiverRoom:String
): RecyclerView.Adapter<RecyclerView.ViewHolder?>(){
    lateinit var modelMessages:ArrayList<ModelMessage>
    val ITEM_SENT=1
    val ITEM_RECEIVE=2
    val senderRoom:String
    val receiverRoom:String
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType==ITEM_SENT){
            val view=LayoutInflater.from(context).inflate(R.layout.send_msg,parent,false)
            SentMsgHolder(view)
        }
        else{
            val view=LayoutInflater.from(context).inflate(R.layout.receive_msg,parent,false)
            ReceiveMsgHolder(view)

        }
    }

    override fun getItemViewType(position: Int): Int {
        val messages=modelMessages[position]
        return if(FirebaseAuth.getInstance().uid==messages.senderId){
            ITEM_SENT
        }else{
            ITEM_RECEIVE
        }
    }
    override fun getItemCount(): Int =modelMessages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message=modelMessages[position]
        if(holder.javaClass==SentMsgHolder::class.java){
            val viewHolder=holder as SentMsgHolder
            viewHolder.binding.message.text=message.message

        }
    }
    inner class SentMsgHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var binding:SendMsgBinding= SendMsgBinding.bind(itemView)
    }
    inner class ReceiveMsgHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var binding:SendMsgBinding= SendMsgBinding.bind(itemView)
    }
    init{
        if(modelMessages!=null){
            this.modelMessages=modelMessages
        }
        this.senderRoom=senderRoom
        this.receiverRoom=receiverRoom
    }




}
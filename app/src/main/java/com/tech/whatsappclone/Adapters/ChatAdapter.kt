package com.tech.whatsappclone.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tech.whatsappclone.Model.MessageModel
import com.tech.whatsappclone.R

class ChatAdapter(
    private var messageList: ArrayList<MessageModel>,
    var context: Context,
    private var receiverId: String?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var SENDER_VIEW_TYPE = 1
    private var RECEIVER_VIEW_TYPE = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SENDER_VIEW_TYPE) {
            val view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false)
            SenderViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.sample_receiver, parent, false)
            ReceiverViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val messageModel = messageList[position]
        if (holder.javaClass == SenderViewHolder::class.java) {
            (holder as SenderViewHolder).senderMsg.text = messageModel.getMessage()
        } else {
            (holder as ReceiverViewHolder).receiverMsg.text = messageModel.getMessage()
        }

        //delete chat on Long click listener
        holder.itemView.setOnLongClickListener {
            android.app.AlertDialog.Builder(context)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this message")
                .setPositiveButton(
                    "yes"
                ) { dialogInterface, i ->
                    val database:FirebaseDatabase = FirebaseDatabase.getInstance()
                    if(receiverId!=null){
                        //personal chat delete
                        val senderRoom:String = FirebaseAuth.getInstance().uid.toString()+receiverId
                        Log.d("@@A",messageModel.getMessageId().toString())
                        database.reference.child("chats").child(senderRoom)
                            .child(messageModel.getMessageId().toString())
                            .setValue(null)
                    }else{
                        //Group chat message delete
                        database.reference.child("Group Chat")
                            .child(messageModel.getMessageId().toString())
                            .setValue(null)
                    }
                    Toast.makeText(context, "Delete message", Toast.LENGTH_SHORT).show()
                }.setNegativeButton(
                    "No"
                ) { dialogInterface, i -> dialogInterface.dismiss() }.show()
            false
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].getUid() == FirebaseAuth.getInstance().uid) {
            SENDER_VIEW_TYPE
        } else {
            RECEIVER_VIEW_TYPE
        }
    }

    internal class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var receiverMsg: TextView
        var receiverTime: TextView

        init {
            receiverMsg = itemView.findViewById(R.id.receiverText)
            receiverTime = itemView.findViewById(R.id.receiverTime)
        }
    }

    internal class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var senderMsg: TextView
        var senderTime: TextView

        init {
            senderMsg = itemView.findViewById(R.id.senderText)
            senderTime = itemView.findViewById(R.id.senderTime)
        }
    }
}

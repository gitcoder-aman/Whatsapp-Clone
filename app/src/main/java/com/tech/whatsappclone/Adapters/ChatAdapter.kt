package com.tech.whatsappclone.Adapters

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.tech.whatsappclone.Model.MessageModel
import com.tech.whatsappclone.R
import java.util.Date

class ChatAdapter(private var messageList: ArrayList<MessageModel>, var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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

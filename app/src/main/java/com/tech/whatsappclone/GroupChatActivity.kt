package com.tech.whatsappclone

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tech.whatsappclone.Adapters.ChatAdapter
import com.tech.whatsappclone.Model.MessageModel
import com.tech.whatsappclone.databinding.ActivityGroupChatBinding
import java.util.Date

class GroupChatActivity : AppCompatActivity() {

    private lateinit var binding:ActivityGroupChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        binding.backArrowBtn.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
        val database:FirebaseDatabase = FirebaseDatabase.getInstance()

        val messageArrayList:ArrayList<MessageModel> = ArrayList()
        val chatAdapter = ChatAdapter(messageArrayList, this)
        binding.chatRecyclerView.adapter = chatAdapter

        val senderId:String = FirebaseAuth.getInstance().uid.toString()
        binding.txtUsername.text = "Friends Group"

        val linearLayoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.layoutManager = linearLayoutManager

        binding.sendBtn.setOnClickListener {
            val message:String = binding.etMessage.text.toString()
            val messageModel = MessageModel(senderId,message)
            messageModel.setTimestamp(Date().time)
            binding.etMessage.setText("")

            database.reference.child("Group Chat")
                .push()
                .setValue(messageModel).addOnSuccessListener {
                    Log.d("@@@@","group chat set on firebase successful")
                }
        }

        //Get Messages from firebase and show on RecyclerView
        database.reference.child("Group Chat").addValueEventListener(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                messageArrayList.clear()
                for (dataSnapshot : DataSnapshot in snapshot.children){
                    val messageModel: MessageModel? = dataSnapshot.getValue(MessageModel::class.java)

                    if (messageModel != null) {
                        messageArrayList.add(messageModel)
                    }
                }
                chatAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("@@@@","dataSnapshot is cancelled")
            }

        })
    }
}
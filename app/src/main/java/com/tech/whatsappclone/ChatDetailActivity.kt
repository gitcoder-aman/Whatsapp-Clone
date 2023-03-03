package com.tech.whatsappclone

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.tech.whatsappclone.Adapters.ChatAdapter
import com.tech.whatsappclone.Model.MessageModel
import com.tech.whatsappclone.databinding.ActivityChatDetailBinding
import java.util.Date

class ChatDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatDetailBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth :FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val senderId : String = auth.uid.toString()
        var receiverId = intent.getStringExtra("userId")
        val userName = intent.getStringExtra("username")
        val profilePic = intent.getStringExtra("profilePic")

        binding.txtUsername.text = userName
        Picasso.get().load(profilePic)
            .placeholder(R.drawable.profile)
            .into(binding.profileImage)

        binding.backArrowBtn.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
        val messageArrayList:ArrayList<MessageModel> = ArrayList()
        val chatAdapter:ChatAdapter = ChatAdapter(messageArrayList,this)
        binding.chatRecyclerView.adapter = chatAdapter

        val linearLayoutManager:LinearLayoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.layoutManager = linearLayoutManager

        val senderRoom:String = senderId+receiverId
        val receiverRoom:String = receiverId+senderId
        //send button on Click
        binding.sendBtn.setOnClickListener {
           val message = binding.etMessage.text.toString()
            val messageModel = MessageModel(senderId,message)
            messageModel.setTimestamp(Date().time)
            binding.etMessage.setText("")

            //now we're working sender model set on firebase
            database.reference.child("chats")
                .child(senderRoom)
                .push()
                .setValue(messageModel).addOnSuccessListener {
                    Log.d("@@@@","Sender messageSet on firebase")

                    //now we're working receiver model set on firebase
                    database.reference.child("chats")
                        .child(receiverRoom)
                        .push()
                        .setValue(messageModel).addOnSuccessListener {
                            Log.d("@@@@","Receiver messageSet on firebase")
                        }
            }
        }

        database.reference.child("chats")
            .child(senderRoom).addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageArrayList.clear()
                    for(dataSnapshot:DataSnapshot in snapshot.children){
                        val messageModel = dataSnapshot.getValue(MessageModel::class.java)
                        if (messageModel != null) {
                            messageArrayList.add(messageModel)
                        }
                    }
                    chatAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("@@@@","addValueEventListener Failed"+error.message)
                }

            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
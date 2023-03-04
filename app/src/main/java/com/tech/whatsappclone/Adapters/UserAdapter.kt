package com.tech.whatsappclone.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.tech.whatsappclone.ChatDetailActivity
import com.tech.whatsappclone.Model.UserModel
import com.tech.whatsappclone.R

class UserAdapter(private val dataset: ArrayList<UserModel>,val context: Context?) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sample_show_user,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userModel : UserModel = dataset[position]
        Picasso.get().load(userModel.getProfilePic())
            .placeholder(R.drawable.profile)
            .into(holder.profileImage)

        holder.txtUsername.text = userModel.getUserName()

        //last message set on chat fragment
        FirebaseDatabase.getInstance().reference.child("chats")
            .child(FirebaseAuth.getInstance().uid+userModel.getUserId())
            .orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChildren()){
                        for(dataSnapshot : DataSnapshot in snapshot.children){
                            holder.txtLastMessage.text = dataSnapshot.child("message").value.toString()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("@@@@","timestamp on Cancelled")
                }

            })

        holder.itemView.setOnClickListener {
            val intent = Intent(context,ChatDetailActivity::class.java)
            intent.putExtra("userId",userModel.getUserId())
            intent.putExtra("profilePic",userModel.getProfilePic())
            intent.putExtra("username",userModel.getUserName())
            context?.startActivity(intent)
        }

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
         val profileImage:ImageView
         val txtUsername : TextView
         val txtLastMessage : TextView

        init {
            profileImage = view.findViewById(R.id.profile_image)
            txtUsername = view.findViewById(R.id.txtUsername)
            txtLastMessage = view.findViewById(R.id.txtLastMessage)
        }
    }
}
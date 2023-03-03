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
         private val txtLastMessage : TextView

        init {
            profileImage = view.findViewById(R.id.profile_image)
            txtUsername = view.findViewById(R.id.txtUsername)
            txtLastMessage = view.findViewById(R.id.txtLastMessage)
        }
    }
}
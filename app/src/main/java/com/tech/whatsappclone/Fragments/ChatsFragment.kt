package com.tech.whatsappclone.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tech.whatsappclone.Adapters.UserAdapter
import com.tech.whatsappclone.Model.UserModel
import com.tech.whatsappclone.databinding.FragmentChatsBinding


class ChatsFragment : Fragment() {


    private lateinit var binding: FragmentChatsBinding

    var list:ArrayList<UserModel> = ArrayList()
    private lateinit var database: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater, container, false)

        val linearLayoutManager  = LinearLayoutManager(context)
        binding.chatRecyclerView.layoutManager = linearLayoutManager
        binding.chatRecyclerView.setHasFixedSize(true)

        list = arrayListOf<UserModel>()
        val adapter = UserAdapter(list,context)
        binding.chatRecyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance().getReference("Users")
        database.addValueEventListener(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    list.clear()
                    for (dataSnapshot  in snapshot.children ){
                        val userModel = dataSnapshot.getValue(UserModel::class.java)
                        userModel?.setUserId(dataSnapshot.key)
                            list.add(userModel!!)
                    }
                    adapter.notifyDataSetChanged()
                }else{
                    Log.d("@@@@","Snapshot is not exist")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("@@@@","loadPost::Cancelled"+error.message)
            }

        })

        return binding.root
    }


}
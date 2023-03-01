package com.tech.whatsappclone.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tech.whatsappclone.Fragments.CallsFragment
import com.tech.whatsappclone.Fragments.ChatsFragment
import com.tech.whatsappclone.Fragments.StatusFragment

open class FragmentsAdapter(private val fm : FragmentManager, private val lifecycle: Lifecycle) : FragmentStateAdapter(fm,lifecycle)
{
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
         when (position) {
            0 -> {
                return ChatsFragment()
            }
            1 -> {
                return StatusFragment()
            }
            2 -> {
                return CallsFragment()
            }

        }
        return ChatsFragment()
    }
}

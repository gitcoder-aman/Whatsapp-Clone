package com.tech.whatsappclone

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tech.whatsappclone.Adapters.FragmentsAdapter
import com.tech.whatsappclone.databinding.ActivityMainBinding

val tabArray = arrayOf(
    "Chats",
    "Status",
    "Calls"
)
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        val pagerAdapter = FragmentsAdapter(supportFragmentManager,lifecycle)
        viewPager2 = binding.viewPager2

        tabLayout = binding.tabs
        tabLayout.addTab(tabLayout.newTab().setText("Chats"))
        tabLayout.addTab(tabLayout.newTab().setText("Status"))
        tabLayout.addTab(tabLayout.newTab().setText("Calls"))

        viewPager2.adapter = pagerAdapter

        TabLayoutMediator(tabLayout , viewPager2 , true) { tab , index ->
            tab.text = tabArray[index]
        }.attach()

        //you can also this type code implement for tabLayout set to viewPager2

//        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener{
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                if (tab != null) {
//                    viewPager2.currentItem = tab.position
//                }
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//
//            }
//
//        })
//        viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback(){
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                tabLayout.selectTab(tabLayout.getTabAt(position))
//            }
//        })







    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.setting->{
                startActivity(Intent(this,SettingActivity::class.java))
            }
            R.id.logout->{
                auth.signOut()
                startActivity(Intent(this,SignInActivity::class.java))
            }
            R.id.groupChat->{
                startActivity(Intent(this,GroupChatActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
}
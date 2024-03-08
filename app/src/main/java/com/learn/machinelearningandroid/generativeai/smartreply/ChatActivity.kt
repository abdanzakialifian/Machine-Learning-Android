package com.learn.machinelearningandroid.generativeai.smartreply

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.learn.machinelearningandroid.R
import com.learn.machinelearningandroid.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentManager = supportFragmentManager
        val chatFragment = ChatFragment()
        val fragment = fragmentManager.findFragmentByTag(ChatFragment::class.java.simpleName)

        if (fragment !is ChatFragment) {
            fragmentManager
                .beginTransaction()
                .add(R.id.container, chatFragment, ChatFragment::class.java.simpleName)
                .commit()
        }

    }
}
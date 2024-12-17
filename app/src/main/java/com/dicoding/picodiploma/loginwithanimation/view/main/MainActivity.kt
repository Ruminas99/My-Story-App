package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.view.detail.DetailActivity
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import com.dicoding.picodiploma.loginwithanimation.Result
import com.dicoding.picodiploma.loginwithanimation.view.add.AddActivity
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory.getInstance(this))[MainViewModel::class.java]
    }
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        binding.addButton.startAnimation(fadeIn)
        binding.addButton.setOnClickListener {
            it.startAnimation(fadeOut)
        }
        binding.logoutButton.startAnimation(fadeIn)
        binding.logoutButton.setOnClickListener {
            it.startAnimation(fadeOut)
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
        binding.addButton.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
            finish()
        }

        setupView()
        setupRecyclerView()
        setupAction()
        observeStories()
        observeSession()
    }

    private fun observeSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin){
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Selamat Datang Kembali, ${user.name}", Toast.LENGTH_SHORT).show()
                viewModel.getStory()
                binding.imageView.text = getString(R.string.greeting, user.name)
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
        }
    }
    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter(ArrayList()) { id ->
            navigateToDetail(id)
        }
        binding.storyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.storyRecyclerView.adapter = storyAdapter
        binding.storyRecyclerView.setHasFixedSize(true)
    }
    private fun observeStories() {
        viewModel.stories.observe(this) { stories ->
            when (stories) {
                is Result.Success -> {
                    storyAdapter.updateList(stories.data)
                }
                is Result.Error -> {
                    Toast.makeText(this, stories.message, Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToDetail(placeId: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("PLACE_ID", placeId)
        startActivity(intent)
    }
}
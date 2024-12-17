package com.dicoding.picodiploma.loginwithanimation.view.detail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.Result

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory.getInstance(this))[DetailViewModel::class.java]
    }
    private lateinit var id: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        id = intent.getStringExtra("PLACE_ID") ?: run {
            Toast.makeText(this, "Invalid place ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        detailViewModel.fetchStoryDetail(id)

        playAnimation()
        setupObserver()
    }

    private fun playAnimation() {
        val image = ObjectAnimator.ofFloat(binding.imageStory, View.ALPHA, 0f, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.textTitle, View.ALPHA, 0f, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.textDescription, View.ALPHA, 0f, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(image, title, desc)
            start()
        }
    }

    fun setupObserver() {
        detailViewModel.storyDetail.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val detail = result.data.story
                    binding.apply {
                        textTitle.text = detail?.name
                        textDescription.text = HtmlCompat.fromHtml(detail?.description ?: "", HtmlCompat.FROM_HTML_MODE_COMPACT)
                        Glide.with(this@DetailActivity)
                            .load(detail?.photoUrl)
                            .into(imageStory)


                    }
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }

            }
        }

    }
}
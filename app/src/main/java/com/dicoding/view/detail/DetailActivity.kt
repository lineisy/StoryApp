package com.dicoding.view.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.R
import com.dicoding.data.remote.response.ListStoryItem
import com.dicoding.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val storyDetail = intent.getParcelableExtra<ListStoryItem>("STORY_DATA") as ListStoryItem

        Glide.with(this)
            .load(storyDetail.photoUrl)
            .placeholder(R.drawable.ic_broken)
            .into(binding.ivDetailPhoto)
        binding.tvDetailName .text = storyDetail.name
        binding.tvDetailDescription .text = storyDetail.description
    }
}
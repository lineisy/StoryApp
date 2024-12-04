package com.dicoding.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.R
import com.dicoding.databinding.ActivityMainBinding
import com.dicoding.view.ResultStories
import com.dicoding.view.StoryAdapter
import com.dicoding.view.ViewModelFactory
import com.dicoding.view.login.LoginActivity
import com.dicoding.view.upload.UploadActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRv()
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {

                setupView()
                setupAction()
                setupData()
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

    }

    private fun setupRv() {
        storyAdapter = StoryAdapter()
        binding.recyclerView.apply {
            adapter = storyAdapter
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)

        }

    }

    private fun setupData() {
        viewModel.stories.observe(this) { resultStories ->
            when (resultStories) {
                is ResultStories.Loading -> {
                    binding.progrssBar.visibility = View.VISIBLE
                }

                is ResultStories.Success -> {
                    binding.progrssBar.visibility = View.GONE
                    val stories = resultStories.data?.listStory
                    storyAdapter.submitList(stories)
                    if (stories != null && stories.isNotEmpty()) {
                        binding.recyclerView.scrollToPosition(0)
                    }
                }

                is ResultStories.Error -> {
                    binding.progrssBar.visibility = View.GONE
                    Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.getStories()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getStories()
        binding.recyclerView.scrollToPosition(0)
    }


    private fun setupAction() {
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                viewModel.logout()
                true
            }

            R.id.upload -> {
                startActivity(Intent(this, UploadActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


}
package com.technobugsai.smartweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.technobugsai.smartweather.appview.viewmodel.AuthViewModel
import com.technobugsai.smartweather.databinding.ActivityMainBinding
import com.technobugsai.smartweather.utils.applyTheme
import com.technobugsai.smartweather.utils.isDarkTheme
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                false
//                viewModel.isLoading.value
            }
        }
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()

        setErrorListener()

        setSupportActionBar(binding.toolbar)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun setErrorListener() {
        lifecycleScope.launch {
//            viewModel.errorPublisher.collectLatest {
//                if (it != null)
//                    this@MainActivity.toast(it.message.toString())
//            }
        }
    }

    private fun initViews() {
        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_uimode -> {
                val uiMode = if (isDarkTheme()) {
                    AppCompatDelegate.MODE_NIGHT_NO
                } else {
                    AppCompatDelegate.MODE_NIGHT_YES
                }
                applyTheme(uiMode)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}
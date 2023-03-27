package com.technobugsai.smartweather

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHost
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import com.technobugsai.smartweather.appview.auth.AuthFragment
import com.technobugsai.smartweather.appview.auth.AuthFragmentDirections
import com.technobugsai.smartweather.appview.auth.SignUpFragment
import com.technobugsai.smartweather.appview.profile.UserProfileFragment
import com.technobugsai.smartweather.appview.viewmodel.AuthViewModel
import com.technobugsai.smartweather.appview.viewmodel.UserProfileViewModel
import com.technobugsai.smartweather.databinding.ActivityMainBinding
import com.technobugsai.smartweather.utils.applyTheme
import com.technobugsai.smartweather.utils.isDarkTheme
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: AuthViewModel by viewModel()
    private val userViewModel: UserProfileViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
            setOnExitAnimationListener {
                findNavController(binding.fcvMain.id).run {
                    if (viewModel.toAuth.not()
                        && (currentDestination as? FragmentNavigator.Destination)?.className == AuthFragment::class.qualifiedName) {
                        findNavController(binding.fcvMain.id).navigate(
                            AuthFragmentDirections.actionAuthFragmentToUserProfileFragment()
                        )
                    }
                }
                it.remove()
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

    private fun backPress() {

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

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val menuItem = menu?.findItem(R.id.menu_more)
        (supportFragmentManager.fragments[0] as? NavHostFragment)?.run {
            if(childFragmentManager.fragments[0] is AuthFragment){
                menuItem?.isVisible = false
            } else if (childFragmentManager.fragments[0] is SignUpFragment) {
                menuItem?.isVisible = false
            }
        }
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
            R.id.menu_more -> {
                val popupMenu = PopupMenu(this, binding.toolbar.findViewById(R.id.menu_more))
                popupMenu.inflate(R.menu.overflow_menu)
                popupMenu.setOnMenuItemClickListener { popupMenuItem ->
                    // Handle menu item clicks here
                    when (popupMenuItem.itemId) {
                        R.id.menu_log_out -> {
                            lifecycleScope.launch {
                                viewModel.clearDb()
                                val intent = Intent(this@MainActivity, MainActivity::class.java)
                                finish()
                                startActivity(intent)
                            }
                        }
                    }
                    true
                }
                popupMenu.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    fun showHideProgress(shouldShow: Boolean) {
        if (shouldShow) {
            binding.progressBar.visibility = View.VISIBLE
            binding.layer.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.layer.visibility = View.GONE
        }
    }

}
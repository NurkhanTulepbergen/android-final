package com.example.dms

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.dms.network.RetrofitClient
import com.example.dms.ui.*
import com.example.dms.utils.SessionManager
import com.example.dms.ui.FinanceDashboardFragment
import com.example.dms.ui.LiveRequestsApprovalFragment
import com.example.dms.ui.RepairRequestFragment
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuButton: ImageView
    private lateinit var toolbarTitle: TextView
    private lateinit var sessionManager: SessionManager

    private val CURRENT_FRAGMENT_KEY = "current_fragment"
    private val CURRENT_TITLE_KEY = "current_title"

    private var currentFragmentTag: String? = null
    private var currentTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        // Проверка что токен есть
        val token = sessionManager.getToken()
        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        menuButton = findViewById(R.id.menuButton)
        toolbarTitle = findViewById(R.id.fragmentTitle)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        updateNavigationMenuForRole()

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Загружаем данные пользователя через /api/me
        loadUserData()

        // Восстанавливаем фрагмент после поворота
        if (savedInstanceState != null) {
            currentFragmentTag = savedInstanceState.getString(CURRENT_FRAGMENT_KEY)
            currentTitle = savedInstanceState.getString(CURRENT_TITLE_KEY)

            val fragment = supportFragmentManager.findFragmentByTag(currentFragmentTag)
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, it, currentFragmentTag)
                    .commit()
                toolbarTitle.text = currentTitle
            }
        } else {
            openFragment(NewsFragment(), R.id.nav_home, "Новости")
        }

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home ->
                    openFragment(NewsFragment(), R.id.nav_home, "Новости")

                R.id.nav_profile ->
                    openFragment(ProfileFragment(), R.id.nav_profile, "Личная информация")

                R.id.nav_living ->
                    openFragment(ResidenceFragment(), R.id.nav_living, "Проживание")

                R.id.nav_sport ->
                    openFragment(SportsRegistrationFragment(), R.id.nav_sport, "Запись на занятие физкультурой")

                R.id.nav_docs ->
                    openFragment(MyRequestsFragment(), R.id.nav_docs, "Мои запросы")

                R.id.nav_finance ->
                    openFragment(FinanceDashboardFragment(), R.id.nav_finance, "Финансовый кабинет")

                R.id.nav_repair ->
                    openFragment(RepairRequestFragment(), R.id.nav_repair, "Запрос на ремонт")

                R.id.nav_live_requests ->
                    openFragment(LiveRequestsApprovalFragment(), R.id.nav_live_requests, "Запросы на заселение")

                R.id.nav_logout ->
                    logout()
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    /**
     * Загружаем данные пользователя через /api/me и сохраняем в SessionManager
     */
    private fun loadUserData() {
        val token = sessionManager.getToken() ?: return

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.getInstance(token)
                val user = api.getProfile() // suspend fun
                sessionManager.saveUserName(user.name)
                sessionManager.saveUserEmail(user.email)
                sessionManager.saveUserId(user.id.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show()
            }
        }
    }


    /**
     * Логаут через /api/logout
     */
    private fun logout() {
        val token = sessionManager.getToken()

        RetrofitClient.getInstance(token).logout()
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    sessionManager.clearToken()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    sessionManager.clearToken()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            })
    }

    private fun showPlaceholder(text: String, menuId: Int) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        navView.setCheckedItem(menuId)
        toolbarTitle.text = text
        currentFragmentTag = null
        currentTitle = text
    }

    private fun openFragment(fragment: Fragment, menuId: Int, title: String) {
        val tag = fragment::class.java.simpleName

        currentFragmentTag = tag
        currentTitle = title

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment, tag)
            .commit()

        navView.setCheckedItem(menuId)
        toolbarTitle.text = title
    }

    private fun setCheckedMenuItem(menuId: Int) {
        navView.menu.findItem(menuId)?.isChecked = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CURRENT_FRAGMENT_KEY, currentFragmentTag)
        outState.putString(CURRENT_TITLE_KEY, currentTitle)
    }

    private fun updateNavigationMenuForRole() {
        val role = sessionManager.getUserRole()
        val menu = navView.menu
        val isManager = role?.equals("manager", true) == true

        listOf(
            R.id.nav_living,
            R.id.nav_sport,
            R.id.nav_docs,
            R.id.nav_finance,
            R.id.nav_repair
        ).forEach { menu.findItem(it)?.isVisible = !isManager }

        menu.findItem(R.id.nav_live_requests)?.isVisible = isManager
    }

    private fun isManager(): Boolean =
        sessionManager.getUserRole()?.equals("manager", true) == true
}

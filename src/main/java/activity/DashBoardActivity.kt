package activity

import adaptor.Menurecycleradapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.toolbox.Volley
import com.arpit.foodproject.R
import com.google.android.material.navigation.NavigationView
import fragment.*
import fragment.FragmentMenu.Companion.resId


class DashBoardActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView

    private lateinit var sharedPrefs: SharedPreferences

    var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        sharedPrefs=getSharedPreferences(getString(R.string.pref_name) , Context.MODE_PRIVATE)
        drawerLayout = findViewById(R.id.drawerlayout)
        coordinatorLayout = findViewById(R.id.coordinatorlayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.framelayout)
        navigationView = findViewById(R.id.navigationview)

        openHome()
        setupToolbar()

        val actionBarDrawerToggle = ActionBarDrawerToggle(this@DashBoardActivity, drawerLayout,
                R.string.open_drawer,
                R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()


        /*Below we handle the click listeners of the menu items inside the navigation drawer*/
        navigationView.setNavigationItemSelectedListener {


            /*Unchecking the previous menu item when a new item is clicked*/
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            /*Highlighting the new menu item, the one which is clicked*/
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it


            /*Getting the id of the clicked item to identify which fragment to display*/
            when (it.itemId) {

                /*Opening the home fragment*/
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.framelayout, HomeFragment())
                            .commit()
                    supportActionBar?.title = "Home"
                    drawerLayout.closeDrawers()
                }

                R.id.profile -> {

                    supportFragmentManager.beginTransaction()
                            .replace(R.id.framelayout, ProfileFragment())
                            .commit()
                    supportActionBar?.title = "Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.favourite -> {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.framelayout, FavouriteFragment())
                            .commit()
                    supportActionBar?.title = "Favourite"
                    drawerLayout.closeDrawers()
                }

                R.id.faq -> {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.framelayout, FaqFragment())
                            .commit()
                    supportActionBar?.title = "FAQ"
                    drawerLayout.closeDrawers()
                }

                R.id.logout -> {
                    /*Creating a confirmation dialog*/
                    val builder = AlertDialog.Builder(this@DashBoardActivity)
                    builder.setTitle("Confirmation")
                            .setMessage("Are you sure you want exit?")
                            .setPositiveButton("Yes") { _, _ ->

                                sharedPrefs.edit().clear().apply()
                                startActivity(Intent(this@DashBoardActivity, LoginActivity::class.java))
                                Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
                                ActivityCompat.finishAffinity(this)
                            }
                            .setNegativeButton("No") { _, _ ->
                                openHome()
                            }
                            .create()
                            .show()

                }
            }
            return@setNavigationItemSelectedListener true
        }


    }


    fun openHome() {
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.framelayout, fragment)
        transaction.commit()
        supportActionBar?.title = "Home"
        navigationView.setCheckedItem(R.id.home)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "All Restaurants"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val f = supportFragmentManager.findFragmentById(R.id.framelayout)
        when (f) {
            is HomeFragment -> {
                Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
                super.onBackPressed()
            }

            is FragmentMenu -> {
                if (!Menurecycleradapter.isCartEmpty) {
                    val builder = AlertDialog.Builder(this@DashBoardActivity)
                    builder.setTitle("Confirmation")
                            .setMessage("Going back will reset cart items. Do you still want to proceed?")
                            .setPositiveButton("Yes") { _, _ ->
                              val clearCart= CartActivity.ClearDBAsync(applicationContext, resId.toString()).execute().get()
                                openHome()

                                Menurecycleradapter.isCartEmpty = true
                            }
                            .setNegativeButton("No") { _, _ ->

                            }
                            .create()
                            .show()
                } else {
                    openHome()
                }
            }
            else -> openHome()


        }



    }

}




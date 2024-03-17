package com.example.cineflix.View.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.example.cineflix.R
import com.example.cineflix.View.Fragments.AccountFragment
import com.example.cineflix.View.Fragments.BookmarkFragment
import com.example.cineflix.View.Fragments.HomeFragment
import com.example.cineflix.View.Fragments.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var homeFragment: HomeFragment
    private lateinit var searchFragment: SearchFragment
    private lateinit var bookmarkFragment: BookmarkFragment
    private lateinit var accountFragment: AccountFragment
    private lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(R.layout.activity_main)

        homeFragment = HomeFragment()
        searchFragment = SearchFragment()
        bookmarkFragment = BookmarkFragment()
        accountFragment = AccountFragment()
        activeFragment = homeFragment

        supportFragmentManager.beginTransaction().apply {
            add(R.id.frameLayout, homeFragment, "home")
            add(R.id.frameLayout, searchFragment, "search").hide(searchFragment)
            add(R.id.frameLayout, bookmarkFragment, "bookmark").hide(bookmarkFragment)
            add(R.id.frameLayout, accountFragment, "account").hide(accountFragment)
            commit()
        }

        val bottomNav : BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navHome -> {
                    showFragment(homeFragment)
                    true
                }

                R.id.navSearch -> {
                    showFragment(searchFragment)
                    true
                }

                R.id.navFavorite -> {
                    showFragment(bookmarkFragment)
                    true
                }

                R.id.navAccount -> {
                    showFragment(accountFragment)
                    true
                }

                else -> false
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().hide(activeFragment).show(fragment).commit()
        activeFragment = fragment
    }
}
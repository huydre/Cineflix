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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(R.layout.activity_main)

        val bottomNav : BottomNavigationView = findViewById(R.id.bottom_nav)
        replaceFragment(HomeFragment())

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navHome -> {
                    // Xử lý khi người dùng chọn mục "Home"
                    replaceFragment(HomeFragment())
                    true
                }

                R.id.navSearch -> {
                    // Xử lý khi người dùng chọn mục "Search"
                    replaceFragment(SearchFragment())
                    true
                }

                R.id.navFavorite -> {
                    replaceFragment(BookmarkFragment())
                    // Xử lý khi người dùng chọn mục "Favorite"
                    true
                }

                R.id.navAccount -> {
                    replaceFragment(AccountFragment())
                    // Xử lý khi người dùng chọn mục "Account"
                    true
                }

                else -> false
            }
        }
    }
    private fun replaceFragment(fragment: Fragment) {

        val fragmentManage = supportFragmentManager
        val fragmentTransaction = fragmentManage.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}
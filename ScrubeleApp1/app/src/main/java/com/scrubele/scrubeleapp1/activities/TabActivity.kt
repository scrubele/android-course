package com.scrubele.scrubeleapp1.activities

import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.scrubele.scrubeleapp1.R
import com.scrubele.scrubeleapp1.adapters.TabsAdapter
import com.scrubele.scrubeleapp1.fragments.DataListFragment
import com.scrubele.scrubeleapp1.fragments.ProfileFragment
import com.scrubele.scrubeleapp1.fragments.Tab2Fragment
import kotlinx.android.synthetic.main.activity_tabs.*
import kotlinx.android.synthetic.main.app_bar.*

class TabActivity : AppCompatActivity() {
    private val layoutResId: Int
        @LayoutRes
        get() = R.layout.activity_list

    lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabs)
        initViews()
        setStatePageAdapter()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
                val fm = supportFragmentManager
                val ft = fm.beginTransaction()
                val count = fm.backStackEntryCount
                if (count >= 1) {
                    supportFragmentManager.popBackStack()
                }
                ft.commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        toolbar.setNavigationOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewpager_main)
        tabLayout = findViewById(R.id.tabs_main)
    }

    private fun setStatePageAdapter() {
        val fragmentAdapter = TabsAdapter(supportFragmentManager)
        fragmentAdapter.addFragment(DataListFragment(), getString(R.string.list))
        fragmentAdapter.addFragment(Tab2Fragment(), getString(R.string.tab2))
        fragmentAdapter.addFragment(ProfileFragment(), getString(R.string.profile))
        viewpager_main.adapter = fragmentAdapter
        tabs_main.setupWithViewPager(viewpager_main)
    }
}
package com.scrubele.scrubeleapp1.activities

import android.content.Intent
import android.os.Bundle
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

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabs)
        initViews()
        setStatePageAdapter()
        initSupportActionBar()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                setTabLayout(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
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

    private fun initSupportActionBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            launchMainActivity()
        }
    }

    private fun setTabLayout(tab: TabLayout.Tab){
        viewPager.currentItem = tab.position
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        val count = fm.backStackEntryCount
        if (count >= 1) {
            supportFragmentManager.popBackStack()
        }
        ft.commit()
    }

    private fun launchMainActivity() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY and Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}
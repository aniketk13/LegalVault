package com.teamdefine.legalvault.main.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teamdefine.legalvault.main.home.generate.GenerateNewDocument
import com.teamdefine.legalvault.main.home.mydocs.MyDocuments

class HomePageViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycleOwner: LifecycleOwner
) : FragmentStateAdapter(fragmentManager, lifecycleOwner.lifecycle) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> GenerateNewDocument()
            1 -> MyDocuments()
            else -> {
                throw android.content.res.Resources.NotFoundException("Position Not Found")
            }
        }
    }
}
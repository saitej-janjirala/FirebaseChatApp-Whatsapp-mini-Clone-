package com.saitejajanjirala.mychatapp.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

data class ViewPagerAdapter(val context:Context,val fragmentManager: FragmentManager):FragmentPagerAdapter(fragmentManager) {

    val fragments:ArrayList<Fragment>
    val titles:ArrayList<String>
    init{
        fragments= ArrayList()
        titles= ArrayList()
    }
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
    fun addfragment(fragment:Fragment,title:String){
        fragments.add(fragment)
        titles.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }


}
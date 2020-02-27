package com.appleto.loppyuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

open class PagerAdapter() : PagerAdapter() {

//    setContentView(R.layout.activity_intro_slider)

    lateinit var layouts: IntArray

    lateinit var inflater: LayoutInflater

    lateinit var con:Context

    constructor(layout: IntArray,con:Context) : this() {
        this.layouts = layout
        this.con  = con
        inflater = con.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun getCount(): Int {
        return layouts.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var view: View = inflater.inflate(layouts[position],container,false)
        container!!.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        var view:View = `object` as View
        container!!.removeView(view)
    }
}
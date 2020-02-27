package com.appleto.loppyuser.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.appleto.loppyuser.R
import com.appleto.loppyuser.helper.DepthPageTransformer
import kotlinx.android.synthetic.main.activity_intro_slider.*

class IntroSliderActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v) {
            btnNext -> {
                // checking for last page
                // if last page home screen will be launched
                val current = getItem(+1)
                if (current == layouts!!.size) {
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                } else {
                    if (current < layouts!!.size) {
                        // move to next screen
                        viewPager!!.currentItem = current
                    }
                }
            }
            btnSkip -> {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    private var myViewPagerAdapter: MyViewPagerAdapter? = null
    private var layouts: IntArray? = null


    //  viewpager change listener
    internal var viewPagerPageChangeListener: ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {

                getCurrentItem(position)

                // changing the next button text 'NEXT' / 'GOT IT'
                if (position == layouts!!.size - 1) {
                    // last page. make button text to GOT IT
                    btnNext!!.layoutParams =
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    btnNext!!.background = null
                    btnNext!!.setBackgroundColor(resources.getColor(R.color.transparent))
                    btnSkip!!.visibility = View.GONE
                } else {
                    // still pages are left
                    btnNext!!.layoutParams =
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    btnNext!!.background = null
                    btnNext!!.setBackgroundColor(resources.getColor(R.color.transparent))
                    btnSkip!!.visibility = View.VISIBLE
                }
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {

            }

            override fun onPageScrollStateChanged(arg0: Int) {

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_slider)

        // add few more layouts if you want
        layouts = intArrayOf(
            R.layout.welcome_slide_one,
            R.layout.welcome_slide_two,
            R.layout.welcome_slide_three
        )


        myViewPagerAdapter = MyViewPagerAdapter()
        viewPager!!.adapter = myViewPagerAdapter
        viewPager!!.addOnPageChangeListener(viewPagerPageChangeListener)
        viewPager?.setPageTransformer(true, DepthPageTransformer())

        getCurrentItem(0)

        btnNext.setOnClickListener(this)
        btnSkip.setOnClickListener(this)
    }

    private fun getCurrentItem(current: Int) {
        val currentPage: Int = current + 1

        tvTotal.text = layouts?.size.toString()

        tvCurrent.text = currentPage.toString()
    }

    private fun getItem(i: Int): Int {
        return viewPager.currentItem + i
    }

    /**
     * View pager adapter
     */
    inner class MyViewPagerAdapter : PagerAdapter() {
        private var layoutInflater: LayoutInflater? = null

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            assert(layoutInflater != null)
            val view = layoutInflater!!.inflate(layouts!![position], container, false)
            container.addView(view)

            return view
        }

        override fun getCount(): Int {
            return layouts!!.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }


        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }

}
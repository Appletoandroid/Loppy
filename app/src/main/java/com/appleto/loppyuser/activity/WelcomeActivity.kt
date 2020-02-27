package com.appleto.loppyuser.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.appleto.loppyuser.R
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(p0: View?) {
        when (p0) {
            btnRegisterNow -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            btnLoginNow -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        btnRegisterNow.setOnClickListener(this)
        btnLoginNow.setOnClickListener(this)
    }
}

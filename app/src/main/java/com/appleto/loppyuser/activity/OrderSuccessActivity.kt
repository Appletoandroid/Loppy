package com.appleto.loppyuser.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.appleto.loppyuser.R
import kotlinx.android.synthetic.main.activity_success.*

class OrderSuccessActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(p0: View?) {
        when (p0) {
            btnOkay -> {
                startActivity(
                    Intent(this, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_success)

        setSupportActionBar(toolbar)

        btnOkay.setOnClickListener(this)
    }
}

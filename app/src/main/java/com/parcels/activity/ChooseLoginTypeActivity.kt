package com.parcels.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.parcels.R

class ChooseLoginTypeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_login_type)

        showSnack()
    }


    override fun onResponseHandler(response: String, requestCode: Int) {

    }

    override fun onErrorHandler(err: Throwable) {

    }
}

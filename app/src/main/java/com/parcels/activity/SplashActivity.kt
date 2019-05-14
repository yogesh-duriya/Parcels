package com.parcels.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.view.View
import com.parcels.R
import com.parcels.util.AppConstant
import com.parcels.util.ConnectionDetector
import com.parcels.util.network.AppApiHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_splash.*
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        setupProgressBar(progressbar)

        getCompanyLogo()

    }

    fun getCompanyLogo() {
        var companyLogoParam: JSONObject
        var params: HashMap<String, String>? = null
        companyLogoParam = JSONObject()
        try {
            companyLogoParam.put("MethodName", "GetCompanyLogo")
            params = HashMap<String, String>()
            params.put("Request", companyLogoParam.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val status = ConnectionDetector.isConnected(this)

        if (status) {
            showProgress()
            var compositeDisposable: CompositeDisposable = CompositeDisposable()
            var apiHelper: AppApiHelper = AppApiHelper()
            compositeDisposable.add(apiHelper.getCompanyLogo(companyLogoParam.toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { loginResponse ->
                        hideProgress()
                        if (loginResponse.get(0).status.equals("0")) {
                            saveIntoPrefs(AppConstant.LOGO, /*resources.getString(R.string.base_url_pay)+*/loginResponse.get(0).companyLogo!!)
                            setImagedata(/*resources.getString(R.string.base_url_pay) + */loginResponse.get(0).companyLogo!!, iv_logo)
                            //iv_logo.loadImage(loginResponse.get(0).companyLogo!!)
                            navigateToHome()
                        } else {
                            showToast(loginResponse.get(0).errorDescription!!)
                        }
                    }, { err ->
                        hideProgress()
                        println(err)
                    })
            )
        } else {
            var snackbar: Snackbar = Snackbar.make(cl_main, getString(R.string.internet_failure), Snackbar.LENGTH_LONG)
                .setAction("Retry", View.OnClickListener { getCompanyLogo() })

            snackbar.show()
        }
    }


    fun navigateToHome() {
        Handler().postDelayed({
            // only for first milestone after comment are activate
            val isFirstTime = getBooleanFromPrefs(AppConstant.IS_FIRST_TIME_LAUNCH)
            var splashIntent: Intent

            if (isFirstTime) {
                if (!getFromPrefs(AppConstant.USER_ID).equals("") || !getFromPrefs(AppConstant.USER_ID_PAY).equals("")) {
                    splashIntent = Intent(this, MainActivity::class.java)
                } else {
                    splashIntent = Intent(this, ChooseLoginTypeActivity::class.java)
                }
            } else {
                saveBooleanIntoPrefs(AppConstant.IS_FIRST_TIME_LAUNCH, true)
                splashIntent = Intent(this, WelcomeActivity::class.java)
            }
            //splashIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            //splashIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            //splashIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(splashIntent)
            finish()

        }, 3000)
    }

    override fun onResponseHandler(response: String, requestCode: Int) {

    }

    override fun onErrorHandler(err: Throwable) {
    }

}

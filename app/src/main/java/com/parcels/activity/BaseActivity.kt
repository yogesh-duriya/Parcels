package com.parcels.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.parcels.R
import com.parcels.util.AppConstant
import com.parcels.util.network.ApiClient
import com.parcels.util.network.RequestInterface
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Created by yogesh.duriya on 11/24/2017.
 */
abstract class BaseActivity : AppCompatActivity() {

    private var sharedPreferences: SharedPreferences? = null
    lateinit var progressBar : ProgressBar
    val requestInterface = ApiClient.getClient().create(RequestInterface::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    }
    fun getAndroidId() : String {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun passCombination(pass: String): Boolean {
        val expression = "(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{6,}\$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(pass)
        return matcher.matches()
    }

    fun hideSoftKeyboard() {
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (currentFocus != null)
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.SHOW_FORCED)
    }


    fun appLogout() {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
            builder.setMessage(resources.getString(R.string.applogout))
        builder.setPositiveButton("Yes" , { _, _ ->
            val prefs = getSharedPreferences(AppConstant.PREF_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.clear()
            editor.commit()
            val intent = Intent(this, ChooseLoginTypeActivity::class.java)
            startActivity(intent)
            (0 until AppConstant.ACTIVITIES.size)
                .filter { AppConstant.ACTIVITIES[it] != null }
                .forEach { AppConstant.ACTIVITIES[it].finish() }
            saveBooleanIntoPrefs(AppConstant.IS_FIRST_TIME_LAUNCH, true)
            finish()
        })
        builder.setNegativeButton("No", { dialog, _ ->
            dialog.cancel()
        })
        val alert = builder.create()
        alert.show()
    }

    fun setDashboardHeader(header: String){
        val tv_header = findViewById(R.id.header) as TextView
        tv_header.visibility = View.VISIBLE
    }

    fun setHeader(header: String) {
        val tv_header = findViewById(R.id.header) as TextView
        tv_header.text = header

        val back = findViewById(R.id.back) as ImageView
        showHide(back)
        back.setOnClickListener {
            hideSoftKeyboard()
            this@BaseActivity.onBackPressed()
        }

    }

    fun showHide(view: View) {
        view.visibility = if (view.visibility == View.VISIBLE){
            View.GONE
        } else{
            View.VISIBLE
        }
    }

    /*fun saveUserData(filterData: LoginPojo) {
        val sharedPreferences = getSharedPreferences("Teacher", Activity.MODE_PRIVATE)
        val prefsEditor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(filterData)
        prefsEditor.putString("user_details", json)
        prefsEditor.commit()
    }

    fun getUserData(): LoginPojo? {
        val sharedPreferences = getSharedPreferences("Teacher", Activity.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("user_details", "")
        return gson.fromJson<LoginPojo>(json, LoginPojo::class.java!!)
    }*/

    fun saveIntoPrefs(key: String, value: String) {
        val prefs = getSharedPreferences(AppConstant.PREF_NAME, Context.MODE_PRIVATE)
        val edit = prefs.edit()
        edit.putString(key, value)
        edit.commit()
    }

    fun saveIntIntoPrefs(key: String, value: Int) {
        val prefs = getSharedPreferences(AppConstant.PREF_NAME, Context.MODE_PRIVATE)
        val edit = prefs.edit()
        edit.putInt(key, value)
        edit.commit()
    }

    fun saveDoubleIntoPrefs(key: String, value: Double) {
        val prefs = getSharedPreferences(AppConstant.PREF_NAME, Context.MODE_PRIVATE)
        val edit = prefs.edit()
        edit.putLong(key, java.lang.Double.doubleToRawLongBits(value))
        edit.commit()
    }

    fun getFromPrefs(key: String): String {
        val prefs = getSharedPreferences(AppConstant.PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(key, AppConstant.DEFAULT_VALUE)
    }

    fun getIntFromPrefs(key: String): Int {
        val prefs = getSharedPreferences(AppConstant.PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(key, 0)
    }

    fun getDoubleFromPrefs(key: String): Double {
        val prefs = getSharedPreferences(AppConstant.PREF_NAME, Context.MODE_PRIVATE)
        return java.lang.Double.longBitsToDouble(prefs.getLong(key, java.lang.Double.doubleToLongBits(0.0)))
    }


    fun saveBooleanIntoPrefs(key: String, value: Boolean) {
        val prefs = getSharedPreferences(AppConstant.PREF_NAME, Context.MODE_PRIVATE)
        val edit = prefs.edit()
        edit.putBoolean(key, value)
        edit.commit()

    }

    fun getBooleanFromPrefs(key: String): Boolean {
        val prefs = getSharedPreferences(AppConstant.PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(key, false)
    }

    /**
     * Just a check to see if we have marshmallows (version 23)
     *
     * @return
     */
    fun canMakeSmores(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
    }

    /**
     * a method that will centralize the showing of a snackbar
     */
    fun makePostRequestSnack(message: String, size: Int) {

        Toast.makeText(applicationContext, size.toString() + " " + message, Toast.LENGTH_SHORT).show()

        finish()
    }

    fun makeSimpleMsg(message: String) {

        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

        finish()
    }

    fun hasPermission(permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (canMakeSmores()) {
                return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
            }
        }
        return true
    }

    /**
     * method to determine whether we have asked
     * for this permission before.. if we have, we do not want to ask again.
     * They either rejected us or later removed the permission.
     *
     * @param permission
     * @return
     */
    fun shouldWeAsk(permission: String): Boolean {
        return sharedPreferences!!.getBoolean(permission, true)
    }

    /**
     * we will save that we have already asked the user
     *
     * @param permission
     */
    fun markAsAsked(permission: String, sharedPreferences: SharedPreferences) {
        sharedPreferences.edit().putBoolean(permission, false).apply()
    }

    /**
     * We may want to ask the user again at their request.. Let's clear the
     * marked as seen preference for that permission.
     *
     * @param permission
     */
    fun clearMarkAsAsked(permission: String) {
        sharedPreferences?.edit()?.putBoolean(permission, true)?.apply()
    }

    fun findUnAskedPermissions(wanted: ArrayList<String>): ArrayList<String> {
        val result = ArrayList<String>()

        for (perm in wanted) {
            if (!hasPermission(perm)) {
                result.add(perm)
            }
        }

        return result
    }

    fun findRejectedPermissions(wanted: ArrayList<String>): ArrayList<String> {
        val result = ArrayList<String>()

        for (perm in wanted) {
            if (!hasPermission(perm) && !shouldWeAsk(perm)) {
                result.add(perm)
            }
        }

        return result
    }

    fun setProfileImageInLayout(ctx: Context, url: String?, image: ImageView) {
        if (url != null && url != "")
            Picasso.get().load(url).placeholder(R.mipmap.user_icon_default).error(R.mipmap.ic_launcher).into(image)
    }

    fun setImagedata(url: String?, image: ImageView) {
        if (url != null && url != "")
            Picasso.get().load(url).into(image)
    }

    fun getDateFormat(): SimpleDateFormat {
        val myFormat = "yyy/MM/dd" //In which you need put here
        return SimpleDateFormat(myFormat, Locale.US)
    }

    fun changeDateFromat(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormat = SimpleDateFormat("dd MMMM")
        val inputDateStr = date
        val date = inputFormat.parse(inputDateStr)
        val outputDateStr = outputFormat.format(date)
        return outputDateStr
    }

    fun changeDateTimeFromat(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val outputFormat = SimpleDateFormat("dd MMMM, hh:mm a")
        val date = inputFormat.parse(date)
        val outputDateStr = outputFormat.format(date)
        return outputDateStr
    }

    fun getCurrentDate(): String {
        val now = Calendar.getInstance()
        val day = now.get(Calendar.MONTH) + 1
        return day.toString()
    }

    fun getChangedDate(date: String): String {
        var result = ""
        val sds = date.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val day = sds[0]
        val month = sds[1]
        val year = sds[2]
        if (month == "01") {
            result = year+" Jan" +  " " + day
        } else if (month == "02") {
            result = year+" Feb" + " " +day
        } else if (month == "03") {
            result = year+" March" + " " +day
        } else if (month == "04") {
            result = year+" April" + " " +day
        } else if (month == "05") {
            result = year+" May" + " " +day
        } else if (month == "06") {
            result = year+" June" + " " +day
        } else if (month == "07") {
            result = year+" July" + " " +day
        } else if (month == "08") {
            result = year+" August" + " " +day
        } else if (month == "09") {
            result = year+" Sep" + " " +day
        } else if (month == "10") {
            result = year+" Oct" + " " +day
        } else if (month == "11") {
            result = year+" Nov" + " " +day
        } else if (month == "12") {
            result = year+" Dec" + " " +day
        } else {
            result = ""
        }
        return result
    }

    fun removeActivity(activity: String) {
        for (i in AppConstant.ACTIVITIES.size - 1 downTo 0) {
            if (AppConstant.ACTIVITIES[i] != null && AppConstant.ACTIVITIES[i].toString().contains(activity)) {
                AppConstant.ACTIVITIES[i].finish()
                AppConstant.ACTIVITIES.removeAt(i)
//                break
            }
        }
    }

    fun showToast(message : String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    fun showSnack(){
        Snackbar.make(findViewById(android.R.id.content), R.string.next, Snackbar.LENGTH_LONG).show()
        //Snackbar.make(activity!!.findViewById(android.R.id.content), R.string.next, Snackbar.LENGTH_LONG).show()
    }

    fun setupProgressBar(pb: ProgressBar){
        progressBar = pb
    }

    fun hideProgress() {
        progressBar.visibility = View.GONE
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun showProgress() {
        hideProgress()
        progressBar.visibility = View.VISIBLE
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun execute(params : String, requestCode : Int){
        Log.d("Request:", params)
        var compositeDisposable: CompositeDisposable = CompositeDisposable()
        compositeDisposable.add(requestInterface.performServerRequest(params)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { response ->
                    hideProgress()
                    var res = response.string()
                    onResponseHandler(res, requestCode)
                    Log.d("Request Response :", response.string())
                }, { err ->
                    hideProgress()
                    println(err)
                    onErrorHandler(err)
                })
        )
    }

    abstract fun onResponseHandler(response: String, requestCode: Int)

    abstract fun onErrorHandler(err: Throwable)
}
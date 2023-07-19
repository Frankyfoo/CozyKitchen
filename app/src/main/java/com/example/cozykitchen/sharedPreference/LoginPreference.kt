package com.example.cozykitchen.sharedPreference

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.example.cozykitchen.model.User
import com.example.cozykitchen.ui.LoginActivity
import com.google.gson.Gson

class LoginPreference {
    lateinit var pref:SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var context: Context

    companion object {
        val PREF_NAME = "My_Preferences"
        val IS_LOGIN = "isLoggedIn"
        val KEY_USERID = "userId"
        val KEY_EMAIL = "userEmail"
        val KEY_PHONE_NUMBER = "userPhoneNumber"
        val KEY_NAME = "userName"
        val KEY_SHOP_ID = "shopId" // New key for shop ID
    }

    constructor(context: Context) {
        this.context = context
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = pref.edit()
    }

    fun createLoginSession(userId: String, userEmail: String, userName: String, userPhoneNumber: String) {
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(KEY_USERID, userId)
        editor.putString(KEY_EMAIL, userEmail)
        editor.putString(KEY_NAME, userName)
        editor.putString(KEY_PHONE_NUMBER, userPhoneNumber)
        editor.apply()
    }

    private fun isLoggedIn(): Boolean {
        return pref.getBoolean(IS_LOGIN, false)
    }

    fun checkLogin() {
        if (!this.isLoggedIn()) {
            var i = Intent(context, LoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(i)
        }
    }

    fun getUserDetails(): HashMap<String, String> {
        var user: Map<String, String> = HashMap()
        (user as HashMap)[KEY_USERID] = pref.getString(KEY_USERID, null)?: ""
        (user as HashMap)[KEY_EMAIL] = pref.getString(KEY_EMAIL, null)?: ""
        (user as HashMap)[KEY_PHONE_NUMBER] = pref.getString(KEY_PHONE_NUMBER, null)?: ""
        (user as HashMap)[KEY_NAME] = pref.getString(KEY_NAME, null)?: ""

        return user
    }

    fun getCurrentUserId(): String {
        return pref.getString(KEY_USERID, null) ?: ""
    }

    // function to create a shop ID session
    fun createShopIdSession(shopId: String) {
        editor.putString(KEY_SHOP_ID, shopId)
        editor.apply()
    }

    // function to get the shop ID
    fun getShopId(): String {
        return pref.getString(KEY_SHOP_ID, null) ?: ""
    }

    fun LogoutUser() {
        editor.clear()
        editor.commit()
        var i = Intent(context, LoginActivity::class.java)
        context.startActivity(i)
    }
}
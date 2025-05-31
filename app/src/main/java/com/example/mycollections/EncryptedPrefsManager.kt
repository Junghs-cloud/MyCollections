package com.example.mycollections

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object EncryptedPrefsManager {

    private const val PREF_NAME = "MyCollections"
    private var encryptedPrefs: SharedPreferences? = null

    private fun getMasterKey(context: Context): MasterKey {
        return MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private fun init(context: Context) {
        if (encryptedPrefs == null) {
            val masterKey = getMasterKey(context.applicationContext)
            encryptedPrefs = EncryptedSharedPreferences.create(
                context,
                PREF_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    fun getEncryptedPrefs(context: Context): SharedPreferences {
        if (encryptedPrefs == null) {
            init(context)
        }
        return encryptedPrefs!!
    }

    fun saveString(context: Context, key: String, value: String) {
        val prefs = getEncryptedPrefs(context)
        prefs.edit().putString(key, value).apply()
    }

    fun getString(context: Context, key: String, defaultValue: String? = null): String? {
        val prefs = getEncryptedPrefs(context)
        return prefs.getString(key, defaultValue)
    }

    fun saveBoolean(context: Context, key: String, value: Boolean) {
        val prefs = getEncryptedPrefs(context)
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(context: Context, key: String, defaultValue: Boolean = false): Boolean {
        val prefs = getEncryptedPrefs(context)
        return prefs.getBoolean(key, defaultValue)
    }
}
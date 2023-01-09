package com.example.currencyexchangeapp.utils

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedSharedPreferences(val context: Context) {

    val allPreferences: MutableMap<String, *>
        get() = sharedPreferences.all

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "exchange_preferences",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun remove(preference: String) {
        sharedPreferences.edit(true) {
            remove(preference)
        }
    }

    fun addPreference(preference: String, value: String) {
        sharedPreferences.edit(true) {
            putString(preference, value)
        }
    }

    fun addFloatPreference(preference: String, value: Float) {
        sharedPreferences.edit(true) {
            putFloat(preference, value)
        }
    }

    fun getPreference(preference: String) = sharedPreferences.getString(preference, "")

    fun getFloatPreference(preference: String) = sharedPreferences.getFloat(preference, 4f)

}

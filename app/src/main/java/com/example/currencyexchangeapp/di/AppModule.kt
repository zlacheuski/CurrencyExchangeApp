package com.example.currencyexchangeapp.di

import android.content.Context
import androidx.room.Room
import androidx.viewbinding.BuildConfig
import com.example.currencyexchangeapp.db.CurrencyDatabase
import com.example.currencyexchangeapp.db.dao.IRateDAO
import com.example.currencyexchangeapp.domain.RateExchangeAPI
import com.example.currencyexchangeapp.utils.Constants.BASE_URL
import com.example.currencyexchangeapp.utils.EncryptedSharedPreferences
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    @Singleton
    @Provides
    fun provideToHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(interceptor)
        }
        return builder.build()
    }

    @Singleton
    @Provides
    fun provideApi(): RateExchangeAPI = Retrofit.Builder().baseUrl(BASE_URL)
        .client(provideToHttpClient(provideLoggingInterceptor()))
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory()).build()
        .create(RateExchangeAPI::class.java)

    @Singleton
    @Provides
    fun provideCurrencyDB(@ApplicationContext appContext: Context): IRateDAO {
        return Room
            .databaseBuilder(appContext, CurrencyDatabase::class.java, CurrencyDatabase.DB_NAME)
            .fallbackToDestructiveMigration().build().dao()
    }

    @Singleton
    @Provides
    fun provideSharedPref(@ApplicationContext appContext: Context): EncryptedSharedPreferences {
        return EncryptedSharedPreferences(appContext)
    }
}

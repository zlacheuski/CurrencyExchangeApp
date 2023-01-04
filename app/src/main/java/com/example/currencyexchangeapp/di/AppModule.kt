package com.example.currencyexchangeapp.di

import android.content.Context
import androidx.room.Room
import androidx.viewbinding.BuildConfig
import com.example.currencyexchangeapp.db.CurrencyDatabase
import com.example.currencyexchangeapp.db.dao.ICurrencyDAO
import com.example.currencyexchangeapp.domain.CurrencyExchangeAPI
import com.example.currencyexchangeapp.utils.ApiConstants.BASE_URL
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
    fun provideApi(): CurrencyExchangeAPI = Retrofit.Builder().baseUrl(BASE_URL)
        .client(provideToHttpClient(provideLoggingInterceptor()))
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory()).build()
        .create(CurrencyExchangeAPI::class.java)

    @Singleton
    @Provides
    fun provideCurrencyDB(@ApplicationContext appContext: Context): ICurrencyDAO {
        return Room
            .databaseBuilder(appContext, CurrencyDatabase::class.java, CurrencyDatabase.DB_NAME)
            .fallbackToDestructiveMigration().build().vocDao()
    }
}

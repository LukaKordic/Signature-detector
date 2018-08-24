package com.lukakordic.signaturedetector.di.modules

import android.content.Context
import com.lukakordic.signaturedetector.App
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Provides
    fun provideApplicationContext(application: App): Context = application.applicationContext
}

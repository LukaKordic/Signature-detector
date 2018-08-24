package com.lukakordic.signaturedetector.di.components

import com.lukakordic.signaturedetector.App
import com.lukakordic.signaturedetector.di.modules.AppModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, AppModule::class])
interface AppComponent : AndroidInjector<App>{

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()
}

package com.vatsal.kesarwani.login.di.module

import com.vatsal.kesarwani.login.ui.AuthActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector(modules = [FragmentBindingModule::class])
    abstract fun injectAuthActivity(): AuthActivity

}
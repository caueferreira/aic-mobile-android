package edu.artic

import android.content.Context
import dagger.Module
import dagger.Provides
import edu.artic.ui.BuildConfig
import javax.inject.Named
import javax.inject.Singleton

/**
 * Description:
 */
@Module
class AppModule {

    @Provides
    fun provideApplication(): Context = ArticApp.app

    @Provides
    @Named(VERSION)
    fun getBuildVersion(): String = BuildConfig.VERSION_NAME

    @Module
    companion object {

        const val VERSION = "VERSION"
        const val DISPLAY_CONFIG = "DISPLAY_CONFIG"

//        @JvmStatic
//        @Provides
//        @IntoSet
//        @Router
//        fun provideRouter(): ActionToScreenRouter = AppRouter()
    }
}
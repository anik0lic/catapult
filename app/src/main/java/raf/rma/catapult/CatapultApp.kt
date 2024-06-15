package raf.rma.catapult

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import raf.rma.catapult.auth.AuthStore
import javax.inject.Inject

@HiltAndroidApp
class CatapultApp : Application() {

    @Inject lateinit var authStore: AuthStore

    override fun onCreate() {
        super.onCreate()

        val authData = authStore.authData.value
        Log.d("DATASTORE", "AuthData = $authData")
    }
}
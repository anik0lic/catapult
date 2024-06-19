package raf.rma.catapult

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import dagger.hilt.android.AndroidEntryPoint
import raf.rma.catapult.analytics.AppAnalytics
import raf.rma.catapult.navigation.AppNavigation
import raf.rma.catapult.core.theme.CatapultTheme
import raf.rma.catapult.profile.datastore.ProfileDataStore
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val analytics = AppAnalytics()
    @Inject lateinit var profileDataStore: ProfileDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CompositionLocalProvider(
                LocalAnalytics provides analytics
            ) {
                CatapultTheme {
                    AppNavigation(profileDataStore)
                }
            }
        }
    }
}

val LocalAnalytics = compositionLocalOf<AppAnalytics> {
    error("Analytics not provided")
}
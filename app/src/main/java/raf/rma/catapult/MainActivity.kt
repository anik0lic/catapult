package raf.rma.catapult

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import dagger.hilt.android.AndroidEntryPoint
import raf.rma.catapult.analytics.AppAnalytics
import raf.rma.catapult.navigation.AlbumNavigation
import raf.rma.catapult.core.theme.CatapultTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val analytics = AppAnalytics()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CompositionLocalProvider(
                LocalAnalytics provides analytics
            ) {
                CatapultTheme {
                    AlbumNavigation()
                }
            }
        }
    }
}

val LocalAnalytics = compositionLocalOf<AppAnalytics> {
    error("Analytics not provided")
}
package ie.setu.Album.main

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import ie.setu.Album.models.AlbumMemStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    val albums = AlbumMemStore()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("Album started")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}

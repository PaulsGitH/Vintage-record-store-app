package ie.setu.Album.main

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import ie.setu.Album.models.AlbumMemStore
import ie.setu.Album.models.AlbumStore
import ie.setu.album.models.AlbumJSONStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    lateinit var albums: AlbumStore


    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("Album started")
        //albums = AlbumMemStore()
        albums = AlbumJSONStore(applicationContext)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}

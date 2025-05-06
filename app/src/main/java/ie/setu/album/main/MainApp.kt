package ie.setu.album.main

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import ie.setu.album.models.AlbumRoomStore
import ie.setu.album.models.AlbumStore
import ie.setu.album.models.AlbumMemStore
import ie.setu.album.models.FirebaseAlbumStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    lateinit var albums: AlbumStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("Album started")
        // albums = AlbumMemStore()
        // albums = AlbumJSONStore(applicationContext)
        // albums = AlbumRoomStore(applicationContext)
         albums = FirebaseAlbumStore()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        FirebaseApp.initializeApp(this)
    }
}

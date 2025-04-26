package ie.setu.album.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ie.setu.Album.main.MainApp
import ie.setu.Album.R
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {

    private lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        app = application as MainApp

        // Using Coroutine to add a slight delay
        GlobalScope.launch {
            delay(2000) // 2 seconds
            withContext(Dispatchers.Main) {
                startActivity(Intent(this@SplashActivity, AlbumListActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        }
    }
}
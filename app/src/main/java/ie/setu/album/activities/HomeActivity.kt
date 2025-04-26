package ie.setu.album.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import ie.setu.Album.R
import ie.setu.Album.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val spinAnimation = AnimationUtils.loadAnimation(this, R.anim.spin)
        binding.vinylImage.startAnimation(spinAnimation)


        binding.enterStoreButton.setOnClickListener {
            val intent = Intent(this, AlbumListActivity::class.java)
            startActivity(intent)
        }



    }
}


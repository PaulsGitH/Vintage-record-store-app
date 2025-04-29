package ie.setu.album.activities

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.squareup.picasso.Picasso
import ie.setu.album.R
import ie.setu.album.models.AlbumModel

class ViewAlbumActivity : AppCompatActivity() {

    private lateinit var album: AlbumModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_album)

        album = intent.getParcelableExtra("album_view") ?: return

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)

        showAlbum()
    }

    private fun showAlbum() {
        val albumCover = findViewById<ImageView>(R.id.albumCoverImage)
        val albumName = findViewById<TextView>(R.id.viewAlbumName)
        val albumArtist = findViewById<TextView>(R.id.viewAlbumArtist)
        val albumDescription = findViewById<TextView>(R.id.viewAlbumDescription)
        val albumGenre = findViewById<TextView>(R.id.viewAlbumGenre)
        val albumReleaseDate = findViewById<TextView>(R.id.viewAlbumReleaseDate)
        val albumCost = findViewById<TextView>(R.id.viewAlbumCost)
        val albumRating = findViewById<android.widget.RatingBar>(R.id.viewAlbumRating)
        val visitBandWebsite = findViewById<TextView>(R.id.visitBandWebsite)
        val trackListContainer = findViewById<LinearLayout>(R.id.trackListContainer)
        val youtubePlayerView = findViewById<YouTubePlayerView>(R.id.youtubePlayerView)
        val trackListLabel = findViewById<TextView>(R.id.trackListLabel)

        // Set album details
        albumName.text = album.albumName
        albumName.setTypeface(null, Typeface.BOLD)
        albumArtist.text = album.artist
        albumArtist.setTypeface(null, Typeface.BOLD)
        albumDescription.text = album.albumDescription
        albumGenre.text = album.albumGenre
        albumGenre.setTypeface(null, Typeface.BOLD)
        albumReleaseDate.text = album.albumReleaseDate
        albumReleaseDate.setTypeface(null, Typeface.BOLD)
        albumCost.text = getString(R.string.euro_symbol, album.cost)
        albumCost.setTypeface(null, Typeface.BOLD)
        albumRating.rating = album.rating.toFloat()
        trackListLabel.setTypeface(null, Typeface.BOLD)


        try {
            if (album.albumImage != Uri.EMPTY) {
                contentResolver.openInputStream(album.albumImage)?.use {
                    Picasso.get()
                        .load(album.albumImage)
                        .resize(600, 600)
                        .centerCrop()
                        .into(albumCover)
                } ?: albumCover.setImageResource(R.mipmap.ic_launcher)
            } else {
                albumCover.setImageResource(R.mipmap.ic_launcher)
            }
        } catch (e: Exception) {
            albumCover.setImageResource(R.mipmap.ic_launcher)
        }


        visitBandWebsite.setOnClickListener {
            val url = album.linkToAlbumWebsite.trim()
            if (url.startsWith("http://") || url.startsWith("https://")) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.enter_valid_album_website), Toast.LENGTH_SHORT).show()
            }
        }

        for ((key, value) in album.trackList.toSortedMap(compareBy { it.substringAfter("Track ").toIntOrNull() ?: 0 })) {
            val trackTextView = TextView(this).apply {
                text = "$key: $value"
                textSize = 16f
                setPadding(0, 8, 0, 8)
            }
            trackListContainer.addView(trackTextView)
        }

        // Setup YouTube player
        if (album.sampleSongYouTube.isNotEmpty()) {
            lifecycle.addObserver(youtubePlayerView)
            youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    extractVideoId(album.sampleSongYouTube)?.let { videoId ->
                        youTubePlayer.cueVideo(videoId, 0f)
                    }
                }
            })
        }
    }

    private fun extractVideoId(url: String): String? {
        return when {
            "v=" in url -> url.substringAfter("v=").substringBefore("&")
            "youtu.be/" in url -> url.substringAfterLast("/")
            else -> null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_view_album, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_edit_album -> {
                val launcherIntent = Intent(this, AlbumActivity::class.java)
                launcherIntent.putExtra("album_edit", album)
                startActivity(launcherIntent)
                true
            }
            R.id.item_album_list -> {
                val intent = Intent(this, AlbumListActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

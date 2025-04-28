package ie.setu.album.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import ie.setu.album.R
import ie.setu.album.databinding.ActivityAlbumBinding
import ie.setu.album.helpers.showImagePicker
import ie.setu.album.main.MainApp
import ie.setu.album.models.AlbumModel
import ie.setu.album.activities.AlbumListActivity
import ie.setu.album.activities.HomeActivity
import ie.setu.album.activities.FavoritesActivity
import timber.log.Timber.i
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class AlbumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlbumBinding
    private lateinit var imageIntentLauncher: ActivityResultLauncher<Intent>
    var album = AlbumModel()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nightModeFlags = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK

        if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            binding.albumRating.progressTintList = ContextCompat.getColorStateList(this, R.color.md_theme_primary)
        } else {
            binding.albumRating.progressTintList = ContextCompat.getColorStateList(this, R.color.md_theme_primary)
        }

        binding.topAppBar.title = title
        setSupportActionBar(binding.topAppBar)

        app = application as MainApp
        i(getString(R.string.album_activity_started))

        val genres = resources.getStringArray(R.array.album_genres)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.albumGenre.adapter = adapter

        var edit = false // Tracks if an album is being edited

        if (intent.hasExtra("album_edit")) {
            edit = true
            binding.btnAdd.setText(R.string.save_album)
            album = intent.extras?.getParcelable("album_edit")!!
            binding.albumName.setText(album.albumName)
            binding.albumDescription.setText(album.albumDescription)
            binding.albumArtist.setText(album.artist)
            binding.albumCost.setText(album.cost.toString())
            binding.albumReleaseDate.setText(album.albumReleaseDate)
            binding.albumGenre.setSelection(genres.indexOf(album.albumGenre))
            binding.albumRating.progressTintList = ContextCompat.getColorStateList(this, R.color.md_theme_primary)
            binding.sampleSongYouTube.setText(album.sampleSongYouTube)
            binding.linkToAlbumWebsite.setText(album.linkToAlbumWebsite)

            if (album.trackList.isNotEmpty()) {
                for ((key, value) in album.trackList.toSortedMap()) {
                    val trackInput = EditText(this).apply {
                        hint = key
                        setText(value)
                        inputType = InputType.TYPE_CLASS_TEXT
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            topMargin = 8
                        }
                    }
                    binding.trackListContainer.addView(trackInput)
                }
            }

            val youtubePlayerView = findViewById<YouTubePlayerView>(R.id.youtube_player_view)

            lifecycle.addObserver(youtubePlayerView)

            youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    val videoId = extractVideoId(album.sampleSongYouTube)
                    if (videoId != null) {
                        youTubePlayer.loadVideo(videoId, 0f)
                    }
                }
            })

        }

        binding.btnAdd.setOnClickListener {
            album.albumName = binding.albumName.text.toString()
            album.albumDescription = binding.albumDescription.text.toString()
            album.artist = binding.albumArtist.text.toString()
            album.albumReleaseDate = binding.albumReleaseDate.text.toString()
            album.albumGenre = binding.albumGenre.selectedItem.toString()
            album.cost = binding.albumCost.text.toString().toDoubleOrNull() ?: 0.0
            album.rating = binding.albumRating.rating.toInt()

            // Builds track list from the addtrack buttons EditText
            val trackMap = mutableMapOf<String, String>()
            for (i in 0 until binding.trackListContainer.childCount) {
                val view = binding.trackListContainer.getChildAt(i)
                if (view is EditText) {
                    val trackTitle = view.text.toString().trim()
                    if (trackTitle.isNotEmpty()) {
                        trackMap["Track ${i + 1}"] = trackTitle
                    }
                }
            }
            album.trackList = trackMap
            album.sampleSongYouTube = binding.sampleSongYouTube.text.toString().trim()
            album.linkToAlbumWebsite = binding.linkToAlbumWebsite.text.toString().trim()

            val existingAlbum = app.albums.findAll().find { it.albumName == album.albumName }

            if (album.albumName.isNotEmpty() && album.artist.isNotEmpty() &&
                album.albumDescription.isNotEmpty() && album.albumDescription.length <= 750 &&
                album.albumGenre != "Select Genre" && album.albumReleaseDate.isNotEmpty() &&
                album.cost > 0 && album.albumImage != Uri.EMPTY && album.trackList.isNotEmpty() &&
                album.sampleSongYouTube.isNotEmpty() &&
                (album.sampleSongYouTube.contains("youtube.com") || album.sampleSongYouTube.contains("youtu.be")) &&
                Patterns.WEB_URL.matcher(album.linkToAlbumWebsite).matches() &&
                (edit || existingAlbum == null)
            ) {

                if (edit) {
                    app.albums.update(album.copy())
                    setResult(RESULT_OK)
                    finish()
                } else {
                    app.albums.create(album.copy())
                    Snackbar.make(binding.root, getString(R.string.album_added_success), Snackbar.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        setResult(RESULT_OK)
                        finish()
                    }, 500)
                }

            } else {
                when {
                    album.albumName.isEmpty() -> Snackbar.make(it, getString(R.string.enter_album_title), Snackbar.LENGTH_LONG).show()
                    existingAlbum != null -> Snackbar.make(it, getString(R.string.album_duplicate_title), Snackbar.LENGTH_LONG).show()
                    album.artist.isEmpty() -> Snackbar.make(it, getString(R.string.hint_albumArtist), Snackbar.LENGTH_LONG).show()
                    album.albumDescription.isEmpty() -> Snackbar.make(it, getString(R.string.enter_album_description), Snackbar.LENGTH_LONG).show()
                    album.albumDescription.length > 750 -> Snackbar.make(it, getString(R.string.album_description_too_long), Snackbar.LENGTH_LONG).show()
                    album.albumGenre == "Select Genre" -> Snackbar.make(it, getString(R.string.hint_albumGenre), Snackbar.LENGTH_LONG).show()
                    album.albumReleaseDate.isEmpty() -> Snackbar.make(it, getString(R.string.hint_albumReleaseDate), Snackbar.LENGTH_LONG).show()
                    album.cost <= 0 -> Snackbar.make(it, getString(R.string.hint_albumCost), Snackbar.LENGTH_LONG).show()
                    album.albumImage == Uri.EMPTY -> Snackbar.make(it, getString(R.string.enter_album_image), Snackbar.LENGTH_LONG).show()
                    album.trackList.isEmpty() -> Snackbar.make(it, getString(R.string.enter_at_least_one_track), Snackbar.LENGTH_LONG).show()
                    album.sampleSongYouTube.isEmpty() -> Snackbar.make(it, getString(R.string.enter_album_youtube), Snackbar.LENGTH_LONG).show()
                    !album.sampleSongYouTube.contains("youtube.com") && !album.sampleSongYouTube.contains("youtu.be") ->
                        Snackbar.make(it, getString(R.string.youtube_link_only), Snackbar.LENGTH_LONG).show()
                    !Patterns.WEB_URL.matcher(album.linkToAlbumWebsite).matches() -> Snackbar.make(it, getString(R.string.enter_valid_album_website), Snackbar.LENGTH_LONG).show()
                }
            }
        }

        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }
        registerImagePickerCallback()

        // Date Picker for Album Release Date
        binding.albumReleaseDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                binding.albumReleaseDate.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }

        // Keep count of tracks for the tracklist segment of the album page
        var trackCount = 0

        binding.addTrackButton.setOnClickListener {
            trackCount++
            val trackInput = EditText(this).apply {
                hint = getString(R.string.enter_track_name_hint, trackCount)
                inputType = InputType.TYPE_CLASS_TEXT
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 8
                }
            }
            binding.trackListContainer.addView(trackInput)
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.nav_albums -> {
                    startActivity(Intent(this, AlbumListActivity::class.java))
                    true
                }
                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    true
                }
                else -> false
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_album, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                setResult(RESULT_CANCELED)
                finish()
            }
            R.id.item_delete -> {
                app.albums.delete(album)
                setResult(RESULT_OK)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val dataUri = result.data?.data
                if (result.resultCode == RESULT_OK && dataUri != null) {
                    contentResolver.takePersistableUriPermission(
                        dataUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    album.albumImage = dataUri
                    Picasso.get()
                        .load(album.albumImage)
                        .resize(800, 600)
                        .centerCrop()
                        .into(binding.albumImage)
                    binding.chooseImage.setText(R.string.change_album_image)
                }
            }


        if (intent.hasExtra("album_edit")) {
            Picasso.get()
                .load(album.albumImage)
                .resize(800, 600)
                .into(binding.albumImage)
            if (album.albumImage != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.change_album_image)
            }
        }
    }

    fun extractVideoId(url: String): String? {
        val regex = "(?<=v=|be/|embed/)[^&#]+".toRegex()
        return regex.find(url)?.value
    }


}
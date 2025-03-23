package ie.setu.Album.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import ie.setu.Album.R
import ie.setu.Album.databinding.ActivityAlbumBinding
import ie.setu.Album.helpers.showImagePicker
import ie.setu.Album.main.MainApp
import ie.setu.Album.models.AlbumModel
import timber.log.Timber.i

class AlbumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlbumBinding
    private lateinit var imageIntentLauncher: ActivityResultLauncher<Intent>
    var album = AlbumModel()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            binding.albumRating.rating = album.rating.toFloat()
        }

        binding.btnAdd.setOnClickListener {
            album.albumName = binding.albumName.text.toString()
            album.albumDescription = binding.albumDescription.text.toString()
            album.artist = binding.albumArtist.text.toString()
            album.albumReleaseDate = binding.albumReleaseDate.text.toString()
            album.albumGenre = binding.albumGenre.selectedItem.toString()
            album.cost = binding.albumCost.text.toString().toDoubleOrNull() ?: 0.0
            album.rating = binding.albumRating.rating.toInt()

            val existingAlbum = app.albums.findAll().find { it.albumName == album.albumName }

            if (album.albumName.isNotEmpty() && album.artist.isNotEmpty() &&
                album.albumDescription.isNotEmpty() && album.albumDescription.length <= 750 &&
                album.albumGenre != "Select Genre" && album.albumReleaseDate.isNotEmpty() &&
                album.cost > 0 && album.albumImage != Uri.EMPTY && (edit || existingAlbum == null)) {

                if (edit) {
                    app.albums.update(album.copy())
                } else {
                    app.albums.create(album.copy())
                }
                setResult(RESULT_OK)
                finish()

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

            val datePicker = DatePickerDialog(this, { _, selectedYear, _, _ ->
                binding.albumReleaseDate.setText(selectedYear.toString())
            }, year, 0, 1)

            datePicker.show()
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
                if (result.resultCode == RESULT_OK && result.data != null) {
                    i("Got Result ${result.data!!.data}")
                    album.albumImage = result.data!!.data!!
                    Picasso.get()
                        .load(album.albumImage)
                        .resize(800, 600)
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
}
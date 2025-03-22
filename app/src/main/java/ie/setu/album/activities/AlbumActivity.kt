package ie.setu.Album.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    var Album = AlbumModel()
    lateinit var app : MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.title = title
        setSupportActionBar(binding.topAppBar)

        app = application as MainApp
        i(getString(R.string.Album_activity_started))

        var edit = false //tracks if we arrived here via an existing Album

        if (intent.hasExtra("Album_edit")) {
            edit = true
            binding.btnAdd.setText(R.string.save_Album)
            Album = intent.extras?.getParcelable("Album_edit")!!
            binding.AlbumTitle.setText(Album.title)
            binding.AlbumDescription.setText(Album.description)
        }

        binding.btnAdd.setOnClickListener {
            Album.title = binding.AlbumTitle.text.toString()
            Album.description = binding.AlbumDescription.text.toString()

            val existingAlbum = app.Albums.findAll().find { it.title == Album.title }

            if (Album.title.isNotEmpty() && Album.description.isNotEmpty() &&
                Album.description.length <= 750 && Album.image != Uri.EMPTY &&
                (edit || existingAlbum == null)) {

                if (edit) {
                    app.Albums.update(Album.copy())
                } else {
                    app.Albums.create(Album.copy())
                }
                setResult(RESULT_OK)
                finish()

            } else {
                when {
                    Album.title.isEmpty() -> Snackbar.make(it, getString(R.string.enter_Album_title), Snackbar.LENGTH_LONG).show()
                    existingAlbum != null -> Snackbar.make(it, getString(R.string.Album_duplicate_title), Snackbar.LENGTH_LONG).show()
                    Album.description.isEmpty() -> Snackbar.make(it, getString(R.string.enter_Album_description), Snackbar.LENGTH_LONG).show()
                    Album.description.length > 750 -> Snackbar.make(it, getString(R.string.Album_description_too_long), Snackbar.LENGTH_LONG).show()
                    Album.image == Uri.EMPTY -> Snackbar.make(it, getString(R.string.enter_Album_image), Snackbar.LENGTH_LONG).show()
                }
            }
        }


        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }
        registerImagePickerCallback()

        binding.AlbumYear.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)

            val datePicker = DatePickerDialog(this, { _, selectedYear, _, _ ->
                binding.AlbumYear.setText(selectedYear.toString())
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
                app.Albums.delete(Album)
                setResult(RESULT_OK)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            Album.image = result.data!!.data!!
                            Picasso.get()
                                .load(Album.image)
                                .resize(800, 600)
                                .into(binding.AlbumImage)
                            binding.chooseImage.setText(R.string.change_Album_image)
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }

        if (intent.hasExtra("Album_edit")) {
            Picasso.get()
                .load(Album.image)
                .resize(800, 600)
                .into(binding.AlbumImage)
            if (Album.image != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.change_Album_image)
            }
        }

    }

}
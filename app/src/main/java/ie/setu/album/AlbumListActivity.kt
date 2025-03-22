package ie.setu.Album.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ie.setu.Album.R
import ie.setu.Album.adapters.AlbumAdapter
import ie.setu.Album.adapters.AlbumListener
import ie.setu.Album.databinding.ActivityAlbumListBinding
import ie.setu.Album.main.MainApp
import ie.setu.Album.models.AlbumModel

class AlbumListActivity : AppCompatActivity(), AlbumListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityAlbumListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.topAppBar.title = title
        setSupportActionBar(binding.topAppBar)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = AlbumAdapter(app.Albums.findAll(), this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu.findItem(R.id.item_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterAlbums(newText.orEmpty())
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, AlbumActivity::class.java)
                getResult.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                (binding.recyclerView.adapter)?.
                notifyItemRangeChanged(0,app.Albums.findAll().size)
                binding.recyclerView.adapter?.notifyDataSetChanged() // necessary to refresh menu after deletion of item
            }
            if (it.resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(binding.root, "Album Add Cancelled", Snackbar.LENGTH_LONG).show()
            }
        }

    override fun onAlbumClick(Album: AlbumModel) {
        val launcherIntent = Intent(this, AlbumActivity::class.java)
        launcherIntent.putExtra("Album_edit", Album)
        getResult.launch(launcherIntent)
    }

    private fun filterAlbums(query: String) {
        val filteredList = app.Albums.findAll().filter {
            it.title.contains(query, ignoreCase = true)
        }

        (binding.recyclerView.adapter as AlbumAdapter).updateList(filteredList)
    }



}

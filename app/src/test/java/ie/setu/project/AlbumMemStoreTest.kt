package ie.setu.project

import android.net.Uri
import ie.setu.album.models.AlbumMemStore
import ie.setu.album.models.AlbumModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config



@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [android.os.Build.VERSION_CODES.TIRAMISU] // run tests on API 33 to satisfy Robolectric maxSdkVersion
)
class AlbumMemStoreTest {

    private lateinit var dummyUri: Uri
    private lateinit var pinkFloyd: AlbumModel
    private lateinit var beatles: AlbumModel
    private lateinit var queen: AlbumModel
    private lateinit var bowie: AlbumModel
    private lateinit var adele: AlbumModel
    private lateinit var populatedAlbums: AlbumMemStore
    private lateinit var emptyAlbums: AlbumMemStore

    @Before
    fun setup() {
        // Robolectric provides Uri.parse on the JVM
        dummyUri = Uri.parse("https://example.com/dummy.png")

        pinkFloyd = AlbumModel(
            albumName = "The Wall",
            artist = "Pink Floyd",
            albumGenre = "Rock",
            albumImage = dummyUri
        )
        beatles = AlbumModel(
            albumName = "Abbey Road",
            artist = "The Beatles",
            albumGenre = "Pop",
            albumImage = dummyUri
        )
        queen = AlbumModel(
            albumName = "A Night at the Opera",
            artist = "Queen",
            albumGenre = "Rock",
            albumImage = dummyUri
        )
        bowie = AlbumModel(
            albumName = "Heroes",
            artist = "David Bowie",
            albumGenre = "Alternative",
            albumImage = dummyUri
        )
        adele = AlbumModel(
            albumName = "25",
            artist = "Adele",
            albumGenre = "Soul",
            albumImage = dummyUri
        )

        populatedAlbums = AlbumMemStore().apply {
            create(pinkFloyd)
            create(beatles)
            create(queen)
            create(bowie)
            create(adele)
        }
        emptyAlbums = AlbumMemStore()
    }


    @Test
    fun addingToPopulatedListAddsAlbum() {
        val newAlbum = AlbumModel(
            albumName = "Random Access Memories",
            artist = "Daft Punk",
            albumGenre = "Electronic",
            albumImage = dummyUri
        )
        assertEquals(5, populatedAlbums.findAll().size)
        populatedAlbums.create(newAlbum)
        assertEquals(6, populatedAlbums.findAll().size)
        assertEquals("Random Access Memories", populatedAlbums.findAll().last().albumName)
    }

    @Test
    fun addingToEmptyListAddsAlbum() {
        val newAlbum = AlbumModel(
            albumName = "1989",
            artist = "Taylor Swift",
            albumGenre = "Pop",
            albumImage = dummyUri
        )
        assertEquals(0, emptyAlbums.findAll().size)
        emptyAlbums.create(newAlbum)
        assertEquals(1, emptyAlbums.findAll().size)
        assertEquals("1989", emptyAlbums.findAll().last().albumName)
    }

    @Test
    fun findAllEmptyWhenStoreEmpty() {
        assertTrue(emptyAlbums.findAll().isEmpty())
    }

    @Test
    fun findAllReturnsAllWhenStorePopulated() {
        val albums = populatedAlbums.findAll()
        assertEquals(5, albums.size)
        assertTrue(albums.any { it.albumName == "The Wall" })
        assertTrue(albums.any { it.albumName == "Abbey Road" })
        assertTrue(albums.any { it.albumName == "A Night at the Opera" })
        assertTrue(albums.any { it.albumName == "Heroes" })
        assertTrue(albums.any { it.albumName == "25" })
    }

    @Test
    fun searchAllReturnsMatches() {
        val results = populatedAlbums.searchAll("Queen")
        assertEquals(1, results.size)
        assertEquals("Queen", results.first().artist)
    }

    @Test
    fun searchAllReturnsEmptyWhenNoMatch() {
        assertTrue(populatedAlbums.searchAll("Metallica").isEmpty())
    }

    @Test
    fun findFavoritesReturnsOnlyFavorites() {
        val all = populatedAlbums.findAll()
        all[0].isFavorite = true
        all[3].isFavorite = true
        val results = populatedAlbums.findFavorites()
        assertEquals(2, results.size)
        assertTrue(results.all { it.isFavorite })
    }

    @Test
    fun deletingExistingRemoves() {
        val toDelete = populatedAlbums.findAll().first()
        populatedAlbums.delete(toDelete)
        assertFalse(populatedAlbums.findAll().contains(toDelete))
    }

    @Test
    fun deletingNonExistingDoesNothing() {
        val ghost = AlbumModel(
            albumId = 999,
            albumName = "Ghost",
            albumImage = dummyUri
        )
        populatedAlbums.delete(ghost)
        assertEquals(5, populatedAlbums.findAll().size)
    }

    @Test
    fun updatingExistingUpdatesFields() {
        val original = populatedAlbums.findAll().first()
        val updated = original.copy(
            albumName = "The Wall (Remastered)",
            rating = 5,
            isFavorite = true
        ).apply { albumId = original.albumId }
        populatedAlbums.update(updated)
        val retrieved = populatedAlbums.findAll().first { it.albumId == original.albumId }
        assertEquals("The Wall (Remastered)", retrieved.albumName)
        assertEquals(5, retrieved.rating)
        assertTrue(retrieved.isFavorite)
    }

    @Test
    fun updatingNonExistingDoesNothing() {
        val non = AlbumModel(
            albumId = 999,
            albumName = "Nonexistent",
            albumImage = dummyUri
        )
        populatedAlbums.update(non)
        assertEquals(5, populatedAlbums.findAll().size)
    }
}



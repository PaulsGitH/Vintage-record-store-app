package ie.setu.project

import ie.setu.album.models.AlbumMemStore
import ie.setu.album.models.AlbumModel
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AlbumMemStoreTest {

    private var pinkFloyd: AlbumModel? = null
    private var beatles: AlbumModel? = null
    private var queen: AlbumModel? = null
    private var bowie: AlbumModel? = null
    private var adele: AlbumModel? = null
    private var populatedAlbums: AlbumMemStore? = null
    private var emptyAlbums: AlbumMemStore? = null

    @BeforeEach
    fun setup() {
        pinkFloyd = AlbumModel(albumName = "The Wall", artist = "Pink Floyd", albumGenre = "Rock")
        beatles = AlbumModel(albumName = "Abbey Road", artist = "The Beatles", albumGenre = "Pop")
        queen = AlbumModel(
            albumName = "A Night at the Opera",
            artist = "Queen",
            albumGenre = "Rock"
        )
        bowie = AlbumModel(albumName = "Heroes", artist = "David Bowie", albumGenre = "Alternative")
        adele = AlbumModel(albumName = "25", artist = "Adele", albumGenre = "Soul")

        populatedAlbums = AlbumMemStore()
        populatedAlbums!!.create(pinkFloyd!!)
        populatedAlbums!!.create(beatles!!)
        populatedAlbums!!.create(queen!!)
        populatedAlbums!!.create(bowie!!)
        populatedAlbums!!.create(adele!!)

        emptyAlbums = AlbumMemStore()
    }

    @AfterEach
    fun tearDown() {
        pinkFloyd = null
        beatles = null
        queen = null
        bowie = null
        adele = null
        populatedAlbums = null
        emptyAlbums = null
    }

    @Nested
    inner class AddAlbums {
        @Test
        fun `adding an album to a populated list adds to ArrayList`() {
            val newAlbum = AlbumModel(
                albumName = "Random Access Memories",
                artist = "Daft Punk",
                albumGenre = "Electronic"
            )
            assertEquals(5, populatedAlbums!!.findAll().size)
            populatedAlbums!!.create(newAlbum)
            assertEquals(6, populatedAlbums!!.findAll().size)
            assertEquals(newAlbum.albumName, populatedAlbums!!.findAll().last().albumName)
        }

        @Test
        fun `adding an album to an empty list adds to ArrayList`() {
            val newAlbum = AlbumModel(
                albumName = "1989",
                artist = "Taylor Swift",
                albumGenre = "Pop"
            )
            assertEquals(0, emptyAlbums!!.findAll().size)
            emptyAlbums!!.create(newAlbum)
            assertEquals(1, emptyAlbums!!.findAll().size)
            assertEquals(newAlbum.albumName, emptyAlbums!!.findAll().last().albumName)
        }
    }

    @Nested
    inner class ListAlbums {

        @Test
        fun `findAll returns empty list when AlbumMemStore is empty`() {
            assertEquals(0, emptyAlbums!!.findAll().size)
        }

        @Test
        fun `findAll returns all albums when AlbumMemStore is populated`() {
            val albums = populatedAlbums!!.findAll()
            assertEquals(5, albums.size)
            assertTrue(albums.any { it.albumName == "The Wall" })
            assertTrue(albums.any { it.albumName == "Abbey Road" })
            assertTrue(albums.any { it.albumName == "A Night at the Opera" })
            assertTrue(albums.any { it.albumName == "Heroes" })
            assertTrue(albums.any { it.albumName == "25" })
        }

        @Test
        fun `searchAll returns correct results`() {
            val results = populatedAlbums!!.searchAll("Queen")
            assertEquals(1, results.size)
            assertEquals("Queen", results.first().artist)
        }

        @Test
        fun `searchAll returns empty when no match`() {
            val results = populatedAlbums!!.searchAll("Metallica")
            assertTrue(results.isEmpty())
        }

        @Test
        fun `findFavorites returns only favorite albums`() {
            populatedAlbums!!.findAll()[0].isFavorite = true
            populatedAlbums!!.findAll()[3].isFavorite = true
            val results = populatedAlbums!!.findFavorites()
            assertEquals(2, results.size)
            assertTrue(results.all { it.isFavorite })
        }
    }

    @Nested
    inner class DeleteAlbums {
        @Test
        fun `deleting an existing album removes it`() {
            val albumToDelete = populatedAlbums!!.findAll().first()
            populatedAlbums!!.delete(albumToDelete)
            assertFalse(populatedAlbums!!.findAll().contains(albumToDelete))
        }

        @Test
        fun `deleting a non-existing album does not crash`() {
            val nonExisting = AlbumModel(albumId = 999, albumName = "Ghost")
            populatedAlbums!!.delete(nonExisting)
            assertEquals(5, populatedAlbums!!.findAll().size) // no deletion
        }
    }

    @Nested
    inner class UpdateAlbums {
        @Test
        fun `updating an album updates its fields`() {
            val existingAlbum = populatedAlbums!!.findAll().first()
            val updatedAlbum = existingAlbum.copy(albumName = "The Wall (Remastered)", rating = 5)
            updatedAlbum.albumId = existingAlbum.albumId
            populatedAlbums!!.update(updatedAlbum)

            val retrieved = populatedAlbums!!.findAll().first { it.albumId == updatedAlbum.albumId }
            assertEquals("The Wall (Remastered)", retrieved.albumName)
            assertEquals(5, retrieved.rating)
        }

        @Test
        fun `updating non-existing album does nothing`() {
            val nonExistentAlbum = AlbumModel(albumId = 999, albumName = "Nonexistent")
            populatedAlbums!!.update(nonExistentAlbum)
            assertEquals(5, populatedAlbums!!.findAll().size)
        }
    }
}

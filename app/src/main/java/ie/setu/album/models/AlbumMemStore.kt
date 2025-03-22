package ie.setu.Album.models

import timber.log.Timber.i

var lastId = 0L
internal fun getId() = lastId++

class AlbumMemStore : AlbumStore {

    val Albums = ArrayList<AlbumModel>()

    override fun findAll(): List<AlbumModel> {
        return Albums
    }

    override fun create(Album: AlbumModel) {
        Album.id = getId()
        Albums.add(Album)
        logAll()
    }

    override fun update(Album: AlbumModel) {
        val foundAlbum: AlbumModel? = Albums.find { p -> p.id == Album.id }
        if (foundAlbum != null) {
            foundAlbum.title = Album.title
            foundAlbum.description = Album.description
            foundAlbum.image = Album.image
            logAll()
        }
    }

    override fun delete(Album: AlbumModel) {
        Albums.removeIf { it.id == Album.id }
        logAll()
    }

    private fun logAll() {
        Albums.forEach { i("$it") }
    }
}

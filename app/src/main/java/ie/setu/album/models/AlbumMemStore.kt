package ie.setu.Album.models

import timber.log.Timber.i

var lastId = 0L
internal fun getId() = lastId++

class AlbumMemStore : AlbumStore {

    val albums = ArrayList<AlbumModel>()

    override fun findAll(): List<AlbumModel> {
        return albums
    }

    override fun create(album: AlbumModel) {
        album.albumId = getId()
        albums.add(album)
        logAll()
    }

    override fun update(album: AlbumModel) {
        val foundAlbum: AlbumModel? = albums.find { a -> a.albumId == album.albumId }
        if (foundAlbum != null) {
            foundAlbum.albumName = album.albumName
            foundAlbum.albumDescription = album.albumDescription
            foundAlbum.albumImage = album.albumImage
            foundAlbum.albumReleaseDate = album.albumReleaseDate
            foundAlbum.albumGenre = album.albumGenre
            foundAlbum.artist = album.artist
            foundAlbum.rating = album.rating
            foundAlbum.cost = album.cost
            foundAlbum.trackList = album.trackList
            foundAlbum.linkToAlbumWebsite = album.linkToAlbumWebsite
            foundAlbum.sampleSongYouTube = album.sampleSongYouTube
            foundAlbum.isFavorite = album.isFavorite
            logAll()
        }
    }

    override fun delete(album: AlbumModel) {
        albums.remove(album)
        logAll()
    }

    private fun logAll() {
        albums.forEach { i("$it") }
    }

    fun searchAll(query: String): List<AlbumModel> {
        return albums.filter {
            it.albumName.contains(query, ignoreCase = true) ||
                    it.artist.contains(query, ignoreCase = true) ||
                    it.albumGenre.contains(query, ignoreCase = true)
        }
    }

    fun findFavorites(): List<AlbumModel> {
        return albums.filter { it.isFavorite }
    }


}

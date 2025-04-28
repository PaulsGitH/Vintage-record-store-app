package ie.setu.album.models

import android.content.Context
import android.net.Uri

class AlbumRoomStore(context: Context) : AlbumStore {

    private val db = AppDatabase.getInstance(context)
    private val albumDao = db.albumDao()

    override fun findAll(): List<AlbumModel> {
        return albumDao.findAll().map { toAlbumModel(it) }
    }

    override fun create(album: AlbumModel) {
        albumDao.insert(toAlbumEntity(album))
    }

    override fun update(album: AlbumModel) {
        albumDao.update(toAlbumEntity(album))
    }

    override fun delete(album: AlbumModel) {
        albumDao.delete(toAlbumEntity(album))
    }

    override fun searchAll(query: String): List<AlbumModel> {
        return findAll().filter {
            it.albumName.contains(query, ignoreCase = true) ||
                    it.artist.contains(query, ignoreCase = true) ||
                    it.albumGenre.contains(query, ignoreCase = true)
        }
    }

    override fun findFavorites(): List<AlbumModel> {
        return albumDao.findFavorites().map { toAlbumModel(it) }
    }

    private fun toAlbumEntity(model: AlbumModel) = AlbumEntity(
        albumId = model.albumId,
        albumName = model.albumName,
        albumDescription = model.albumDescription,
        albumImage = model.albumImage.toString(),
        albumReleaseDate = model.albumReleaseDate,
        albumGenre = model.albumGenre,
        artist = model.artist,
        rating = model.rating,
        cost = model.cost,
        trackList = model.trackList,
        linkToAlbumWebsite = model.linkToAlbumWebsite,
        sampleSongYouTube = model.sampleSongYouTube,
        isFavorite = model.isFavorite
    )

    private fun toAlbumModel(entity: AlbumEntity) = AlbumModel(
        albumId = entity.albumId,
        albumName = entity.albumName,
        albumDescription = entity.albumDescription,
        albumImage = Uri.parse(entity.albumImage),
        albumReleaseDate = entity.albumReleaseDate,
        albumGenre = entity.albumGenre,
        artist = entity.artist,
        rating = entity.rating,
        cost = entity.cost,
        trackList = entity.trackList,
        linkToAlbumWebsite = entity.linkToAlbumWebsite,
        sampleSongYouTube = entity.sampleSongYouTube,
        isFavorite = entity.isFavorite
    )
}
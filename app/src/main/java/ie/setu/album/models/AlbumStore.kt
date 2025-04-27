package ie.setu.Album.models

interface AlbumStore {
    fun findAll(): List<AlbumModel>
    fun create(album: AlbumModel)
    fun update(album: AlbumModel)
    fun delete(album: AlbumModel)
    fun searchAll(query: String): List<AlbumModel>
    fun findFavorites(): List<AlbumModel>
}

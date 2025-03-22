package ie.setu.Album.models

interface AlbumStore {
    fun findAll(): List<AlbumModel>
    fun create(Album: AlbumModel)
    fun update(Album: AlbumModel)
    fun delete(Album: AlbumModel)
}

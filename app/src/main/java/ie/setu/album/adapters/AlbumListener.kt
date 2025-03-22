package ie.setu.Album.adapters

import ie.setu.Album.models.AlbumModel

interface AlbumListener {
    fun onAlbumClick(Album: AlbumModel)
}

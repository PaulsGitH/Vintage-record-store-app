package ie.setu.album.adapters

import ie.setu.album.models.AlbumModel

interface AlbumListener {
    fun onAlbumClick(Album: AlbumModel)
}

package ie.setu.Album.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlbumModel(var albumId: Long = 0,
                      var albumName: String = "",
                      var albumImage: Uri = Uri.EMPTY,
                      var albumReleaseDate: String = "",
                      var albumGenre: String = "",
                      var artist: String = "",
                      var rating: Int = 1,
                      var cost: Double = 0.0,
                      var trackList: Map<String, String> = emptyMap(),
                      var linkToAlbumWebsite: String = "",
                      var sampleSongYouTube: String = ""
) : Parcelable
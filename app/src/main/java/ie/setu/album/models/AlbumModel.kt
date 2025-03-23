package ie.setu.Album.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents an album in the Vintage Records app.
 *
 * @property albumId Unique identifier for the album.
 * @property albumName Name of the album.
 * @property albumImage URI reference to the album cover image.
 * @property albumReleaseDate Release date of the album.
 * @property albumGenre Genre of the album (selected from predefined genres).
 * @property artist Name of the artist or band.
 * @property rating User rating of the album (out of 5 stars).
 * @property cost Price of the album.
 * @property trackList A map of track names and their durations.
 * @property linkToAlbumWebsite External link to the album’s official website.
 * @property sampleSongYouTube YouTube link to a sample song from the album.
 */

@Parcelize
data class AlbumModel(var albumId: Long = 0,
                      var albumName: String = "",
                      var albumDescription: String = "",
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
package ie.setu.album.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class representing a music album in the Vintage Record Store app.
 *
 * Implements [Parcelable] to allow easy transfer of album data between activities.
 *
 * @property albumId Unique ID used for local and database reference.
 * @property albumName Title of the album.
 * @property albumDescription Brief written description or summary of the album.
 * @property albumImage [Uri] pointing to the album’s cover art (local or remote).
 * @property albumReleaseDate Release date of the album, in `dd/MM/yyyy` format.
 * @property albumGenre Genre/category of the album (e.g., Rock, Jazz, Pop).
 * @property artist Artist or band that produced the album.
 * @property rating Integer value (1–5) representing user rating of the album.
 * @property cost Price of the album in Euros (€).
 * @property trackList Mutable map containing track titles and their order (e.g., "Track 1" to "Song Title").
 * @property linkToAlbumWebsite URL string linking to the artist’s or album’s official website.
 * @property sampleSongYouTube YouTube video link used to embed a sample song preview.
 * @property isFavorite Boolean flag indicating if the album is marked as a user favorite.
 * @property firebaseId Unique identifier used to sync this record with Firebase Firestore.
 */

@Parcelize
data class AlbumModel(
    var albumId: Long = 0,
    var albumName: String = "",
    var albumDescription: String = "",
    var albumImage: Uri = Uri.EMPTY,
    var albumReleaseDate: String = "",
    var albumGenre: String = "",
    var artist: String = "",
    var rating: Int = 1,
    var cost: Double = 0.0,
    var trackList: MutableMap<String, String> = mutableMapOf(),
    var linkToAlbumWebsite: String = "",
    var sampleSongYouTube: String = "",
    var isFavorite: Boolean = false,
    var firebaseId: String = ""
) : Parcelable

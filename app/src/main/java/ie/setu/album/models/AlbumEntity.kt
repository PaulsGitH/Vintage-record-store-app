package ie.setu.album.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class AlbumEntity(
    @PrimaryKey(autoGenerate = true)
    var albumId: Long = 0,
    var albumName: String = "",
    var albumDescription: String = "",
    var albumImage: String = "",
    var albumReleaseDate: String = "",
    var albumGenre: String = "",
    var artist: String = "",
    var rating: Int = 0,
    var cost: Double = 0.0,
    var trackList: MutableMap<String, String> = mutableMapOf(),
    var linkToAlbumWebsite: String = "",
    var sampleSongYouTube: String = "",
    var isFavorite: Boolean = false
)

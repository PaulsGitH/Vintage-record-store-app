package ie.setu.album.models

import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import ie.setu.album.helpers.*
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*

const val JSON_FILE = "albums.json"
val gsonBuilder: Gson = GsonBuilder()
    .setPrettyPrinting()
    .registerTypeAdapter(Uri::class.java, UriParser())
    .create()
val listType: Type = object : TypeToken<ArrayList<AlbumModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}


class AlbumJSONStore(private val context: Context) : AlbumStore {

    var albums = mutableListOf<AlbumModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<AlbumModel> {
        logAll()
        return albums
    }

    override fun create(album: AlbumModel) {
        album.albumId = generateRandomId()
        albums.add(album)
        serialize()
    }

    override fun update(album: AlbumModel) {
        val foundAlbum = albums.find { it.albumId == album.albumId }
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
            serialize()
        }
    }

    override fun delete(album: AlbumModel) {
        albums.remove(album)
        serialize()
    }

    override fun searchAll(query: String): List<AlbumModel> {
        return albums.filter {
            it.albumName.contains(query, ignoreCase = true) ||
                    it.artist.contains(query, ignoreCase = true) ||
                    it.albumGenre.contains(query, ignoreCase = true)
        }
    }

    override fun findFavorites(): List<AlbumModel> {
        return albums.filter { it.isFavorite }
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(albums, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        albums = gsonBuilder.fromJson(jsonString, listType)
    }

    private fun logAll() {
        albums.forEach { Timber.i("$it") }
    }
}

class UriParser : JsonDeserializer<Uri>, JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}

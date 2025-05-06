package ie.setu.album.models

import android.net.Uri
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class FirebaseAlbumStore : AlbumStore {
    private val db = Firebase.firestore
    private val col = db.collection("albums")

    override fun findAll(): List<AlbumModel> = runBlocking {
        val snapshot = col.get().await()
        snapshot.documents.map { it.toModel() }
    }

    override fun create(album: AlbumModel) {
        val docId = album.firebaseId.ifEmpty { col.document().id }
        album.firebaseId = docId
        col.document(docId).set(album.toMap())
    }

    override fun update(album: AlbumModel) {
        col.document(album.firebaseId).set(album.toMap())
    }

    override fun delete(album: AlbumModel) {
        col.document(album.firebaseId).delete()
    }

    override fun searchAll(query: String): List<AlbumModel> =
        findAll().filter {
            it.albumName.contains(query, true) ||
                    it.artist.contains(query, true) ||
                    it.albumGenre.contains(query, true)
        }

    override fun findFavorites(): List<AlbumModel> =
        findAll().filter { it.isFavorite }

    private fun DocumentSnapshot.toModel() = AlbumModel(
        albumId = id.toLongOrNull() ?: 0L,
        albumName = getString("albumName") ?: "",
        albumDescription = getString("albumDescription") ?: "",
        albumImage = Uri.parse(getString("albumImage") ?: ""),
        albumReleaseDate = getString("albumReleaseDate") ?: "",
        albumGenre = getString("albumGenre") ?: "",
        artist = getString("artist") ?: "",
        rating = getLong("rating")?.toInt() ?: 0,
        cost = getDouble("cost") ?: 0.0,
        trackList = (get("trackList") as? Map<String, String>)?.toMutableMap() ?: mutableMapOf(),
        linkToAlbumWebsite = getString("linkToAlbumWebsite") ?: "",
        sampleSongYouTube = getString("sampleSongYouTube") ?: "",
        isFavorite = getBoolean("isFavorite") ?: false,
        firebaseId = id
    )

    private fun AlbumModel.toMap() = mapOf(
        "albumName" to albumName,
        "albumDescription" to albumDescription,
        "albumImage" to albumImage.toString(),
        "albumReleaseDate" to albumReleaseDate,
        "albumGenre" to albumGenre,
        "artist" to artist,
        "rating" to rating,
        "cost" to cost,
        "trackList" to trackList,
        "linkToAlbumWebsite" to linkToAlbumWebsite,
        "sampleSongYouTube" to sampleSongYouTube,
        "isFavorite" to isFavorite
    )
}

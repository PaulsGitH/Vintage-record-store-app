package ie.setu.album.models

import androidx.room.*

@Dao
interface AlbumDao {
    @Query("SELECT * FROM albums")
    fun findAll(): List<AlbumEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(album: AlbumEntity)

    @Update
    fun update(album: AlbumEntity)

    @Delete
    fun delete(album: AlbumEntity)

    @Query("SELECT * FROM albums WHERE isFavorite = 1")
    fun findFavorites(): List<AlbumEntity>

    @Query(
        "SELECT * FROM albums WHERE albumName LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%' OR albumGenre LIKE '%' || :query || '%'"
    )
    fun searchAll(query: String): List<AlbumEntity>
}

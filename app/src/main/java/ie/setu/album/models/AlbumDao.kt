package ie.setu.album.models

import androidx.room.*

@Dao
interface AlbumDao {

    @Query("SELECT * FROM albums")
    fun findAll(): List<AlbumEntity>

    @Insert
    fun insert(album: AlbumEntity)

    @Update
    fun update(album: AlbumEntity)

    @Delete
    fun delete(album: AlbumEntity)

    @Query("SELECT * FROM albums WHERE isFavorite = 1")
    fun findFavorites(): List<AlbumEntity>
}
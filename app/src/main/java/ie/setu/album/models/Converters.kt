package ie.setu.album.models

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromTrackList(value: Map<String, String>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toTrackList(value: String): Map<String, String> {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(value, mapType)
    }
}

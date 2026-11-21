package com.wakemate.ai.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromIntList(list: List<Int>?): String {
        return gson.toJson(list ?: emptyList<Int>())
    }

    @TypeConverter
    fun toIntList(data: String?): List<Int> {
        if (data.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<Int>>() {}.type
        return try {
            gson.fromJson(data, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return gson.toJson(list ?: emptyList<String>())
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(data, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

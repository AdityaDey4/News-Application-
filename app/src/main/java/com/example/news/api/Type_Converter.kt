package com.example.news.api

import androidx.room.TypeConverter
import com.example.news.data_class.Source


class Type_Converter {

    @TypeConverter
    fun fromSource(s : Source): String{
        return s.name
    }
    @TypeConverter
    fun toSource(s: String): Source{
        return Source(s, s)
    }
}
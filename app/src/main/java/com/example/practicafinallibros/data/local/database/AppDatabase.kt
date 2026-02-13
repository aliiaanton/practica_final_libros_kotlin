package com.example.practicafinallibros.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.practicafinallibros.data.local.dao.BookDao
import com.example.practicafinallibros.data.local.dao.UserDao
import com.example.practicafinallibros.data.local.dao.UserFavoriteDao
import com.example.practicafinallibros.data.local.entity.BookEntity
import com.example.practicafinallibros.data.local.entity.UserEntity
import com.example.practicafinallibros.data.local.entity.UserFavoriteEntity

@Database(
    entities = [BookEntity::class, UserEntity::class, UserFavoriteEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun userDao(): UserDao
    abstract fun userFavoriteDao(): UserFavoriteDao
}
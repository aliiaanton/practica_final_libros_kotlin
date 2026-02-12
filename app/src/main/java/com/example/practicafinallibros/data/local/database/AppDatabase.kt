package com.example.practicafinallibros.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.practicafinallibros.data.local.dao.BookDao
import com.example.practicafinallibros.data.local.dao.UserDao
import com.example.practicafinallibros.data.local.entity.BookEntity
import com.example.practicafinallibros.data.local.entity.UserEntity

@Database(
    entities = [BookEntity::class, UserEntity::class],
    version = 2,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun userDao(): UserDao
}
package com.example.practicafinallibros.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val imageUri: String?,
    val createdAt: Long,
    val createdBy: String,
    val createdByName: String,
    val genre: String?,
    val pageCount: Int?,
    val publishYear: Int?
)
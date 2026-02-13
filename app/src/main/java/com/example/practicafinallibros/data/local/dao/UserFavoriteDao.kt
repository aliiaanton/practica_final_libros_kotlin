package com.example.practicafinallibros.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.practicafinallibros.data.local.entity.BookEntity
import com.example.practicafinallibros.data.local.entity.UserFavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserFavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: UserFavoriteEntity)

    @Query("DELETE FROM user_favorites WHERE userId = :userId AND bookId = :bookId")
    suspend fun removeFavorite(userId: String, bookId: Int)

    @Query("SELECT bookId FROM user_favorites WHERE userId = :userId")
    fun getFavoriteBookIds(userId: String): Flow<List<Int>>

    @Query("""
        SELECT b.* FROM books b
        INNER JOIN user_favorites uf ON b.id = uf.bookId
        WHERE uf.userId = :userId
        ORDER BY b.createdAt DESC
    """)
    fun getFavoriteBooks(userId: String): Flow<List<BookEntity>>
}

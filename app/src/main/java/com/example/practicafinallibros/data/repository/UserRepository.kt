package com.example.practicafinallibros.data.repository

import com.example.practicafinallibros.data.local.dao.UserDao
import com.example.practicafinallibros.data.local.entity.UserEntity
import com.example.practicafinalLibros.data.remote.api.AuthApiService

class UserRepository(
    private val api: AuthApiService,
    private val userDao: UserDao
) {

    fun observeCache() = userDao.getAllUsers()

    suspend fun refresh(token: String) {
        try {
            val usersDto = api.getAllUsers("Bearer $token")

            val usersEntity: List<UserEntity> = usersDto.map { userDto ->
                UserEntity(
                    id = userDto.id,
                    name = userDto.name,
                    email = userDto.email,
                    role = userDto.role
                )
            }

            usersEntity.forEach { userDao.insertUser(it) }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateUserName(token: String, userId: String, newName: String) {
        try {
            val updatedDto = api.updateUser(
                token = "Bearer $token",
                userId = userId,
                updateRequest = UpdateUserRequest(name = newName)
            )

            val entity = UserEntity(
                id = updatedDto.id,
                name = updatedDto.name,
                email = updatedDto.email,
                role = updatedDto.role
            )
            userDao.updateUser(entity)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun deleteUser(token: String, userId: String) {
        try {
            api.deleteUser("Bearer $token", userId)

            userDao.deleteUserById(userId)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getById(userId: String): UserEntity? {
        return userDao.getUserById(userId)
    }
}
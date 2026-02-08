package com.example.practicafinallibros.data.repository

import com.example.practicafinallibros.data.local.dao.UserDao
import com.example.practicafinallibros.data.local.entity.UserEntity
import com.example.practicafinallibros.data.remote.UsersApi
import com.example.practicafinallibros.data.remote.dto.UpdateUserRequest

class UserRepository(
    private val api: UsersApi,
    private val userDao: UserDao
) {

    fun observeCache() = userDao.getAllUsers()

    suspend fun refresh(token: String) {
        try {
            val response = api.getAllUsers("Bearer $token")

            if (response.isSuccessful) {
                val usersDto = response.body()!!
                val usersEntity: List<UserEntity> = usersDto.map { userDto ->
                    UserEntity(
                        id = userDto.id.toString(),
                        name = userDto.name,
                        email = userDto.email,
                        role = userDto.role
                    )
                }
                usersEntity.forEach { userDao.insertUser(it) }
            } else {
                throw Exception("Error: ${response.code()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateUserName(token: String, userId: String, newName: String) {
        try {
            val response = api.updateUser(
                token = "Bearer $token",
                id = userId.toLong(),
                request = UpdateUserRequest(name = newName)
            )

            if (response.isSuccessful) {
                val updatedDto = response.body()!!
                val entity = UserEntity(
                    id = updatedDto.id.toString(),
                    name = updatedDto.name,
                    email = updatedDto.email,
                    role = updatedDto.role
                )
                userDao.updateUser(entity)
            } else {
                throw Exception("Error: ${response.code()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun deleteUser(token: String, userId: String) {
        try {
            val response = api.deleteUser("Bearer $token", userId.toLong())

            if (response.isSuccessful) {
                userDao.deleteUserById(userId)
            } else {
                throw Exception("Error: ${response.code()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getById(userId: String): UserEntity? {
        return userDao.getUserById(userId)
    }
}
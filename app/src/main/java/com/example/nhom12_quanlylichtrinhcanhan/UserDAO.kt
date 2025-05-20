package com.example.nhom12_quanlylichtrinhcanhan

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDAO {

    // Thêm
    @Insert
    fun insertUser(tbUser: tb_user)

    // Xóa
    @Delete
    fun deleteUser(tbUser: tb_user)

    // Sửa
    @Update
    fun updateUser(tbUser: tb_user)

    @Query("SELECT * FROM table_user WHERE username = :username LIMIT 1")
    fun getUserByUsername(username: String): tb_user?

    // Đọc tất cả người dùng
    @Query("SELECT * FROM table_user")
    fun getAllUser(): List<tb_user>
}

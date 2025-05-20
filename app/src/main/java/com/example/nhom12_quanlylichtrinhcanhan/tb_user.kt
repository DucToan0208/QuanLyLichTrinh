package com.example.nhom12_quanlylichtrinhcanhan

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_user")
data class tb_user(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val sdt: String,
    val username: String,
    var password: String
)
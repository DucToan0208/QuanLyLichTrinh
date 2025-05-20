package com.example.nhom12_quanlylichtrinhcanhan

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_schedule")
data class tb_schedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val image: String? = null
)

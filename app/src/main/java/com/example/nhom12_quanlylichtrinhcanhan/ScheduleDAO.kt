package com.example.nhom12_quanlylichtrinhcanhan

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ScheduleDAO{
    // Thêm
    @Insert
    fun insertSchedule(tbSchedule: tb_schedule)

    // Xóa
    @Delete
    fun deleteSchedule(tbSchedule: tb_schedule)

    // Sửa
    @Update
    fun updateSchedule(tbSchedule: tb_schedule)

    // Đọc
    @Query("SELECT * FROM table_schedule")
    fun getAllSchedule(): List<tb_schedule>

    // Sắp xếp theo ngày bé đến lớn
    @Query("SELECT * FROM table_schedule ORDER BY date ASC")
    fun getAllScheduleASC(): List<tb_schedule>

    // Lấy theo danh mục
    @Query("SELECT * FROM table_schedule WHERE category = :category")
    fun getScheduleByCategory(category: String): List<tb_schedule>
}
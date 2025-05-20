package com.example.nhom12_quanlylichtrinhcanhan

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [tb_user::class], version = 1)
abstract class CreateDatabaseUser:RoomDatabase() {
    abstract fun userDAO(): UserDAO
}

@Database(entities = [tb_schedule::class], version = 1)
abstract class CreateDatabaseSchedule:RoomDatabase() {
    abstract fun scheduleDAO(): ScheduleDAO
}
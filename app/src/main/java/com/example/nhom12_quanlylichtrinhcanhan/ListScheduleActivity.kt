package com.example.nhom12_quanlylichtrinhcanhan

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListScheduleActivity : AppCompatActivity() {

    private lateinit var imgExit: ImageView
    private lateinit var imgAdd: ImageView
    private lateinit var lvListSchedule: ListView
    private lateinit var adapter: ScheduleAdapter

    private lateinit var db: CreateDatabaseSchedule
    private lateinit var scheduleDAO: ScheduleDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_schedule)

        setControl()

        setEvent()
    }

    private fun setControl() {
        imgExit = findViewById(R.id.imgExit)
        lvListSchedule = findViewById(R.id.lvListSchedule)
        imgAdd = findViewById(R.id.imgAdd)
    }

    private fun setEvent() {
        initDatabase()
        loadSchedules()
        imgExit.setOnClickListener {
            loadSchedules()
            finish()
        }

        imgAdd.setOnClickListener {
            val intent = Intent(this, AddScheduleActivity::class.java)
            startActivity(intent)
        }

        deleteSchedule()

        updateSchedule()
    }

    private fun initDatabase() {
        db = Room.databaseBuilder(
            applicationContext,
            CreateDatabaseSchedule::class.java, "schedule_database"
        ).build()
        scheduleDAO = db.scheduleDAO()
    }

    private fun loadSchedules() {
        CoroutineScope(Dispatchers.IO).launch {
            val schedules = scheduleDAO.getAllScheduleASC()
            runOnUiThread {
                adapter = ScheduleAdapter(this@ListScheduleActivity, schedules.toMutableList())
                lvListSchedule.adapter = adapter
            }
        }
    }

    // Xóa lịch trình
    private fun deleteSchedule() {
        lvListSchedule.setOnItemLongClickListener { _, _, position, _ ->
            // Lấy đối tượng lịch trình từ danh sách
            val selectedSchedule = adapter.getItem(position) as tb_schedule

            // Tạo một AlertDialog xác nhận
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Xác nhận xóa")
            builder.setMessage("Bạn có chắc chắn muốn xóa lịch trình này?")

            // Nếu người dùng Xóa
            builder.setPositiveButton("Xóa") { _, _ ->
                // Xóa lịch trình khỏi database
                CoroutineScope(Dispatchers.IO).launch {
                    scheduleDAO.deleteSchedule(selectedSchedule)

                    // Cập nhật lại danh sách sau khi xóa
                    val schedules = scheduleDAO.getAllSchedule()
                    withContext(Dispatchers.Main) {
                        adapter.updateSchedules(schedules)
                    }
                }
                Toast.makeText(this, "Đã xóa lịch trình", Toast.LENGTH_SHORT).show()
            }
            // Nếu người dùng Hủy
            builder.setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
            true
        }
    }

    // Chuyển dữ liệu
    private fun updateSchedule(){
        lvListSchedule.setOnItemClickListener { _, _, position, _ ->
            val selectedSchedule = adapter.getItem(position) as tb_schedule
            val intent = Intent(this, UpdateScheduleActivity::class.java)
            intent.putExtra("id", selectedSchedule.id)
            intent.putExtra("category", selectedSchedule.category)
            intent.putExtra("title", selectedSchedule.title)
            intent.putExtra("description", selectedSchedule.description)
            intent.putExtra("date", selectedSchedule.date)
            intent.putExtra("time", selectedSchedule.time)
            intent.putExtra("image", selectedSchedule.image)
            startActivity(intent)
        }
    }
}

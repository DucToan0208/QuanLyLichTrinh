package com.example.nhom12_quanlylichtrinhcanhan

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeActivity : AppCompatActivity() {

    private lateinit var imgSchedule: ImageView
    private lateinit var imgStatistic: ImageView
    private lateinit var imgUser: ImageView
    private lateinit var imgDate: ImageView
    private lateinit var imgTime: ImageView
    private lateinit var lvDanhSach: ListView
    private lateinit var spinnerCategory: Spinner
    private lateinit var adapter: HomeAdapter

    private lateinit var db: CreateDatabaseSchedule
    private lateinit var scheduleDAO: ScheduleDAO

    private lateinit var categoryAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        setControl()
        setEvent()
    }

    private fun setControl(){
        imgSchedule = findViewById(R.id.imgSchedule)
        imgStatistic = findViewById(R.id.imgStatistic)
        imgDate = findViewById(R.id.imgDate)
        imgTime = findViewById(R.id.imgTime)
        imgUser = findViewById(R.id.imgUser)
        lvDanhSach = findViewById(R.id.lvDanhSach)
        spinnerCategory = findViewById(R.id.spinnerCategory)
    }

    private fun setEvent(){
        imgSchedule.setOnClickListener {
            val intent = Intent(this, ListScheduleActivity::class.java)
            startActivity(intent)
        }

        imgStatistic.setOnClickListener {
            val intent = Intent(this, StatisticActivity::class.java)
            loadSchedules()
            startActivity(intent)
        }

        imgUser.setOnClickListener {
            val intent = Intent(this, UserActivity::class.java)

            startActivity(intent)
        }

        imgDate.setOnClickListener {
            viewDate()
        }

        imgTime.setOnClickListener {
            viewTime()
        }

        detailSchedule()

        val categories = listOf("Tất cả", "Công việc", "Giải trí", "Sức khỏe", "Gia đình") // Các danh mục bạn muốn hiển thị
        categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = categoryAdapter.getItem(position) ?: ""
                getCategory(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        initDatabase()

        loadSchedules()
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

            // Lấy ngày hôm qua, hôm nay và ngày mai
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)
            val tomorrow = today.plusDays(1)

            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

            // Lọc các lịch trình theo ngày hôm qua, hôm nay và ngày mai
            val filteredSchedules = schedules.filter { schedule ->
                try {
                    val scheduleDate = LocalDate.parse(schedule.date, formatter)

                    scheduleDate == yesterday || scheduleDate == today || scheduleDate == tomorrow
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }

            filteredSchedules.sortedByDescending { shedule ->
                LocalDate.parse(shedule.date, formatter)
            }

            // Cập nhật danh sách lịch trình sau khi lọc
            runOnUiThread {
                adapter = HomeAdapter(this@HomeActivity, filteredSchedules.toMutableList())
                lvDanhSach.adapter = adapter
            }
        }
    }

    // Xem ngày
    fun viewDate() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            null,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // Xem giờ
    fun viewTime() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            this,
            null,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    // Lấy theo Danh mục
    private fun getCategory(category: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val schedules = if (category == "Tất cả") {
                scheduleDAO.getAllScheduleASC()
            } else {
                scheduleDAO.getScheduleByCategory(category)
            }

            val today = LocalDate.now()
            val yesterday = today.minusDays(1)
            val tomorrow = today.plusDays(1)

            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

            // Lọc các lịch trình theo ngày hôm qua, hôm nay và ngày mai
            val filteredSchedules = schedules.filter { schedule ->
                try {
                    val scheduleDate = LocalDate.parse(schedule.date, formatter)

                    scheduleDate == yesterday || scheduleDate == today || scheduleDate == tomorrow
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }

            filteredSchedules.sortedByDescending { schedule ->
                LocalDate.parse(schedule.date, formatter)
            }

            runOnUiThread {
                adapter = HomeAdapter(this@HomeActivity, filteredSchedules.toMutableList())
                lvDanhSach.adapter = adapter
            }
        }
    }

    private fun detailSchedule(){
        lvDanhSach.setOnItemClickListener { _, _, position, _ ->
            val selectedSchedule = adapter.getItem(position) as tb_schedule
            val intent = Intent(this, DetailScheduleActivity::class.java)
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
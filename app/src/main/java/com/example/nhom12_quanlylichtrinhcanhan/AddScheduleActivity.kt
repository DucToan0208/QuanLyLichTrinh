package com.example.nhom12_quanlylichtrinhcanhan

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddScheduleActivity : AppCompatActivity() {

    private lateinit var imgExit: ImageView
    private lateinit var imgSave: ImageView
    private lateinit var edtTitle: EditText
    private lateinit var edtDescription: EditText
    private lateinit var tvDate: TextView
    private lateinit var imgDate: ImageView
    private lateinit var tvTime: TextView
    private lateinit var imgTime: ImageView
    private lateinit var imgImage: ImageView
    private lateinit var adapter: ScheduleAdapter
    private var imagePath: String? = null
    private var selectedDate: String? = null
    private lateinit var spinnerCategory: Spinner
    private var selectedCategory: String? = null

    private lateinit var db: CreateDatabaseSchedule
    private lateinit var scheduleDAO: ScheduleDAO
    private var selectedSchedule: tb_schedule? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_schedule)

        setControl()

        setEvent()
    }

    private fun setControl() {
        imgExit = findViewById(R.id.imgExit)
        imgSave = findViewById(R.id.imgSave)
        edtTitle = findViewById(R.id.edtTitle)
        edtDescription = findViewById(R.id.edtDescription)
        tvDate = findViewById(R.id.tvDate)
        imgDate = findViewById(R.id.imgDate)
        tvTime = findViewById(R.id.tvTime)
        imgTime = findViewById(R.id.imgTime)
        imgImage = findViewById(R.id.imgImage)
        spinnerCategory = findViewById(R.id.spinnerCategory)
    }

    private fun setEvent() {
        imgExit.setOnClickListener {
            finish()
        }

        imgDate.setOnClickListener {
            getDate()
        }

        imgTime.setOnClickListener {
            getTime()
        }

        imgImage.setOnClickListener {
            getImage()
        }

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCategory = parent.getItemAtPosition(position).toString()
                if (selectedCategory == "Chọn danh mục") {
                    selectedCategory = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedCategory = null
            }
        }

        imgSave.setOnClickListener {
            initDatabase()
            saveSchedule()
        }
    }

    // Kết nối database
    private fun initDatabase() {
        db = Room.databaseBuilder(
            applicationContext,
            CreateDatabaseSchedule::class.java, "schedule_database"
        ).build()
        scheduleDAO = db.scheduleDAO()
    }

    // Lấy ngày bằng Calender
    private fun getDate() {
        // Khai baos Calender
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Hiển thị DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Cập nhật TextView với ngày đã chọn
                selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                tvDate.text = selectedDate
            }, year, month, day
        )
        // Không cho chọn ngày trong quá khứ
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    // Lấy giờ bằng Calender
    private fun getTime() {
        if (!today(selectedDate!!)) {
            // Nếu ngày không phải hôm nay, cho phép chọn giờ bình thường
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    // Cập nhật TextView với thời gian đã chọn
                    val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    tvTime.text = selectedTime
                },
                hour, minute, true
            )
            timePickerDialog.show()
        } else {
            // Nếu là hôm nay, chỉ cho phép chọn giờ từ giờ hiện tại trở đi
            val currentCalendar = Calendar.getInstance()
            val currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = currentCalendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    if (selectedHour < currentHour || (selectedHour == currentHour && selectedMinute < currentMinute)) {
                        Toast.makeText(this, "Không thể chọn giờ đã qua!", Toast.LENGTH_SHORT).show()
                    } else {
                        val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                        tvTime.text = selectedTime
                    }
                },
                currentHour, currentMinute, true
            )
            timePickerDialog.show()
        }
    }

    // Kiểm tra xem ngày đã chọn có phải hôm nay không
    private fun today(selectedDate: String): Boolean {
        val calendar = Calendar.getInstance()
        val todayYear = calendar.get(Calendar.YEAR)
        val todayMonth = calendar.get(Calendar.MONTH)
        val todayDay = calendar.get(Calendar.DAY_OF_MONTH)

        val dateParts = selectedDate.split("/")
        val selectedDay = dateParts[0].toInt()
        val selectedMonth = dateParts[1].toInt() - 1
        val selectedYear = dateParts[2].toInt()

        return todayYear == selectedYear && todayMonth == selectedMonth && todayDay == selectedDay
    }

    // Lấy ảnh
    private fun getImage() {
        val images = listOf(
            R.drawable.chinhtri,
            R.drawable.hocbai,
            R.drawable.theduc,
            R.drawable.bannel,
            R.drawable.img_baner
        )
        val gridView = GridView(this)
        gridView.numColumns = 2
        gridView.adapter = ImageAdapter(this, images)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Chọn ảnh")
        builder.setView(gridView)

        gridView.setOnItemClickListener { _, _, position, _ ->
            val selectedImageRes = images[position]

            imgImage.setImageResource(selectedImageRes)

            imagePath = resources.getResourceEntryName(selectedImageRes)
        }
        builder.show()
    }

    // Lưu lịch trình
    private fun saveSchedule() {
        val title = edtTitle.text.toString()
        val description = edtDescription.text.toString()
        val date = tvDate.text.toString()
        val time = tvTime.text.toString()
        val img = imagePath

        if (title.isBlank() || description.isBlank() || date.isBlank() || time.isBlank() || selectedCategory.isNullOrBlank() || selectedCategory == "Chọn danh mục") {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin và chọn danh mục hợp lệ!", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val listSchedules = scheduleDAO.getAllSchedule()
                val isTimeConflict = listSchedules.any { schedule ->
                    schedule.date == date && schedule.time == time
                }
                withContext(Dispatchers.Main) {
                    if (isTimeConflict) {
                        Toast.makeText(this@AddScheduleActivity, "Thời gian này đã tồn tại!", Toast.LENGTH_SHORT).show()
                    } else {
                        val newSchedule = tb_schedule(
                            id = 0,
                            category = selectedCategory!!,
                            title = title,
                            description = description,
                            date = date,
                            time = time,
                            image = img
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            scheduleDAO.insertSchedule(newSchedule)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@AddScheduleActivity,
                                    "Thêm thành công!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                CoroutineScope(Dispatchers.IO).launch {
                                    val schedule = scheduleDAO.getAllSchedule()
                                    withContext(Dispatchers.Main) {
                                        adapter.updateSchedules(schedule)
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddScheduleActivity, "Lỗi khi thêm lịch trình: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
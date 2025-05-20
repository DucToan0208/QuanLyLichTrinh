package com.example.nhom12_quanlylichtrinhcanhan

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

class UpdateScheduleActivity : AppCompatActivity() {

    private lateinit var imgExit: ImageView
    private lateinit var imgSave: ImageView
    private lateinit var edtTitle: EditText
    private lateinit var edtDescription: EditText
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView
    private lateinit var imgImage: ImageView
    private lateinit var imgDate: ImageView
    private lateinit var imgTime: ImageView
    private lateinit var spinnerCategory: Spinner
    private lateinit var adapter: ScheduleAdapter
    private var imagePath: String? = null
    private var selectedDate: String? = null
    private var selectedCategory: String? = null

    private lateinit var db: CreateDatabaseSchedule
    private lateinit var scheduleDAO: ScheduleDAO
    private var selectedSchedule: tb_schedule? = null

    private lateinit var categoryAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_schedule)

        setControl()

        setEvent()
    }

    private fun setControl(){
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

    private fun setEvent(){
        imgExit.setOnClickListener {
            finish()
        }

        getDataIntent()

        imgDate.setOnClickListener {
            getDate()
        }

        imgTime.setOnClickListener {
            getTime()
        }

        imgImage.setOnClickListener {
            getImage()
        }

        imgSave.setOnClickListener {
            initDatabase()
            saveSchedule()
        }

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = categoryAdapter.getItem(position) ?: ""
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
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

    // Dữ liệu từ intent qua
    private fun getDataIntent() {
        val id = intent.getIntExtra("id", -1)
        val category = intent.getStringExtra("category") ?: ""
        val title = intent.getStringExtra("title") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val date = intent.getStringExtra("date") ?: ""
        val time = intent.getStringExtra("time") ?: ""
        val image = intent.getStringExtra("image") ?: ""

        // Lưu ID
        selectedSchedule = tb_schedule(id, category,title, description, date, time, image)

        // Hiển thị thông tin
        val categories = listOf("Chọn danh mục", "Công việc", "Giải trí", "Sức khỏe", "Gia đình")
        categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        val categoryPosition = categories.indexOf(category)
        if (categoryPosition >= 0) {
            spinnerCategory.setSelection(categoryPosition)
        } else {
            spinnerCategory.setSelection(0)
        }

        edtTitle.setText(title)
        edtDescription.setText(description)
        tvDate.text = date
        tvTime.text = time

        // Nếu có ảnh, hiển thị ảnh
        if (!image.isNullOrBlank()) {
            val showImage = resources.getIdentifier(image, "drawable", packageName)
            imgImage.setImageResource(showImage)
        }
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
        if (!isToday(selectedDate!!)) {
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
                        // Cập nhật TextView với thời gian đã chọn
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
    private fun isToday(selectedDate: String): Boolean {
        val calendar = Calendar.getInstance()
        val todayYear = calendar.get(Calendar.YEAR)
        val todayMonth = calendar.get(Calendar.MONTH)
        val todayDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Tách ngày được chọn
        val dateParts = selectedDate.split("/")
        val selectedDay = dateParts[0].toInt()
        val selectedMonth = dateParts[1].toInt() - 1
        val selectedYear = dateParts[2].toInt()

        return todayYear == selectedYear && todayMonth == selectedMonth && todayDay == selectedDay
    }

    // Lấy ảnh
    private fun getImage() {
        // Danh sách các ảnh từ drawable
        val images = listOf(
            R.drawable.chinhtri,
            R.drawable.hocbai,
            R.drawable.theduc,
            R.drawable.bannel,
            R.drawable.img_baner
        )

        // Tạo một GridView để hiển thị ảnh
        val gridView = GridView(this)
        gridView.numColumns = 2
        gridView.adapter = ImageAdapter(this, images)

        // Tạo một dialog để hiển thị ảnh
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Chọn ảnh")
        builder.setView(gridView)

        // Khi người dùng chọn ảnh từ GridView
        gridView.setOnItemClickListener { _, _, position, _ ->
            val selectedImageRes = images[position]

            // Hiển thị ảnh trong ImageView
            imgImage.setImageResource(selectedImageRes)

            // Lưu tên của ảnh
            imagePath = resources.getResourceEntryName(selectedImageRes)
        }
        builder.show()
    }

    // Lưu vào list
    private fun saveSchedule() {
        val title = edtTitle.text.toString()
        val description = edtDescription.text.toString()
        val date = tvDate.text.toString()
        val time = tvTime.text.toString()
        val img = imagePath ?: selectedSchedule?.image

        if (title.isBlank() || description.isBlank() || date.isBlank() || time.isBlank() || selectedCategory.isNullOrBlank() || selectedCategory == "Chọn danh mục") {
            Toast.makeText(this, "Chưa đầy đủ thông tin!!!", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val getAllSchedule = scheduleDAO.getAllSchedule()
            val equalTime = getAllSchedule.any { schedule ->
                schedule.time == time && schedule.date == date && schedule.id != selectedSchedule?.id
            }

            withContext(Dispatchers.Main) {
                if (equalTime) {
                    Toast.makeText(this@UpdateScheduleActivity,"Thời gian này đã tồn tại", Toast.LENGTH_SHORT).show()
                } else {
                    try {
                        CoroutineScope(Dispatchers.IO).launch {
                            selectedSchedule?.let {
                                // Cập nhật dữ liệu
                                val updatedSchedule = tb_schedule(id = it.id, category = selectedCategory!!, title = title, description = description, date = date, time = time, image = img)
                                scheduleDAO.updateSchedule(updatedSchedule)
                            }
                        }
                        Toast.makeText(this@UpdateScheduleActivity, "Sửa thành công", Toast.LENGTH_SHORT).show()
                        CoroutineScope(Dispatchers.IO).launch {
                            val schedule = scheduleDAO.getAllSchedule()
                            withContext(Dispatchers.Main) {
                                adapter.updateSchedules(schedule)
                            }
                        }
                    } catch (ex: Exception) {
                        Toast.makeText(this@UpdateScheduleActivity, "Lỗi!!!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
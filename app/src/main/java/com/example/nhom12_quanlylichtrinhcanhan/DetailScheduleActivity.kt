package com.example.nhom12_quanlylichtrinhcanhan

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room

class DetailScheduleActivity : AppCompatActivity() {

    private lateinit var imgExit: ImageView
    private lateinit var imgImage: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvCategory: TextView

    private var selectedSchedule: tb_schedule? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_schedule)

        setControl()

        setEvent()
    }

    private fun setControl(){
        imgExit = findViewById(R.id.imgExit)
        tvCategory = findViewById(R.id.tvCategory)
        tvTitle = findViewById(R.id.tvTitle)
        tvDescription = findViewById(R.id.tvDescription)
        tvDate = findViewById(R.id.tvDate)
        tvTime = findViewById(R.id.tvTime)
        imgImage = findViewById(R.id.imgImage)
    }

    private fun setEvent(){
        imgExit.setOnClickListener {
            finish()
        }
        getDataIntent()
    }

    private fun getDataIntent() {
        val id = intent.getIntExtra("id", -1)
        val category = intent.getStringExtra("category") ?: ""
        val title = intent.getStringExtra("title") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val date = intent.getStringExtra("date") ?: ""
        val time = intent.getStringExtra("time") ?: ""
        val image = intent.getStringExtra("image") ?: ""

        // Lưu ID
        selectedSchedule = tb_schedule(id, title, description, date, time, image)

        // Hiển thị thông tin
        tvCategory.setText(category)
        tvTitle.setText(title)
        tvDescription.setText(description)
        tvDate.text = date
        tvTime.text = time

        // Nếu có ảnh, hiển thị ảnh
        if (!image.isNullOrBlank()) {
            val showImage = resources.getIdentifier(image, "drawable", packageName)
            imgImage.setImageResource(showImage)
        }
    }
}
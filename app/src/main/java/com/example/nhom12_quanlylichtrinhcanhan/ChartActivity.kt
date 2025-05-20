package com.example.nhom12_quanlylichtrinhcanhan

import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.nhom12_quanlylichtrinhcanhan.databinding.ActivityChartBinding

class ChartActivity : AppCompatActivity() {

    private lateinit var imgExit: ImageView

    private var _binding: ActivityChartBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val counts = intent.getFloatArrayExtra("COUNTS") ?: floatArrayOf()
        val startOfWeek = intent.getStringExtra("START_DATE") ?: "Ngày không xác định"
        val endOfWeek = intent.getStringExtra("END_DATE") ?: "Ngày không xác định"

        binding.tvWeek.text = "Từ $startOfWeek ... $endOfWeek"

        val daysOfWeek = listOf(
            "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"
        )

        val duLieu = daysOfWeek.zip(counts.toList())

        setupLeftColumn(counts)

        binding.apply {
            barChart.animate(duLieu)
        }

        setControl()
        setEvent()
    }

    private fun setControl() {
        imgExit = findViewById(R.id.imgExit)
    }

    private fun setEvent() {
        imgExit.setOnClickListener {
            finish()
        }
    }

    private fun setupLeftColumn(counts: FloatArray) {
        val maxValue = counts.maxOrNull()?.toInt() ?: 0
        if (maxValue <= 0) return

        val fValuesColumn = findViewById<LinearLayout>(R.id.fValuesColumn)
        fValuesColumn.removeAllViews()

        for (i in maxValue downTo 0) {
            val textView = TextView(this).apply {
                text = i.toString()
                textSize = 12f
                gravity = Gravity.END
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0,
                1f
            )
            textView.layoutParams = params
            fValuesColumn.addView(textView)
        }
    }
}

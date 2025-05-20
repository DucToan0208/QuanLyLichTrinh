package com.example.nhom12_quanlylichtrinhcanhan

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.content.Intent
import android.widget.ImageView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class StatisticActivity : AppCompatActivity() {

    // Khai báo biến
    private lateinit var imgExit: ImageView
    private lateinit var imgChart: ImageView
    private lateinit var imgPrev: ImageView
    private lateinit var imgNext: ImageView
    private lateinit var lvStatistic: ListView
    private lateinit var adapter: StatisticAdapter
    private var currentWeek = 0

    private lateinit var db: CreateDatabaseSchedule
    private lateinit var scheduleDAO: ScheduleDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_statistic)

        setControl()

        setEvent()
    }

    private fun setControl(){
        imgExit = findViewById(R.id.imgExit)
        imgChart = findViewById(R.id.imgChart)
        imgPrev = findViewById(R.id.imgPrev)
        imgNext = findViewById(R.id.imgNext)
        lvStatistic = findViewById(R.id.lvStatistic)
    }

    private fun setEvent(){
        initDatabase()
        loadSchedules()

        imgExit.setOnClickListener {
            finish()
        }

        imgNext.setOnClickListener {
            currentWeek ++
            loadSchedules()
        }

        imgPrev.setOnClickListener {
            currentWeek --
            loadSchedules()
        }
        imgChart.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val schedules = scheduleDAO.getAllSchedule()

                val getWeekDate = getWeek(currentWeek)
                val startDate = getWeekDate.first().date
                val endDate = getWeekDate.last().date

                val counts = getWeekDate.map { day ->
                    schedules.count { it.date == day.date }
                }

                runOnUiThread {
                    val intent = Intent(this@StatisticActivity, ChartActivity::class.java)
                    intent.putExtra("COUNTS", counts.map { it.toFloat() }.toFloatArray())
                    intent.putExtra("START_DATE", startDate)
                    intent.putExtra("END_DATE", endDate)
                    startActivity(intent)
                }
            }
        }
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
            try {
                val schedules = scheduleDAO.getAllSchedule()

                val getWeekDate = getWeek(currentWeek)

                val daySchedule = getWeekDate.map { getDay ->
                    val matchingSchedules = schedules.filter { it.date == getDay.date }

                    if (matchingSchedules.isNotEmpty()) {
                        val groupDescription = matchingSchedules.joinToString("\n") { it.description }
                        val groupTime = matchingSchedules.joinToString("\n") { it.time }

                        getDay.copy(
                            description = groupDescription,
                            time = groupTime
                        )
                    } else {
                        getDay
                    }
                }
                runOnUiThread {
                    adapter = StatisticAdapter(this@StatisticActivity, daySchedule.toMutableList())
                    lvStatistic.adapter = adapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    adapter = StatisticAdapter(this@StatisticActivity, mutableListOf())
                    lvStatistic.adapter = adapter
                }
            }
        }
    }

    private fun getWeek(weekOffset: Int): List<tb_schedule> {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY

        calendar.add(Calendar.WEEK_OF_YEAR, weekOffset)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val listSchedule = mutableListOf<tb_schedule>()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        for (i in 0..6) {
            val date = dateFormat.format(calendar.time)
            listSchedule.add(
                tb_schedule(
                    id = 0,
                    category = "",
                    title = "",
                    description = "",
                    date = date,
                    time = ""
                )
            )
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return listSchedule
    }
}

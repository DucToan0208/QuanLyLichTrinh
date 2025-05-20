package com.example.nhom12_quanlylichtrinhcanhan

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.util.Date
import java.util.Locale

class StatisticAdapter(
    context: Context,
    private var statisticList: MutableList<tb_schedule>
) : ArrayAdapter<tb_schedule>(context, 0, statisticList) {

    private val currentDate: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.statistic_adapter, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val schedule = statisticList[position]

        // Gán dữ liệu vào các TextView
        holder.tvDescription.text = schedule.description
        holder.tvDate.text = schedule.date
        holder.tvTime.text = schedule.time

        // Kiểm tra xem ngày hôm nay có trùng với ngày trong danh sách không
        if (schedule.date == currentDate) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.holo_pink_light))
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
        }

        // Lấy thứ trong tuần từ ngày
        val dayOfWeek = getDayOfWeek(schedule.date)
        holder.tvThu.text = dayOfWeek

        return view
    }

    private class ViewHolder(view: View) {
        val tvThu: TextView = view.findViewById(R.id.tvThu)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
    }

    fun updateSchedules(newSchedules: List<tb_schedule>) {
        with(statisticList) {
            clear()
            addAll(newSchedules)
        }
        notifyDataSetChanged()
    }

    // Lấy thứ trong tuần từ ngày
    private fun getDayOfWeek(dateString: String): String {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = try {
            format.parse(dateString)
        } catch (e: Exception) {
            // Nếu không parse được ngày thì trả về thông báo lỗi
            return "Thứ không hợp lệ"
        }

        if (date == null) {
            return "Ngày không hợp lệ"
        }

        val calendar = Calendar.getInstance()
        calendar.time = date
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        return when (dayOfWeek) {
            Calendar.MONDAY -> "Thứ 2"
            Calendar.TUESDAY -> "Thứ 3"
            Calendar.WEDNESDAY -> "Thứ 4"
            Calendar.THURSDAY -> "Thứ 5"
            Calendar.FRIDAY -> "Thứ 6"
            Calendar.SATURDAY -> "Thứ 7"
            Calendar.SUNDAY -> "Chủ Nhật"
            else -> "Không xác định"
        }
    }
}

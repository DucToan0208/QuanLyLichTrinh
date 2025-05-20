package com.example.nhom12_quanlylichtrinhcanhan

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeAdapter(
    context: Context,
    private var homeList: MutableList<tb_schedule>
) : ArrayAdapter<tb_schedule>(context, 0, homeList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.home_adapter, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val schedule = homeList[position]

        // Format ngày theo kiểu dd/MM/yyyy
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val scheduleDate = LocalDate.parse(schedule.date, formatter)
        val today = LocalDate.now()
        val tomorrow = today.plusDays(7)
        val yesterday = today.minusDays(7)

        // Gán dữ liệu
        holder.tvDescription.text = schedule.description
        holder.tvDate.text = schedule.date
        holder.tvTime.text = schedule.time

        // Gán màu cho TextView nếu là ngày hôm nay, hôm qua và ngày mai
        if (scheduleDate == today) {
            view.setBackgroundColor(Color.parseColor("#A8E6A1"))
        }
        if(scheduleDate == tomorrow){
            view.setBackgroundColor(Color.parseColor("#A1C6E6"))
        }
        if(scheduleDate == yesterday) {
            view.setBackgroundColor(Color.parseColor("#F5F5F5"))
        }

        // Gán hình ảnh
        if (schedule.image != null) {
            val resourceId = context.resources.getIdentifier(schedule.image, "drawable", context.packageName)
            if (resourceId != 0) {
                holder.imgImage.setImageResource(resourceId)
            } else {
                holder.imgImage.setImageResource(R.drawable.ic_launcher_background)
            }
        } else {
            holder.imgImage.setImageResource(R.drawable.ic_launcher_background)
        }
        return view
    }

    private class ViewHolder(view: View) {
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val imgImage: ImageView = view.findViewById(R.id.imgImage)
    }

    fun updateSchedules(newSchedules: List<tb_schedule>) {
        with(homeList) {
            clear()
            addAll(newSchedules)
        }
        notifyDataSetChanged()
    }
}


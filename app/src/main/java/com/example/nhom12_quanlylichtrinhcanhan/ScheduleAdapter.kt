package com.example.nhom12_quanlylichtrinhcanhan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class ScheduleAdapter(
    context: Context,
    private var scheduleList: MutableList<tb_schedule>
):ArrayAdapter<tb_schedule>(context, 0, scheduleList){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.schedule_adapter, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }
        val schedule = scheduleList[position]

        holder.tvCategory.text = schedule.category
        holder.tvTitle.text = schedule.title
        holder.tvDescription.text = schedule.description
        holder.tvDate.text = schedule.date
        holder.tvTime.text = schedule.time

        if (schedule.image != null) {
            // Lấy ID từ tên của ảnh trong drawable
            val resourceId = context.resources.getIdentifier(schedule.image, "drawable", context.packageName)
            if (resourceId != 0) {
                holder.imgImage.setImageResource(resourceId)
            } else {
                // Hình ảnh mặc định nếu không tìm thấy
                holder.imgImage.setImageResource(R.drawable.ic_launcher_background)
            }
        } else {
            holder.imgImage.setImageResource(R.drawable.ic_launcher_background)
        }
        return view
    }

    private class ViewHolder(view: View){
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvTitle:TextView = view.findViewById(R.id.tvTitle)
        val tvDescription:TextView = view.findViewById(R.id.tvDescription)
        val tvDate:TextView = view.findViewById(R.id.tvDate)
        val tvTime:TextView = view.findViewById(R.id.tvTime)
        val imgImage:ImageView = view.findViewById(R.id.imgImage)
    }

    fun updateSchedules(newSchedules: List<tb_schedule>){
        with(scheduleList){
            clear()
            addAll(newSchedules)
        }
        notifyDataSetChanged()
    }
}
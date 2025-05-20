package com.example.nhom12_quanlylichtrinhcanhan

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UserActivity : AppCompatActivity() {

    private lateinit var imgExit: ImageView
    private lateinit var imgUpdate: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvSDT: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPassword: TextView
    private lateinit var btnLogout: Button

    private lateinit var db: CreateDatabaseUser
    private lateinit var userDAO: UserDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user)

        setControl()

        setEvent()
    }

    private fun setControl(){
        imgExit = findViewById(R.id.imgExit)
        imgUpdate = findViewById(R.id.imgUpdate)
        tvName = findViewById(R.id.tvName)
        tvSDT = findViewById(R.id.tvSdt)
        tvEmail = findViewById(R.id.tvEmail)
        tvPassword = findViewById(R.id.tvPassword)
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun setEvent(){
        imgExit.setOnClickListener {
            finish()
        }

        imgUpdate.setOnClickListener {
            inputPassword()
        }

        initDatabase()

        loadUser()

        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initDatabase() {
        db = Room.databaseBuilder(
            applicationContext,
            CreateDatabaseUser::class.java, "user_database"
        ).build()
        userDAO = db.userDAO()
    }

    private fun loadUser(){
        CoroutineScope(Dispatchers.IO).launch {
            val userDAO = userDAO.getAllUser()
            if(userDAO.isNotEmpty()){
                val user = userDAO.first()

                withContext(Dispatchers.Main){
                    tvName.text = user.name
                    tvSDT.text = user.sdt
                    tvEmail.text = user.username
                    tvPassword.text = "*".repeat(user.password.length)
                }
            }else{
                withContext(Dispatchers.Main) {
                    tvName.text = "Không có người dùng"
                    tvSDT.text = "Không có số điện thoại"
                    tvEmail.text = "Không có email"
                    tvPassword.text = "Không có mật khẩu"
                }
            }
        }
    }

    private fun inputPassword(){
        val builder = AlertDialog.Builder(this)
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setTitle("Nhập mật khẩu để sửa thông tin")
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, which ->
            val password = input.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                val user = userDAO.getAllUser().firstOrNull()

                if (user != null) {
                    if (password == user.password) {
                        withContext(Dispatchers.Main) {
                            val intent = Intent(this@UserActivity, UpdateUserActivity::class.java)
                            intent.putExtra("id", user.id)
                            intent.putExtra("name", user.name)
                            intent.putExtra("sdt", user.sdt)
                            intent.putExtra("email", user.username)
                            intent.putExtra("password", user.password)
                            startActivity(intent)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@UserActivity, "Mật khẩu sai. Vui lòng thử lại!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@UserActivity, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        builder.setNegativeButton("Hủy") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }
}
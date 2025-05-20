package com.example.nhom12_quanlylichtrinhcanhan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var btnLogin: Button
    private lateinit var tvDangKyNgay: TextView
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var tvQuenMatKhau: TextView

    private lateinit var db: CreateDatabaseUser
    private lateinit var userDAO: UserDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Khởi tạo database


        setControl()
        setEvent()
    }

    private fun setControl() {
        btnLogin = findViewById(R.id.btnLogin)
        tvDangKyNgay = findViewById(R.id.tvDangKyNgay)
        tvQuenMatKhau = findViewById(R.id.tvQuenMatKhau)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
    }

    private fun setEvent() {
        initDatabase()

        btnLogin.setOnClickListener {
            val usernameInput = username.text.toString()
            val passwordInput = password.text.toString()

            // Kiểm tra email hợp lệ cho username
            if (!isValidEmail(usernameInput)) {
                Toast.makeText(this, "Vui lòng nhập địa chỉ email hợp lệ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kiểm tra mật khẩu ít nhất 6 ký tự và không chứa khoảng trắng
            if (!isValidPassword(passwordInput)) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự và không chứa khoảng trắng!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kiểm tra thông tin đăng nhập từ database
            CoroutineScope(Dispatchers.IO).launch {
                val user = userDAO.getAllUser().find {
                    it.username == usernameInput && it.password == passwordInput
                }
                withContext(Dispatchers.Main) {
                    if (user != null) {
                        // Nếu tài khoản hợp lệ, chuyển đến HomeActivity
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Nếu tài khoản không hợp lệ, hiển thị thông báo lỗi
                        Toast.makeText(this@LoginActivity, "Tên đăng nhập hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        tvDangKyNgay.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        tvQuenMatKhau.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }
    }

    // Hàm kiểm tra email hợp lệ
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@(.+)$") // Kiểm tra định dạng email
        return email.matches(emailRegex)
    }

    // Hàm kiểm tra mật khẩu hợp lệ
    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6 && !password.contains(" ") // Mật khẩu phải có ít nhất 6 ký tự và không có khoảng trắng
    }


    private fun initDatabase() {
        db = Room.databaseBuilder(
            applicationContext,
            CreateDatabaseUser::class.java,
            "user_database"
        ).build()
        userDAO = db.userDAO()
    }
}
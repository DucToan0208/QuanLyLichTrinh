package com.example.nhom12_quanlylichtrinhcanhan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Forgot : AppCompatActivity() {

    private lateinit var edtPassword: EditText
    private lateinit var edtPasswordAgain: EditText
    private lateinit var btnForgot: Button
    private lateinit var db: CreateDatabaseUser
    private lateinit var userDAO: UserDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)


        setControl()
        setEvent()
    }

    private fun setControl() {
        edtPassword = findViewById(R.id.password)
        edtPasswordAgain = findViewById(R.id.passwordagain)
        btnForgot = findViewById(R.id.btnForgot)
    }

    private fun setEvent() {
        initDatabase()

        btnForgot.setOnClickListener {
            val password = edtPassword.text.toString()
            val passwordAgain = edtPasswordAgain.text.toString()

            // Kiểm tra tính hợp lệ của mật khẩu
            if (password.isEmpty() || passwordAgain.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != passwordAgain) {
                Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lấy username từ màn hình trước
            val username = intent.getStringExtra("username")

            if (username != null) {
                // Cập nhật mật khẩu trong cơ sở dữ liệu
                CoroutineScope(Dispatchers.IO).launch {
                    val user = userDAO.getUserByUsername(username)
                    if (user != null) {
                        user.password = password // Cập nhật mật khẩu
                        userDAO.updateUser(user) // Lưu lại thay đổi

                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@Forgot, "Đặt lại mật khẩu thành công!", Toast.LENGTH_SHORT).show()

                            // Chuyển đến màn hình đăng nhập
                            val intent = Intent(this@Forgot, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish() // Kết thúc màn hình hiện tại
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@Forgot, "Lỗi: Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Lỗi: Không nhận được tên tài khoản!", Toast.LENGTH_SHORT).show()
            }
        }
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
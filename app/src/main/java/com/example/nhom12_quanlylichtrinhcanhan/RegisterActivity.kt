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

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtSDT: EditText
    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtPasswordAgain: EditText
    private lateinit var btnRegister: Button

    private lateinit var db: CreateDatabaseUser
    private lateinit var userDAO: UserDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initDatabase()

        setControl()

        setEvent()
    }

    private fun initDatabase() {
        db = Room.databaseBuilder(
            applicationContext,
            CreateDatabaseUser::class.java,
            "user_database"
        ).build()
        userDAO = db.userDAO()
    }

    private fun setControl() {
        edtName = findViewById(R.id.edtName)
        edtSDT = findViewById(R.id.edtSDT)
        edtUsername = findViewById(R.id.username)
        edtPassword = findViewById(R.id.password)
        edtPasswordAgain = findViewById(R.id.passwordagain)
        btnRegister = findViewById(R.id.btnRegister)
    }

    private fun setEvent() {
        btnRegister.setOnClickListener {
            val name = edtName.text.toString()
            val sdt = edtSDT.text.toString()
            val username = edtUsername.text.toString()
            val password = edtPassword.text.toString()
            val passwordAgain = edtPasswordAgain.text.toString()

            if (!isValidEmail(username)) {
                Toast.makeText(this, "Vui lòng nhập địa chỉ email hợp lệ!!!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidPassword(password)) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự và không chứa khoảng trắng!!!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidPhoneNumber(sdt)) {
                Toast.makeText(this, "Số điện thoại không hợp lệ!!!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != passwordAgain) {
                Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (name.isEmpty() || sdt.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val existingUser = userDAO.getAllUser().find { it.username == username || it.sdt == sdt}
                withContext(Dispatchers.Main) {
                    if (existingUser != null) {
                        Toast.makeText(this@RegisterActivity, "Tên đăng nhập đã tồn tại!", Toast.LENGTH_SHORT).show()
                    } else {
                        val newUser = tb_user(name = name, sdt = sdt, username = username, password = password)
                        CoroutineScope(Dispatchers.IO).launch {
                            userDAO.insertUser(newUser)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@RegisterActivity, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@(.+)$")
        return email.matches(emailRegex)
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6 && !password.contains(" ")
    }

    fun isValidPhoneNumber(phone: String): Boolean {
        val phoneRegex = "^\\+?[0-9]{10,12}\$".toRegex()
        return phone.matches(phoneRegex)
    }
}
package com.example.nhom12_quanlylichtrinhcanhan

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.*
import java.util.*
import javax.mail.*
import javax.mail.internet.*

class ForgotPassword : AppCompatActivity() {

    private lateinit var edtUsername: EditText
    private lateinit var btnNext: Button
    private lateinit var db: CreateDatabaseUser
    private lateinit var userDAO: UserDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        initDatabase()
        setControl()
        setEvent()
    }

    private fun setControl() {
        edtUsername = findViewById(R.id.username)
        btnNext = findViewById(R.id.btnNext)
    }

    private fun setEvent() {
        btnNext.setOnClickListener {
            val usernameInput = edtUsername.text.toString()

            if (usernameInput.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tài khoản!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kiểm tra tài khoản trong database
            CoroutineScope(Dispatchers.IO).launch {
                val user = userDAO.getAllUser().firstOrNull { it.username == usernameInput }

                withContext(Dispatchers.Main) {
                    if (user != null) {
                        // Nếu tài khoản tồn tại, kiểm tra email
                        val email = user.username

                        // Tạo mã OTP ngẫu nhiên
                        val otp = generateOTP()

                        // Gửi OTP qua email
                        sendEmailUsingAppPassword(email, otp)

                        // Chuyển sang màn hình nhập OTP
                        val intent = Intent(this@ForgotPassword, EnterOtpActivity::class.java)
                        intent.putExtra("username", usernameInput)
                        intent.putExtra("otp", otp)
                        startActivity(intent)
                    } else {
                        // Nếu tài khoản không tồn tại, hiển thị thông báo lỗi
                        Toast.makeText(this@ForgotPassword, "Tài khoản không tồn tại!", Toast.LENGTH_SHORT).show()
                    }
                }
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

    // Hàm tạo mã OTP ngẫu nhiên
    private fun generateOTP(): String {
        val random = (100000..999999).random()
        return random.toString()
    }

    // Hàm gửi email sử dụng mật khẩu ứng dụng Gmail
    private fun sendEmailUsingAppPassword(email: String, otp: String) {
        val subject = "Mã OTP Đổi Mật Khẩu"
        val message = "Mã OTP của bạn là: $otp"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val username = "huytrunglang123@gmail.com"
                val appPassword = "jevw acme xmhc nrbh"

                val props = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.port", "587")
                }

                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(username, appPassword)
                    }
                })

                val mimeMessage = MimeMessage(session).apply {
                    setFrom(InternetAddress(username))
                    addRecipient(Message.RecipientType.TO, InternetAddress(email))
                    this.subject = subject
                    setText(message)
                }

                Transport.send(mimeMessage)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ForgotPassword, "Mã OTP đã được gửi đến email của bạn", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ForgotPassword, "Gửi email thất bại: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

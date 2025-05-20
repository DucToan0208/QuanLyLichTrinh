package com.example.nhom12_quanlylichtrinhcanhan

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class EnterOtpActivity : AppCompatActivity() {

    private lateinit var edtOtp: EditText
    private lateinit var btnVerify: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_otp)

        edtOtp = findViewById(R.id.edtOtp)
        btnVerify = findViewById(R.id.btnVerify)

        val username = intent.getStringExtra("username")
        val sentOtp = intent.getStringExtra("otp")

        btnVerify.setOnClickListener {
            val enteredOtp = edtOtp.text.toString()

            if (enteredOtp.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập OTP!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (enteredOtp == sentOtp) {
                val intent = Intent(this, Forgot::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
            } else {
                Toast.makeText(this, "OTP không đúng!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

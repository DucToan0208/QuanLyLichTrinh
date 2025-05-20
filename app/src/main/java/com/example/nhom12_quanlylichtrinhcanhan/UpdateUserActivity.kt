package com.example.nhom12_quanlylichtrinhcanhan

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateUserActivity : AppCompatActivity() {

    private lateinit var imgExit: ImageView
    private lateinit var imgSave: ImageView
    private lateinit var edtName: EditText
    private lateinit var edtSDT: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText

    private lateinit var db: CreateDatabaseUser
    private lateinit var userDAO: UserDAO
    private var selectUser: tb_user? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_user)

        setControl()

        setEvent()
    }

    private fun setControl() {
        imgExit = findViewById(R.id.imgExit)
        imgSave = findViewById(R.id.imgSave)
        edtName = findViewById(R.id.edtName)
        edtSDT = findViewById(R.id.edtSDT)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
    }

    private fun setEvent() {
        initDatabase()

        getDataIntent()

        imgExit.setOnClickListener {
            finish()
        }

        imgSave.setOnClickListener {
            saveUser()
        }
    }

    private fun initDatabase() {
        db = Room.databaseBuilder(
            applicationContext,
            CreateDatabaseUser::class.java, "user_database"
        ).build()
        userDAO = db.userDAO()
    }

    private fun getDataIntent() {
        val id = intent.getIntExtra("id", -1)
        val name = intent.getStringExtra("name") ?: ""
        val sdt = intent.getStringExtra("sdt") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val password = intent.getStringExtra("password") ?: ""

        selectUser = tb_user(id, name, sdt, email, password)

        edtName.setText(name)
        edtSDT.setText(sdt)
        edtEmail.setText(email)
        edtPassword.setText(password)
    }

    private fun saveUser() {
        val name = edtName.text.toString()
        val sdt = edtSDT.text.toString()
        val email = edtEmail.text.toString()
        val password = edtPassword.text.toString()

        if (name.isBlank() || sdt.isBlank() || email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Chưa đầy đủ thông tin!!!", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                GlobalScope.launch(Dispatchers.IO) {
                    selectUser?.let {
                        val updateUser = tb_user(
                            id = it.id,
                            name = name,
                            sdt = sdt,
                            username = email,
                            password = password
                        )
                        userDAO.updateUser(updateUser)
                    }
                }
                Toast.makeText(this@UpdateUserActivity, "Sửa thành công", Toast.LENGTH_SHORT).show()
            } catch (ex: Exception) {
                Toast.makeText(this@UpdateUserActivity, "Lỗi!!!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
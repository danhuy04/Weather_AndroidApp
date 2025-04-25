package com.example.weatherapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Khởi tạo Google Sign In Client
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Hiển thị thông tin người dùng
        val user = auth.currentUser
        user?.let {
            binding.etName.setText(it.displayName ?: "Người dùng")
            binding.etEmail.setText(it.email ?: "Không có email")
            it.photoUrl?.let { uri ->
                // Nếu có ảnh đại diện từ Google, bạn có thể load nó ở đây
                // Sử dụng Glide hoặc Picasso để load ảnh
            }
        }

        // Xử lý sự kiện nút quay lại
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Xử lý sự kiện đăng xuất
        binding.btnSignOut.setOnClickListener {
            // Đăng xuất khỏi Firebase
            auth.signOut()
            
            // Đăng xuất khỏi Google
            googleSignInClient.signOut().addOnCompleteListener(this) {
                // Xóa tài khoản Google đã đăng nhập
                googleSignInClient.revokeAccess().addOnCompleteListener {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
} 
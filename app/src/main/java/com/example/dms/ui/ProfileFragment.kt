package com.example.dms.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.dms.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch
import com.example.dms.utils.SessionManager
import com.example.dms.network.RetrofitClient

class ProfileFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var profileName: MaterialTextView
    private lateinit var profileId: MaterialTextView
    private lateinit var profileEmail: TextInputEditText
    private lateinit var profilePhone: TextInputEditText
    private lateinit var changePasswordButton: MaterialButton
    private lateinit var saveProfileButton: MaterialButton

    private val PREFS_NAME = "user_prefs"
    private val KEY_PROFILE_IMAGE = "profile_image"
    private val KEY_TOKEN = "token" // токен хранится в SharedPreferences

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                if (imageUri != null) {
                    grantUriPermission(imageUri)
                    saveProfileImageUri(imageUri)
                    loadImage(imageUri)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        initViews(view)
        loadSavedImage()

        // после загрузки аватара подгружаем профиль с API
        loadUserDataFromApi()

        profileImage.setOnClickListener { openImagePicker() }

        changePasswordButton.setOnClickListener {
            Toast.makeText(requireContext(), "Изменение пароля", Toast.LENGTH_SHORT).show()
        }

        saveProfileButton.setOnClickListener {
            Toast.makeText(requireContext(), "Изменения сохранены", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun initViews(view: View) {
        profileImage = view.findViewById(R.id.profileImage)
        profileName = view.findViewById(R.id.profileName)
        profileId = view.findViewById(R.id.profileId)
        profileEmail = view.findViewById(R.id.profileEmail)
        profilePhone = view.findViewById(R.id.profilePhone)
        changePasswordButton = view.findViewById(R.id.changePasswordButton)
        saveProfileButton = view.findViewById(R.id.saveProfileButton)
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        pickImageLauncher.launch(intent)
    }

    private fun loadImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .circleCrop()
            .into(profileImage)
    }

    private fun saveProfileImageUri(uri: Uri) {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_PROFILE_IMAGE, uri.toString()).apply()
    }

    private fun grantUriPermission(uri: Uri) {
        try {
            requireContext().contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: Exception) {}
    }

    private fun loadSavedImage() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedUri = prefs.getString(KEY_PROFILE_IMAGE, null)
        if (!savedUri.isNullOrEmpty()) {
            loadImage(Uri.parse(savedUri))
        }
    }

    // ✅ Загружаем данные пользователя с API с использованием токена
    private fun loadUserDataFromApi() {
        lifecycleScope.launch {
            try {
                // Получаем токен из SessionManager
                val sessionManager = SessionManager(requireContext())
                val token = sessionManager.getToken() ?: return@launch

                // Создаём ApiService с токеном
                val api = RetrofitClient.getInstance(token)
                val user = api.getProfile() // suspend fun

                // Обновляем UI
                profileName.text = "${user.lastname} ${user.name} ${user.middlename}"
                profileId.text = user.uni_id
                profileEmail.setText(user.email)
                profilePhone.setText(user.phone_number)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

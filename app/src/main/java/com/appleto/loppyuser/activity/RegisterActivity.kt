package com.appleto.loppyuser.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.appleto.loppyuser.R
import com.appleto.loppyuser.helper.Const
import com.appleto.loppyuser.helper.PrefUtils
import com.appleto.loppyuser.helper.Utils
import com.appleto.loppyuser.viewModel.RegisterViewModel
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(p0: View?) {
        when (p0) {
            btnSignUp -> {
                if (!Utils.isEmptyEditText(edtFirstName, "Please enter first name", cardFirstName)
                    && !Utils.isEmptyEditText(edtLastName, "Please enter last name", cardLastName)
                    && !Utils.isEmptyEditText(edtEmail, "Please enter Email", cardEmail)
                    && !Utils.isEmptyEditText(edtPhone, "Please enter phone", cardPhone)
                    && !Utils.isEmptyEditText(edtPassword, "Please enter password", cardPassword)
                    && !Utils.isValidateEmail(edtEmail, "Enter valid email address", cardEmail)
                    && !Utils.isValidatePhone(edtPhone, "Enter valid phone number", cardPhone)
                ) {
                    if (profileImageFile == null) {
                        Utils.showSnackBar(root, "Please select profile image")
                    } else {
                        register()
                    }
                }
            }
            fabAddProfileImage -> {
                ImagePicker.with(this)
                    .crop()                    //Crop image(Optional), Check Customization for more option
                    .compress(1024)            //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        1080,
                        1080
                    )    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start()
            }
        }
    }

    private var profileImageFile: File? = null
    private var viewModel: RegisterViewModel? = null
    private var from: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setSupportActionBar(toolbar)

        from = intent?.extras?.getString(Const.FROM)

        if (from == Const.PROFILE) {
            setProfileData()
        }

        viewModel = ViewModelProvider(
            this
        ).get(RegisterViewModel::class.java)

        viewModel?.response?.observe(this, Observer {
            if (it?.has("message")!!) {
                Toast.makeText(this, it.get("message").asString, Toast.LENGTH_LONG).show()
                startActivity(
                    Intent(
                        this,
                        SuccessActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        })

        btnSignUp.setOnClickListener(this)
        fabAddProfileImage.setOnClickListener(this)

    }

    private fun setProfileData() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
        tvActionBarTitle.text = getString(R.string.title_profile)
        fabAddProfileImage.hide()
        btnTerms.visibility = View.GONE
        btnSignUp.visibility = View.GONE
        btnLogin.visibility = View.GONE
        edtFirstName.isFocusable = false
        edtFirstName.isLongClickable = false
        cardLastName.visibility = View.GONE
        edtEmail.isFocusable = false
        edtEmail.isLongClickable = false
        edtPhone.isFocusable = false
        edtPhone.isLongClickable = false
        cardPassword.visibility = View.GONE
        edtFirstName.setText(PrefUtils.getStringValue(this, Const.NAME))
        edtEmail.setText(PrefUtils.getStringValue(this, Const.EMAIL))
        edtPhone.setText(PrefUtils.getStringValue(this, Const.MOBILE_NO))
        Glide.with(this)
            .load(PrefUtils.getStringValue(this, Const.PROFILE_IMAGE))
            .placeholder(R.drawable.launcher_background)
            .error(R.drawable.launcher_background)
            .into(ivProfileImage)
    }

    private fun register() {
        val sendData = HashMap<String, RequestBody>()
        sendData["email"] =
            RequestBody.create(
                MediaType.parse("text/plain"),
                edtEmail.text.toString().trim()
            )
        sendData["phone"] =
            RequestBody.create(
                MediaType.parse("text/plain"),
                edtPhone.text.toString().trim()
            )
        sendData["password"] =
            RequestBody.create(
                MediaType.parse("text/plain"),
                edtPassword.text.toString().trim()
            )
        sendData["name"] =
            RequestBody.create(
                MediaType.parse("text/plain"),
                edtFirstName.text.toString().trim() + " " + edtLastName.text.toString().trim()
            )
        sendData["user_type"] =
            RequestBody.create(
                MediaType.parse("text/plain"),
                "customer"
            )
        sendData["fcm_token"] =
            RequestBody.create(
                MediaType.parse("text/plain"),
                "123456"
            )

//        val selectedID = rgGender.checkedRadioButtonId
//        val radioButton = findViewById<RadioButton>(selectedID)
//        var gender = "male"
//        if (radioButton.text.toString().trim() == "Male") {
//            gender = "male"
//        } else {
//            gender = "female"
//        }
//        sendData["gender"] =
//            RequestBody.create(
//                MediaType.parse("text/plain"),
//                gender
//            )
        val licenceImage = MultipartBody.Part.createFormData(
            "profile_image",
            profileImageFile?.name,
            RequestBody.create(MediaType.parse("image/*"), profileImageFile)
        )
//        sendData["file\"; filename=\"" + licenceImageFile?.name] =
//            RequestBody.create(
//                MediaType.parse("image/*"),
//                licenceImageFile
//            )

        viewModel?.register(this, sendData, licenceImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data
            ivProfileImage.setImageURI(fileUri)

            //You can get File object from intent
            profileImageFile = ImagePicker.getFile(data)

            //You can also get File Path from intent
            val filePath: String? = ImagePicker.getFilePath(data)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Utils.showSnackBar(root, ImagePicker.getError(data))
        } else {
            Utils.showSnackBar(root, "Task Cancelled")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}

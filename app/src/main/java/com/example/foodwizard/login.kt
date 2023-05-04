package com.example.foodwizard

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import com.example.foodwizard.DB.USER_TYPE
import com.example.foodwizard.DB.User
import com.example.foodwizard.databinding.ActivityLoginBinding
import com.example.foodwizard.viewModel.UsersViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

class login : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding

    private val usersViewModel: UsersViewModel by viewModels()
    var userId : Int = 0 //user id will be used to get the user data from the database
    private lateinit var userType : USER_TYPE //user type will be used to check if the user is admin or not
    private val adminUser = User("admin",ValidationManager.encryption("1234"), userType = USER_TYPE.ADMIN) //userId will be 1
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var database2: DatabaseReference
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //demo for firebase
        val database = Firebase.database
        val myRef1 = database.getReference("TestKey1")
        myRef1.setValue("TestValue1")
        val myRef2 = database.getReference("TestKey2")
        myRef2.setValue("TestValueNow 04/30")

        database2 = Firebase.database.reference
//        database2.child("users").child("John").setValue("changemeplease")
//        database2.child("meals").child("1").setValue("changemeplease")


        database2.child("TestKey1").get().addOnSuccessListener {
            Log.i("firebase read","Got value ${it.value}")
        }.addOnFailureListener{
            Log.e("firebase read", "Error getting data", it)
        }

        // Image Upload/Download in Firebase Storage
        val storage = Firebase.storage
        val storageRef = storage.reference

        val drawableId: Int = resources.getIdentifier("dog", "drawable", packageName)
        // 加载 drawable 图像并将其转换为 InputStream
        val drawable: Drawable = resources.getDrawable(drawableId)
        val bitmap: Bitmap = (drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()

        //Report error in red underline, but it is working!!!
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        // Get Firebase Storage reference

        // create StorageReference storage dir in Google Cloud
        val mountainsRef = storageRef.child("images/dog.jpg")
        val uploadTask = mountainsRef.putBytes(stream.toByteArray())
        uploadTask.addOnFailureListener {
            println("Upload Error")
        }.addOnSuccessListener { taskSnapshot ->
            println("Successfully Uploaded!")
        }

        // download the image from cloud
        // create a StorageReference on target downloading dir
        val imageRef = storageRef.child("images/dog.jpg")

        val localFile = File.createTempFile("dog", "jpg")
        imageRef.getFile(localFile).addOnSuccessListener {
            // Uselocal File
            println("Successfully Downloaded")
        }.addOnFailureListener {
            println("Download error")
        }





        binding= ActivityLoginBinding.inflate(layoutInflater)
        binding.title.text= Html.fromHtml(
            "<font color=${Color.parseColor("#AEFC08")}>L</font>" +
                    "<font color=${Color.parseColor("#06F23A")}>og In</font>")

        GlobalScope.launch(Dispatchers.IO) {
            usersViewModel.addUserIfNotExist(adminUser) //add admin user automatically when the app is installed
        }
        sharedPreferences = getSharedPreferences(R.string.app_name.toString(), MODE_PRIVATE)

        calculateLastLogin() //check if the user had already signed in or not

        binding.back.setOnClickListener {
            finish();
        }
//        binding.login.setOnClickListener {
//            val intent = Intent(this, Record::class.java)
//            startActivity(intent)
//        }
        setContentView(binding.root)
    }

    //----------- check if the user had already signed in for the last hour or not ----------------
    private fun calculateLastLogin(){
        val lastLogin = sharedPreferences.getLong("LAST_LOGIN", -1)
        val lastId = sharedPreferences.getInt("USER_ID", -1)
        if(lastLogin != -1L &&  System.currentTimeMillis() - lastLogin < 3600000 ) //one hour
            goToMainActivityDirectly(lastId) //
        else
            setOnSignInUpListener()
    }

    private fun setOnSignInUpListener() {
        binding.login.setOnClickListener {
            isUserValid()  //check if the user is valid or not
        }
//        sign_up_button.setOnClickListener {
//            onSignUpClick() //go to sign up fragment
//        }
    }

    //---------------------------check if the user validation ---------------------------
    private fun isUserValid() {
        usersViewModel.usersData.observe(this) {
            if (userCheck(it)) {  //if the user is valid then go to main activity
                goToMainActivity()
                binding.account.text.clear()
                binding.password.text.clear()
            } else {
                binding.account.text.clear() //clear the edit text if the user is not valid
                binding.password.text.clear()
            }
        }
    }

    companion object {
        var currentUserId: Int = 0
    }

    private fun userCheck(users: List<User>) : Boolean { // check if the user is valid or not
        var flag = false
        for (user in users) {
            if ((user.userName == binding.account.text.toString()) &&
                ValidationManager.comparePasswordEncrypt(user.password, binding.password.text.toString())) {
                flag = true
                userId = user.id
                currentUserId = user.id
            }
        }
        if (!flag)
            errorMotionToast() //show error toast if the user is not valid
        else
            successMotionToast() //show success toast if the user is valid
        return flag //return true if the user is valid
    }

    private fun goToMainActivityDirectly(lastId: Int) {
        userId = lastId
        userType = if (userId == 1) USER_TYPE.ADMIN else USER_TYPE.USER
        val intent = Intent(this, Main::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("userType", userType)
        startActivity(intent)
    }

    private fun goToMainActivity() { //go to main activity
        sharedPreferences.edit().putLong("LAST_LOGIN", System.currentTimeMillis()).apply()
        sharedPreferences.edit().putInt("USER_ID", userId).apply()
        userType = if (userId == 1) USER_TYPE.ADMIN else USER_TYPE.USER
        val intent = Intent(this, Main::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("userType", userType)
        startActivity(intent)
    }
    //----- Display Motion Toasts successes or fail --------------------------------------
    private fun errorMotionToast(){
        MotionToast.darkColorToast(
            this,
            getString(R.string.try_again),
            getString(R.string.signIn_error),
            MotionToastStyle.ERROR,
            MotionToast.GRAVITY_BOTTOM or MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this,R.font.circular)
        )
    }
    private fun successMotionToast(){
        MotionToast.darkColorToast(
            this,
            getString(R.string.success),
            getString(R.string.signIn_success),
            MotionToastStyle.SUCCESS,
            MotionToast.GRAVITY_BOTTOM or MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this,R.font.circular)
        )
    }
    
}
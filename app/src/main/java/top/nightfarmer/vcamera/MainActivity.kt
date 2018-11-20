package top.nightfarmer.vcamera

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.provider.MediaStore
import android.widget.Toast
import android.support.v4.content.ContextCompat.startActivity
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory
import java.io.File
import java.io.FileInputStream
import android.text.TextUtils
import java.util.*


class MainActivity : AppCompatActivity() {

    var mUri: Uri? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        to_take_pic.setOnClickListener {
            val openCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //系统常量， 启动相机的关键

            if (hasPreferredApplication(this@MainActivity, openCameraIntent)) {
                startAppDetails()
                return@setOnClickListener
            }
            //            startActivity(Intent(this, CameraActivity::class.java))
//            testGetDefaultActivity()
//            testStartAppDetails()
//            Log.e("xxxx", "" + hasPreferredApplication(this, openCameraIntent))

            val path = getFilesDir().toString() + File.separator + "images" + File.separator;
            val file = File(path, "vcamera-${System.currentTimeMillis()}.jpg");
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //步骤二：Android 7.0及以上获取文件 Uri
                mUri = FileProvider.getUriForFile(this, "top.nightfarmer.vcamera.files", file);
            } else {
                //步骤三：获取文件Uri
                mUri = Uri.fromFile(file);
            }

            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri)
            startActivityForResult(openCameraIntent, 110) // 参数常量为自定义的request code, 在取返回结果时有用
        }
    }

    override fun onResume() {
        super.onResume()
        checkDefaultApp()
    }

    fun checkDefaultApp() {
        val openCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //系统常量， 启动相机的关键
        if (hasPreferredApplication(this, openCameraIntent)) {
            to_take_pic.text = "重置默认应用"
            tv_notice.text = "相机已设置默认应用，重置默认应用后才会弹出相机选择项"
        } else {
            to_take_pic.text = "测试拍照"
            tv_notice.text = " "
        }
    }

    fun startAppDetails() {
        val pm = packageManager
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        val intent2 = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${info.activityInfo.packageName}"))
        startActivity(intent2)
    }

    fun hasPreferredApplication(context: Context, intent: Intent): Boolean {
        val pm = context.getPackageManager()
        val info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        Log.e("xxx", info.activityInfo.packageName)
        return "android" != info.activityInfo.packageName && "top.nightfarmer.vcamera" != info.activityInfo.packageName
    }

    private fun testGetDefaultActivity() {
        val pm = packageManager
//        val intent = Intent(Intent.ACTION_VIEW)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        intent.data = Uri.parse("http://www.google.com")
        val info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        Log.e("xxxx", "getDefaultActivity info = " + info + ";pkgName = " + info.activityInfo.packageName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Glide.with(this).load(mUri).into(iv_demo)
        }
    }

}


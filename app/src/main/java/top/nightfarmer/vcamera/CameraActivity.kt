package top.nightfarmer.vcamera

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_camera.*
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import java.io.File
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.text.TextUtils
import java.io.FileOutputStream
import java.io.IOException


class CameraActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(this, "权限获取失败，放开权限后才能正常使用", Toast.LENGTH_SHORT).show();
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        chooseRequest()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        to_choose_pic.setOnClickListener {
            choosePic();
        }
        choosePic();
    }

    private fun choosePic() {
        EasyPermissions.requestPermissions(
                PermissionRequest.Builder(this, 111, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .setRationale("文件访问权限未开启，是否尝试打开？")
                        .setPositiveButtonText("确定")
                        .setNegativeButtonText("取消")
//                    .setTheme(R.style.my_fancy_style)
                        .build())
    }

    private fun chooseRequest() {
        val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //这里要传一个整形的常量RESULT_LOAD_IMAGE到startActivityForResult()方法。
        startActivityForResult(intent, 110)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //我们需要判断requestCode是否是我们之前传给startActivityForResult()方法的RESULT_LOAD_IMAGE，并且返回的数据不能为空
        if (requestCode == 110 && resultCode == RESULT_OK && null != data) {
            val selectedImage = data.getData();
            val mUri = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT) as Uri
            copy(this, selectedImage, mUri)
//            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
//            查询我们需要的数据
//            val picturePath = Uri2Path(selectedImage, filePathColumn)
//            Log.e("xxxx", picturePath)
//
//            Log.e("xxxx", mUri?.toString())
//            File(mUri)
//            val realPathFromURI = getRealPathFromURI(this, mUri)
//            Log.e("xxx", realPathFromURI)

//            val targetPath = Uri2Path(mUri, filePathColumn)

//            val target = File(targetPath)
//            File(picturePath).copyTo(target, true)
//
            val intent = Intent()
            setResult(RESULT_OK, intent);
            finish();
        } else {
            finish()
        }
    }

    fun copy(context: Context, srcUri: Uri, dstUri: Uri) {
        try {
            val outputStream = context.contentResolver.openOutputStream(dstUri) ?: return
            val inputStream = context.contentResolver.openInputStream(srcUri) ?: return
//            val outputStream = FileOutputStream(dstFile)
            IoUtils.copy(inputStream, outputStream)
            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}

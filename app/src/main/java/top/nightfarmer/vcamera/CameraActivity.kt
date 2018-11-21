package top.nightfarmer.vcamera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_camera.*
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import java.io.IOException


class CameraActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(this, "权限获取失败，放开权限后才能正常使用", Toast.LENGTH_SHORT).show()
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
            choosePic()
        }
        choosePic()
        if (intent.action == MediaStore.ACTION_VIDEO_CAPTURE) {
            to_choose_pic.text = "选择本地视频文件"
        }
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
        if (intent.action == MediaStore.ACTION_VIDEO_CAPTURE) {
            val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 666)
        } else {
            val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 666)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //我们需要判断requestCode是否是我们之前传给startActivityForResult()方法的RESULT_LOAD_IMAGE，并且返回的数据不能为空
        if (requestCode == 666 && resultCode == RESULT_OK && null != data) {
            val selectedFile = data.data
            val mUri = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT) as Uri
//            Log.e("xxx", "" + selectedFile)
            copy(this, selectedFile, mUri)

            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        } else {
            finish()
        }
    }

    private fun copy(context: Context, srcUri: Uri, dstUri: Uri) {
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

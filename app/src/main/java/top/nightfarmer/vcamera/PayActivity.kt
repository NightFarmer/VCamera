package top.nightfarmer.vcamera

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_pay.*
import top.nightfarmer.vcamera.donate.AlipayDonate
import android.content.Context.MODE_PRIVATE


class PayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)
        //nightfarmer tsx09794xowzgzxocqfa3ec
//        donateAlipay("tsx09794xowzgzxocqfa3ec");

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        title = "捐赠"
        supportActionBar?.title = "捐赠"

        bt_alipay.setOnClickListener {
            donateAlipay("tsx09794xowzgzxocqfa3ec")
        }
        bt_hide_pay.setOnClickListener {
            val sharedPreferences = getSharedPreferences("db", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()//获取编辑器
            editor.putBoolean("hideIcon", true)
            editor.commit()//提交修改
            Toast.makeText(this, "感谢使用", Toast.LENGTH_SHORT).show();
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    /**
     * 支付宝支付
     * @param payCode 收款码后面的字符串；例如：收款二维码里面的字符串为 https://qr.alipay.com/tsx09794xowzgzxocqfa3ec ，则
     * payCode = tsx09794xowzgzxocqfa3ec
     * 注：不区分大小写
     */
    private fun donateAlipay(payCode: String) {
        val hasInstalledAlipayClient = AlipayDonate.hasInstalledAlipayClient(this)
        if (hasInstalledAlipayClient) {
            AlipayDonate.startAlipayClient(this, payCode)
        }
    }
}

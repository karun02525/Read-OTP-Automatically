package com.learn

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.learn.ui.theme.AndroidReadOTPAutomaticallyTheme
import com.learn.ui.theme.SmsBroadcastReceiver
import java.util.regex.Pattern

class MainActivity : ComponentActivity() {

    var smsBroadcastReceiver: SmsBroadcastReceiver? = null

    //var stOtp: TextInputEditText? = null
    var stOtp = ""


    val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val message = result.data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                getOtpFromMessage(message)
            }
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startSmartUserConsent()
        setContent {
            AndroidReadOTPAutomaticallyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                }
            }
        }
    }

    private fun startSmartUserConsent() {
        val client = SmsRetriever.getClient(this)
        client.startSmsUserConsent(null)

    }


    private fun getOtpFromMessage(message: String?) {

        val otpPatter = Pattern.compile("(|^)\\d{6}")
        val matcher = otpPatter.matcher(message)
        if (matcher.find())
            stOtp = matcher.group(0)?.toString() ?: ""
        Toast.makeText(this@MainActivity, "OTP: " + stOtp, Toast.LENGTH_SHORT).show()

    }


    private fun registerBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver!!.smsBroadcastReceiverListener =
            object : SmsBroadcastReceiver.ISmsBroadcastListener {
                override fun onSuccess(intent: Intent?) {
                    activityResultLauncher.launch(intent)
                }

                override fun onFailure() {

                }

            }

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
                registerReceiver(smsBroadcastReceiver, intentFilter)


    }

    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsBroadcastReceiver)
    }

}
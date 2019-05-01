package com.ricardoeihara.calculaflex

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.ricardoeihara.calculaflex.utils.CalculaFlexTracker
import com.ricardoeihara.calculaflex.utils.DatabaseUtil
import com.ricardoeihara.calculaflex.utils.RemoteConfig
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    private lateinit var mAuth: FirebaseAuth

    private val newUserRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        showGasStationAd()

        if (mAuth.currentUser != null) {
            goToHome()
        }

        btLogin.setOnClickListener {
            mAuth.signInWithEmailAndPassword(
                inputLoginEmail.text.toString(),
                inputLoginPassword.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    sendDataToAnalytics()
                    goToHome()
                } else {
                    Toast.makeText(this@LoginActivity, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        btSignup.setOnClickListener {
            startActivityForResult(Intent(this, SignUpActivity::class.java), newUserRequestCode)
        }
    }

    private fun showGasStationAd() {
        val gasStationAd = RemoteConfig.getFirebaseRemoteConfig()
            .getString("gas_station_ad")

        if (gasStationAd != "") {
            Toast.makeText(this@LoginActivity, gasStationAd, Toast.LENGTH_LONG).show()
        }
    }

    private fun goToHome() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { instanceIdResult ->
            val newToken = instanceIdResult.token
            DatabaseUtil.saveToken(newToken)
        }

        val intent = Intent(this, FormActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == newUserRequestCode && resultCode == Activity.RESULT_OK) {
            inputLoginEmail.setText(data?.getStringExtra("email"))
        }
    }

    private fun sendDataToAnalytics() {
        val bundle = Bundle()
        bundle.putString("EVENT_NAME", "LOGIN")
        bundle.putString("USER_EMAIL", inputLoginEmail.text.toString())

        CalculaFlexTracker.trackEvent(this, bundle)
    }
}

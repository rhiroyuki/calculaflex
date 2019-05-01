package com.ricardoeihara.calculaflex

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.support.v7.app.AppCompatActivity

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.ricardoeihara.calculaflex.utils.RemoteConfig
import kotlinx.android.synthetic.main.activity_splash.*

/*
class SplashActivity : AppCompatActivity() {

    private val TEMPO_AGUARDO_SPLASHSCREEN = 3500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        val isFirstOpen = preferences.getBoolean("open_first", true)

        if (isFirstOpen) {
            showLogin()
        } else {
            markAppAlreadyOpen(preferences)
            showSplash()
        }
    }

    private fun markAppAlreadyOpen(preferences: SharedPreferences) {
        val editor = preferences.edit()
        editor.putBoolean("open_first", false)
        editor.apply()
    }

    private fun showLogin() {
        val nextScreen = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(nextScreen)
        finish()
    }

    private fun showSplash() {
        //Carrega a animação
        val anim = AnimationUtils.loadAnimation(this, R.anim.animacao_splash)
        anim.reset()
        ivLogo.clearAnimation()

        //Roda a animação
        ivLogo.startAnimation(anim)

        //Chama a próxima tela após 3,5 segundos definido na SPLASH_DISPLAY_LENGTH
        Handler().postDelayed({
            val nextScreen = Intent(this@SplashActivity, FormActivity::class.java)
            startActivity(nextScreen)
            finish()
        }, TEMPO_AGUARDO_SPLASHSCREEN)
    }
}
*/

class SplashActivity : BaseActivity() {

    private val TEMPO_AGUARDO_SPLASHSCREEN = 3500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        RemoteConfig.remoteConfigFetch()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    RemoteConfig.getFirebaseRemoteConfig().activateFetched()
                    val minVersionApp = RemoteConfig.getFirebaseRemoteConfig()
                        .getLong("min_version_app")
                        .toInt()

                    if (minVersionApp <= BuildConfig.VERSION_CODE)
                        continueApp()
                    else
                        showAlertMinVersion()
                } else
                    continueApp()
            }
    }

    private fun markAppAlreadyOpen(preferences: SharedPreferences) {
        val editor = preferences.edit()
        editor.putBoolean("open_first", false)
        editor.apply()
    }

    private fun showLogin() {
        val nextScreen = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(nextScreen)
        finish()
    }

    private fun showSplash() {
        val anim = AnimationUtils.loadAnimation(this, R.anim.animacao_splash)
        anim.reset()
        ivLogo.clearAnimation()
        ivLogo.startAnimation(anim)

        Handler().postDelayed({
            val nextScreen = Intent(this@SplashActivity, FormActivity::class.java)
            startActivity(nextScreen)
            finish()
        }, TEMPO_AGUARDO_SPLASHSCREEN)
    }

    private fun showAlertMinVersion() {
        AlertDialog.Builder(this)
            .setTitle("Ops")
            .setMessage("Você esta utilizando uma versão muito antiga do aplicativo. Deseja atualizar?")

            .setPositiveButton("Sim") { dialog, which ->
                var intent: Intent
                try {
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
                    startActivity(intent)
                } catch (e: android.content.ActivityNotFoundException) {
                    intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                    startActivity(intent)
                }
            }

            .setNegativeButton("Não") { dialog, which ->
                finish()
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun continueApp() {
        val preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        val isFirstOpen = preferences.getBoolean("open_first", true)

        if (isFirstOpen) {
            showLogin()
        } else {
            markAppAlreadyOpen(preferences)
            showSplash()
        }
    }

}

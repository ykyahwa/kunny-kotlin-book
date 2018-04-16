package com.androidhuman.example.simplegithub.ui.signin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.androidhuman.example.simplegithub.BuildConfig
import com.androidhuman.example.simplegithub.R
import com.androidhuman.example.simplegithub.api.AuthApi
import com.androidhuman.example.simplegithub.api.model.GithubAccessToken
import com.androidhuman.example.simplegithub.api.provideAuthApi
import com.androidhuman.example.simplegithub.data.AuthTokenProvider
import com.androidhuman.example.simplegithub.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_sign_in.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {

    internal val api: AuthApi by lazy { provideAuthApi() }

    internal val authTokenProvider: AuthTokenProvider by lazy { AuthTokenProvider(this) }

    internal var accessTokenCall: Call<GithubAccessToken>?  =  null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        btnActivitySignInStart.setOnClickListener {
            val authUri = Uri.Builder().scheme("https").authority("github.com")
                    .appendPath("login")
                    .appendPath("oauth")
                    .appendPath("authorize")
                    .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
                    .build()

            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(this@SignInActivity, authUri)
        }

        if (null != authTokenProvider.token) {
            launchMainActivity()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        showProgress()

        val uri = intent.getData() ?: throw IllegalArgumentException("No data exists")
//        if (null == uri) {
//            throw IllegalArgumentException("No data exists")
//        }

        val code = uri.getQueryParameter("code") ?: throw IllegalStateException("No code exists")
//        val code = uri!!.getQueryParameter("code")
//        if (null == code) {
//            throw IllegalStateException("No code exists")
//        }

        getAccessToken(code)
    }

    override fun onStop() {
        super.onStop()
        accessTokenCall?.run { cancel() }
    }

    private fun getAccessToken(code: String) {
        showProgress()

        accessTokenCall = api.getAccessToken(
                BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code)

        accessTokenCall!!.enqueue(object : Callback<GithubAccessToken> {
            public override fun onResponse(call: Call<GithubAccessToken>,
                                           response: Response<GithubAccessToken>) {
                hideProgress()

                val token = response.body()
                if (response.isSuccessful() && null != token) {
                    authTokenProvider.updateToken(token.accessToken)

                    launchMainActivity()
                } else {
                    showError(IllegalStateException(
                            "Not successful: " + response.message()))
                }
            }

            public override fun onFailure(call: Call<GithubAccessToken>, t: Throwable) {
                hideProgress()
                showError(t)
            }
        })
    }

    private fun showProgress() {
        btnActivitySignInStart.visibility = View.GONE
        pbActivitySignIn.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        btnActivitySignInStart.visibility = View.VISIBLE
        pbActivitySignIn.visibility = View.GONE
    }

    private fun showError(throwable: Throwable) {
        Toast.makeText(this, throwable.message, Toast.LENGTH_LONG).show()
    }

    private fun launchMainActivity() {
        startActivity(Intent(
                this@SignInActivity, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}

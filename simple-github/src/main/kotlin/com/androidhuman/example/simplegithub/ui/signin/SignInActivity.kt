package com.androidhuman.example.simplegithub.ui.signin

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.androidhuman.example.simplegithub.BuildConfig
import com.androidhuman.example.simplegithub.R
import com.androidhuman.example.simplegithub.api.AuthApi
import com.androidhuman.example.simplegithub.api.provideAuthApi
import com.androidhuman.example.simplegithub.data.AuthTokenProvider
import com.androidhuman.example.simplegithub.extensions.plusAssign
import com.androidhuman.example.simplegithub.rx.AutoClearedDisposable
import com.androidhuman.example.simplegithub.rx.plusAssign
import com.androidhuman.example.simplegithub.ui.main.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.longToast
import org.jetbrains.anko.newTask

class SignInActivity : AppCompatActivity() {

    internal val api: AuthApi by lazy { provideAuthApi() }

    internal val authTokenProvider: AuthTokenProvider by lazy { AuthTokenProvider(this) }

//    internal var accessTokenCall: Call<GithubAccessToken>?  =  null
    internal val disposables = AutoClearedDisposable(this)

    internal val viewDisposable = AutoClearedDisposable(lifecycleOwner = this, alwaysClearOnStop = false)
    internal val viewModelFactory by lazy {
        SignInViewModelFactory(provideAuthApi(), AuthTokenProvider(this))
    }
    lateinit var viewModel : SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[SignInViewModel::class.java]
        lifecycle += disposables
        lifecycle += viewDisposable

        viewDisposable += viewModel.accessToken
                .filter { !it.isEmpty }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{launchMainActivity()}

        viewDisposable += viewModel.message
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{message -> showError(message)}

        viewDisposable += viewModel.isLoading
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{isLoading ->
                    if (isLoading) {
                        showProgress()
                    } else {
                        hideProgress()
                    }
                }

        disposables += viewModel.loadAccessTocken()

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

    private fun getAccessToken(code: String) {

        disposables += viewModel.requestAccessToken(
                BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code)
//        showProgress()
//
//        disposables += api.getAccessToken(BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code)
//                .map { it.accessToken }
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnSubscribe{showProgress()}
//                .doOnTerminate{hideProgress()}
//                .subscribe({token ->
//                    authTokenProvider.updateToken(token)
//                    launchMainActivity()
//                }) {
//                    showError(it)
//                }
    }

    private fun showProgress() {
        btnActivitySignInStart.visibility = View.GONE
        pbActivitySignIn.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        btnActivitySignInStart.visibility = View.VISIBLE
        pbActivitySignIn.visibility = View.GONE
    }

    private fun showError(message : String) {
        longToast(message)
//        longToast(throwable.message?:"No message available")
    }

    private fun launchMainActivity() {
        startActivity(intentFor<MainActivity>().clearTask().newTask())
    }
}

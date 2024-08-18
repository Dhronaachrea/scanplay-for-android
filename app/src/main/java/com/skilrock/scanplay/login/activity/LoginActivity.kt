package com.skilrock.scanplay.login.activity

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.scanplay.R
import com.skilrock.scanplay.base.activity.BaseActivity
import com.skilrock.scanplay.databinding.ActivityLoginBinding
import com.skilrock.scanplay.home.activity.HomeActivity
import com.skilrock.scanplay.login.model.request.LoginRequest
import com.skilrock.scanplay.login.model.response.GetLoginDataResponse
import com.skilrock.scanplay.login.model.response.LoginResponse
import com.skilrock.scanplay.login.viewmodel.LoginViewModel
import com.skilrock.scanplay.utility.*

/**
 * Created by Rajneesh Kumar Sharma on 27 Jan 2023.
 * rajneeshk.sharma@skilrock.com
 * Skilrock Technologies
 **/


class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()
        setViewModel()
        setViewElements()
        setClickListeners()
    }


    private fun initializeBinding() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        viewModel.liveDataNetworkError.observe(this, observerError)
        viewModel.liveDataLogin.observe(this, observerLoginData)
        viewModel.liveDataGetLoginData.observe(this, observerGetLoginData)
    }

    private fun setViewElements() {
        binding.btnForgotPassword.visibilityGone()
    }

    override val observerError = Observer<ResponseStatusError> {
        when (it) {
            is ResponseStatusError.NoInternetError -> {
                showToast(getString(R.string.check_internet_connection))
                resetLoginBtn()
            }

            is ResponseStatusError.TechnicalError -> {
                resetLoginBtn()
            }

            is ResponseStatusError.ErrorResponseError -> {
                resetLoginBtn()
            }
        }
    }

    private fun setClickListeners() {
        with(binding) {
            tieUsername.requestFocus()

            btnLogin.setOnClickListener {
                btnLogin.hideKeyboard()
                if (isInputsValid()) {
                    btnLogin.animate().scaleX(1F).setDuration(100).withEndAction {
                        btnLogin.animate().scaleX(0F).setDuration(200).withEndAction {
                            btnLogin.visibilityGone()
                            pbLogin.visibilityVisible()
                            viewModel.callLoginApi(this@LoginActivity, LoginRequest(username = tieUsername.getTrimText(), password = md5(tiePassword.getTrimText()), terminalId = getDeviceSerialNumber() ?: "NA", modelCode = getDeviceModelCode()))
                        }
                    }
                }
            }

            tiePassword.setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        btnLogin.performClick()
                        true
                    }
                    else -> false
                }
            }

            tieUsername.afterTextChanged {
                tilUsername.removeError()
                tiePassword.isEnabled = tieUsername.getTrimText().isNotEmpty()
            }

            tiePassword.afterTextChanged {
                tilPassword.removeError()
            }
        }
    }

    private fun isInputsValid() : Boolean {
        with(binding) {
            if (tieUsername.getTrimText().isEmpty()) {
                tilUsername.putError(getString(R.string.please_enter_username))
                return false
            }

            if(tiePassword.getTrimText().isEmpty()) {
                tilPassword.putError(getString(R.string.please_enter_password))
                return false
            }
        }

        return true
    }

    private val observerLoginData = Observer<ResponseStatus<LoginResponse>> { responseStatus ->
        when(responseStatus) {
           is ResponseStatus.Success -> {
               RetailerInfo.setLoginData(this, responseStatus.response)
               viewModel.callGetLoginData(this)
           }

           is ResponseStatus.Error -> {
               this.showToast(responseStatus.responseMessage)
               resetLoginBtn()
           }

           is ResponseStatus.TechnicalError -> {
               this.showToast(responseStatus.responseMessage)
               resetLoginBtn()
           }
        }
    }

    private val observerGetLoginData = Observer<ResponseStatus<GetLoginDataResponse>> { responseStatus ->
        when(responseStatus) {
           is ResponseStatus.Success -> {
               RetailerInfo.setGetLoginData(this, responseStatus.response)
               startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
               overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
               finish()
           }

           is ResponseStatus.Error -> {
               this.showToast(responseStatus.responseMessage)
               resetLoginBtn()
           }

           is ResponseStatus.TechnicalError -> {
               this.showToast(responseStatus.responseMessage)
               resetLoginBtn()
           }
        }
    }

    private fun resetLoginBtn() {
        with(binding) {
            pbLogin.visibilityGone()
            btnLogin.visibilityVisible()
            btnLogin.animate().scaleX(0F).setDuration(100).withEndAction {
                btnLogin.animate().scaleX(1F).duration = 180
            }
        }
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

    }
}
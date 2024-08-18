package com.skilrock.scanplay.home.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.PixelCopy.request
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.skilrock.scanplay.R
import com.skilrock.scanplay.base.fragment.BaseFragment
import com.skilrock.scanplay.databinding.FragmentHomeDepositBinding
import com.skilrock.scanplay.home.dialog.DepositSuccessDialog
import com.skilrock.scanplay.home.dialog.SuccessWithdrawalDialog
import com.skilrock.scanplay.home.model.request.ProvideCouponRequest
import com.skilrock.scanplay.home.model.response.ProvideCouponResponse
import com.skilrock.scanplay.home.viewmodel.HomeViewModel
import com.skilrock.scanplay.login.activity.LoginActivity
import com.skilrock.scanplay.utility.*
import com.sunmi.peripheral.printer.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeDepositFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeDepositBinding
    private lateinit var viewModel: HomeViewModel
    private var mSunmiPrinterService: SunmiPrinterService? = null
    private val boldFontEnable = byteArrayOf(0x1B, 0x45, 0x1)
    private val boldFontDisable = byteArrayOf(0x1B, 0x45, 0x0)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeDepositBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        initializeSunmiPrinter()
        setViewElements()
        setRetailerDetailsInTextView()
        setOnClickListener()
    }

    @SuppressLint("SetTextI18n")
    private fun setRetailerDetailsInTextView() {
        with(binding) {
            tvRetailerName.text = RetailerInfo.getUsername()
            tvRetailerId.text   = if (RetailerInfo.getLoginDataUserId() != 0) "ID : ${RetailerInfo.getLoginDataUserId()}" else ""
        }
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        viewModel.liveDataNetworkError.observe(master, observerError)
        viewModel.liveDataDeposit.observe(viewLifecycleOwner, observerDepositData)
    }

    private val observerError = Observer<ResponseStatusError> {
        when (it) {
            is ResponseStatusError.NoInternetError -> {
                master.showToast(getString(R.string.check_internet_connection))
                resetPrintBtn()
            }
            else -> {}
        }
    }

    private fun setOnClickListener() {
        with(binding) {
            lifecycleScope.launch {
                delay(500)
                tieAmount.showKeyboard()
            }

            btnPrint.setOnClickListener {
                btnPrint.hideKeyboard()
                if(isInputsValid()) {
                    btnPrint.animate().scaleX(1F).setDuration(100).withEndAction {
                        btnPrint.animate().scaleX(0F).setDuration(200).withEndAction {
                            btnPrint.visibilityGone()
                            pbPrint.visibilityVisible()
                            master.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            viewModel.callDepositViewModel(master, ProvideCouponRequest(aliasName = ALIAS_NAME, amount = tieAmount.getTrimText().toInt(), couponCount = COUPON_COUNT, gameCode = GAME_CODE, providerCode = PROVIDER_CODE, responseType = RESPONSE_TYPE, retailerName = RetailerInfo.getOrgName(), serviceCode = SERVICE_CODE))
                        }
                    }
                }
            }

            tieAmount.setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        btnPrint.performClick()
                        true
                    }
                    else -> false
                }
            }

            tieAmount.afterTextChanged {
                tilAmount.removeError()
            }
        }
    }

    private fun initializeSunmiPrinter() {
        try {
            InnerPrinterManager.getInstance().bindService(master, innerPrinterCallback)
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
    }

    private fun setViewElements() {
        binding.tieAmount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.euro_currency_symbol, 0, 0, 0)
    }

    private var innerPrinterCallback: InnerPrinterCallback = object : InnerPrinterCallback() {
        override fun onConnected(sunmiPrinterService: SunmiPrinterService) {
            mSunmiPrinterService = sunmiPrinterService
        }

        override fun onDisconnected() {}
    }

    private fun onPrintBtnProceed(mQrCodeUrl: String) {
        Glide.with(master).asBitmap().load(mQrCodeUrl).into(object : CustomTarget<Bitmap?>() {
            override fun onResourceReady(bitmapImage: Bitmap, transition: Transition<in Bitmap?>?) {
                lifecycleScope.launch(Dispatchers.IO) {
                    startPrintingNow(bitmapImage)
                }
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                resetPrintBtn()
                lifecycleScope.launch(Dispatchers.Main) {
                    if (!master.isDestroyed) {
                        master.showToast(getString(R.string.internal_issue_occurred_please_try_again))
                    }
                }

            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                resetPrintBtn()
                lifecycleScope.launch(Dispatchers.Main) {
                    if (!master.isDestroyed) {
                        master.showToast(getString(R.string.internal_issue_occurred_please_try_again))
                    }
                }
            }
        })
    }

    private suspend fun startPrintingNow(qrBitmap: Bitmap) {
        val recreatedQrBitmap = Bitmap.createScaledBitmap(qrBitmap, 300, 300, false)
        mSunmiPrinterService?.run {
            enterPrinterBuffer(true)
            setAlignment(1, null)
            sendRAWData(boldFontEnable, null)
            setFontSize(32f, null)
            printText("\n---- ${RetailerInfo.getUsername()} -----", null)
            sendRAWData(boldFontDisable, null)
            setFontSize(27f, null)
            printText("\n${if (RetailerInfo.getLoginDataUserId() != 0) "ID : ${RetailerInfo.getLoginDataUserId()}" else ""}", null)
            sendRAWData(boldFontEnable, null)
            setFontSize(27f, null)
            printText("\n\n\n----- Amount: $CURRENCY_CODE ${binding.tieAmount.getTrimText().toLong()} -----", null)
            sendRAWData(boldFontDisable, null)
            printBitmapCustom(recreatedQrBitmap, 0, null)
            printText("\n-------------------------\n", null)
            sendRAWData(boldFontEnable, null)
            setFontSize(23f, null)
            setAlignment(0, null)
            printText("*Note : Please always keep this \nreceipt with you when you want\n cash out your remaining game\n balance.", null)
            printText(" Go to game portal for \ninitiate withdrawal.", null)
            sendRAWData(boldFontDisable, null)
            setAlignment(1, null)
            printText("\n--------------------------\n", null)
            printText("\n------ FOR DEMO ------\n\n", null)
            exitPrinterBufferWithCallback(true, object : InnerResultCallback() {
                override fun onRunResult(isSuccess: Boolean) {}

                override fun onReturnString(result: String?) {}

                override fun onRaiseException(code: Int, msg: String?) {
                    master.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    resetPrintBtn()
                }

                override fun onPrintResult(code: Int, msg: String?) {lifecycleScope.launch {
                    withContext(Dispatchers.Main) {
                        if (!master.isDestroyed)
                            master.showToast(getString(R.string.receipt_printed_successfully))
                        resetPrintBtn()
                        master.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                }

                }
            })

        } ?: withContext(Dispatchers.Main) {
            if (!master.isDestroyed)
                master.showToast(getString(R.string.unble_to_print_receipt_please_try_again))
            resetPrintBtn()
        }
    }

    private val observerDepositData = Observer<ResponseStatus<ProvideCouponResponse>> { responseStatus ->
        when(responseStatus) {
            is ResponseStatus.Success -> {
                with(binding) {
                    pbPrint.visibilityGone()
                    llPrinting.visibilityVisible()
                    tvPrinting.startAnimation(AnimationUtils.loadAnimation(master, R.anim.hurry_up_animation))
                }
                master.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //onPrintBtnProceed(responseStatus.response.data?.couponQRCodeUrl ?: "")
                val successDepositDialog = DepositSuccessDialog(master, ::callBack)
                successDepositDialog.arguments = Bundle().apply {
                    putString("amount", (binding.tieAmount.getTrimText()))
                    putString("bitmapImageQrCodeUrl", responseStatus.response.data?.couponQRCodeUrl)
                }

                successDepositDialog.show(parentFragmentManager, "")
                resetPrintBtn()
            }

            is ResponseStatus.Error -> {
                if (responseStatus.responseMessage == "Session Expired, Please login") {
                    performLogoutCleanUp(master, Intent(master, LoginActivity::class.java))
                }
                master.showToast(responseStatus.responseMessage)
                resetPrintBtn()
            }

            is ResponseStatus.TechnicalError -> {
                master.showToast(responseStatus.responseMessage)
                resetPrintBtn()
            }
        }
    }

    private fun callBack(isErrorOccurred: Boolean) {
        if (isErrorOccurred) {
            resetPrintBtn()
            lifecycleScope.launch(Dispatchers.Main) {
                if (!master.isDestroyed) {
                    master.showToast(getString(R.string.internal_issue_occurred_please_try_again))
                }
            }
        }
    }

    private fun isInputsValid() : Boolean {
        with(binding) {
            if (tieAmount.getTrimText().isEmpty()) {
                tilAmount.putError(getString(R.string.please_enter_amount))
                return false
            }

            if(tieAmount.getTrimText().toInt() == 0) {
                tilAmount.putError(getString(R.string.please_enter_valid_amount))
                return false
            }
        }

        return true
    }

    private fun resetPrintBtn() {
        with(binding) {
            pbPrint.visibilityGone()
            btnPrint.visibilityVisible()
            llPrinting.visibilityGone()
            tieAmount.setText("")
            btnPrint.animate().scaleX(0F).setDuration(100).withEndAction {
                btnPrint.animate().scaleX(1F).duration = 180
            }
            master.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}




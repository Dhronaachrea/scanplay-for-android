package com.skilrock.scanplay.home.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.ViewBindingAdapter.setClickListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.skilrock.scanplay.R
import com.skilrock.scanplay.databinding.DepositSuccessDialogBinding
import com.skilrock.scanplay.databinding.SuccessWithdrawalDialogBinding
import com.skilrock.scanplay.utility.CURRENCY_CODE
import com.skilrock.scanplay.utility.RetailerInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DepositSuccessDialog(val mContext: Context, val callBack:(isErrorOccurred: Boolean) -> Unit ) : DialogFragment() {
    private lateinit var binding: DepositSuccessDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.deposit_success_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        receiveDataFromFragment()
    }

    @SuppressLint("SetTextI18n")
    private fun receiveDataFromFragment() {
        arguments.let {
            val amount                  = it?.getString("amount") ?: ""
            val bitmapImageQrCodeUrl    = it?.getString("bitmapImageQrCodeUrl") ?: ""
            binding.tvAmount.text       = "$CURRENCY_CODE $amount"
            binding.tvUserName.text     = RetailerInfo.getUsername()
            onSaveBtnProceed(bitmapImageQrCodeUrl, amount, mContext)
        }
    }

    private fun onSaveBtnProceed(mQrCodeUrl: String, amount: String, context: Context) {
        Glide.with(context).asBitmap().load(mQrCodeUrl).into(object : CustomTarget<Bitmap?>() {
            override fun onResourceReady(bitmapImage: Bitmap, transition: Transition<in Bitmap?>?) {
                binding.ivQR.setImageBitmap(bitmapImage)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                callBack(true)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                callBack(true)

            }

            override fun onStart() {
                super.onStart()
                binding.ivQR.setImageResource(R.drawable.ic_baseline_downloading_24)
            }
        })
    }
}
package com.skilrock.scanplay.home.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.skilrock.scanplay.R
import com.skilrock.scanplay.databinding.SuccessWithdrawalDialogBinding
import com.skilrock.scanplay.utility.CURRENCY_CODE
import java.text.SimpleDateFormat
import java.util.*

class SuccessWithdrawalDialog : DialogFragment() {
    private lateinit var binding: SuccessWithdrawalDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        isCancelable = false

        binding = DataBindingUtil.inflate(inflater, R.layout.success_withdrawal_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        receiveDataFromFragment()
        setClickListener()
    }

    @SuppressLint("SetTextI18n")
    private fun receiveDataFromFragment() {
        arguments.let {
            val amount      = it?.getString("amount")
            val userTxnId   = it?.getString("txnId")
            val date        = SimpleDateFormat("dd MMM yyyy, hh:mm aaa", Locale.getDefault()).format(Date())

            with(binding) {
                tvAmount.text   = "$CURRENCY_CODE $amount"
                tvID.text       = "ID: $userTxnId"
                tvDate.text     = date
            }
        }
    }

    private fun setClickListener() {
        with(binding) {
            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }
}
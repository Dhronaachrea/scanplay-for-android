package com.skilrock.scanplay.home.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.scanplay.R
import com.skilrock.scanplay.databinding.SheetLogoutBinding

class LogoutSheet(val callback:() -> Unit) : BottomSheetDialogFragment() {

    private lateinit var binding : SheetLogoutBinding

    companion object {
        const val TAG = "LogoutSheet"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.sheet_logout, container, false)
        setSheetExpanded()
        return binding.root
    }

    private fun setSheetExpanded() {
        dialog?.setOnShowListener { dialog ->
            val sheetDialog = dialog as BottomSheetDialog
            val bottomSheetInternal: View? = sheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheetInternal?.let { BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            callback()
            dismiss()
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }
}
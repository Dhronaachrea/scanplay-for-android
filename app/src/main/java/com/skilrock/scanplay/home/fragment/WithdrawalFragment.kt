package com.skilrock.scanplay.home.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.core.FocusMeteringAction.FLAG_AF
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.skilrock.scanplay.R
import com.skilrock.scanplay.base.fragment.BaseFragment
import com.skilrock.scanplay.databinding.FragmentWithdrawalBinding
import com.skilrock.scanplay.home.dialog.SuccessWithdrawalDialog
import com.skilrock.scanplay.home.model.request.UpdateQrWithdrawalRequest
import com.skilrock.scanplay.home.model.response.PendingWithdrawalResponse
import com.skilrock.scanplay.home.model.response.UpdateQrWithdrawalResponse
import com.skilrock.scanplay.home.viewmodel.CameraXViewModel
import com.skilrock.scanplay.home.viewmodel.HomeViewModel
import com.skilrock.scanplay.utility.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Created by Monu Sharma on 27 Jan 2023.
 * monu.sharma@skilrock.com
 * Skilrock Technologies
 */

class WithdrawalFragment : BaseFragment() {
    private lateinit var binding: FragmentWithdrawalBinding
    private val successDialog  = SuccessWithdrawalDialog()
    private lateinit var viewModel: HomeViewModel
    private var analysisUseCase: ImageAnalysis?         = null
    private var cameraProvider: ProcessCameraProvider?  = null
    private var cameraSelector: CameraSelector?         = null
    private var previewUseCase: Preview?                = null
    private var camm: Camera?   = null
    private val lensFacing      = CameraSelector.LENS_FACING_BACK
    private var isFlashLightOn  = false
    private var check           = false
    private var pendingWithdrawalResponse: PendingWithdrawalResponse.Data? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_withdrawal, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        checkPermission()
        setClickListener()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        viewModel.liveDataNetworkError.observe(master, observerError)
        viewModel.liveDataUpdateQrWithdrawal.observe(viewLifecycleOwner, updateQrWithdrawalRequest)
        viewModel.liveDataPendingWithdrawal.observe(viewLifecycleOwner, pendingWithdrawalRequest)

    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        check = if (isGranted) {
            // PERMISSION GRANTED
            setupCamera()
            false
        } else {
            true
            // PERMISSION NOT GRANTED
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(master, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            setupCamera()
        }
    }

    private fun setClickListener() {
        with(binding) {
            ivFlash.setOnClickListener {
                enableDisableFlashLight()
            }

            btnApprove.setOnClickListener {
                btnApprove.animate().scaleX(1F).setDuration(100).withEndAction {
                    btnApprove.animate().scaleX(0F).setDuration(200).withEndAction {
                        btnApprove.visibilityGone()
                        pbWithdraw.visibilityVisible()
                        if (pendingWithdrawalResponse != null) {
                            master.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            viewModel.callUpdateQrWithdrawalViewModel(master,
                                UpdateQrWithdrawalRequest(pendingWithdrawalResponse?.aliasId,pendingWithdrawalResponse?.amount,pendingWithdrawalResponse?.domainId,pendingWithdrawalResponse?.requestId,pendingWithdrawalResponse?.userId, retailerId = RetailerInfo.getLoginDataUserId())
                            )
                        }
                    }
                }
            }
        }
    }

    private fun enableDisableFlashLight() {
        if (camm != null) {
            if (camm?.cameraInfo?.hasFlashUnit() == true) {
                if (!isFlashLightOn) {
                    camm?.cameraControl?.enableTorch(true)
                    isFlashLightOn = true
                    binding.ivFlash.setImageResource(R.drawable.baseline_flash_on_24)
                } else {
                    camm?.cameraControl?.enableTorch(false)
                    isFlashLightOn = false
                    binding.ivFlash.setImageResource(R.drawable.baseline_flash_off_24)
                }
            }
        }
    }

    private fun setupCamera() {
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        val cameraXViewModel = ViewModelProvider(master)[CameraXViewModel::class.java]
        cameraXViewModel.processCameraProvider.observe(master) { provider ->
            cameraProvider = provider
            if (isCameraPermissionGranted()) {
                bindCameraUseCases()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun bindCameraUseCases() {
        bindPreviewUseCase()
        bindAnalyseUseCase()
    }

    private fun bindPreviewUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider?.unbind(previewUseCase)
        }
        previewUseCase = Preview.Builder()
            .setTargetRotation(binding.previewView.display.rotation)
            .build()
        previewUseCase?.setSurfaceProvider(binding.previewView.surfaceProvider)
        try {
            camm = cameraSelector?.let { camSelector ->
                cameraProvider?.bindToLifecycle(master, camSelector, previewUseCase)
            }
        } catch (illegalStateException: IllegalStateException) {
            Log.e(APP_LOG_TAG, "IllegalStateException")
        }
    }

    private fun bindAnalyseUseCase() {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()

        val barcodeScanner = BarcodeScanning.getClient(options)

        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider?.unbind(analysisUseCase)
        }
        analysisUseCase = ImageAnalysis.Builder()
            .setTargetRotation(binding.previewView.display.rotation)
            .build()

        // Initialize our background executor
        val cameraExecutor = Executors.newSingleThreadExecutor()
        analysisUseCase?.setAnalyzer(cameraExecutor) { image: ImageProxy ->
            processImageProxy(
                barcodeScanner,
                image
            )
        }
        val meteringPointFactory = view?.let { view ->
            camm?.let {
                DisplayOrientedMeteringPointFactory(
                    view.display,
                    it.cameraInfo,
                    200F,
                    200F
                )
            }
        }
        val meteringPoint1 = meteringPointFactory?.createPoint(200F, 200F)

        val action = FocusMeteringAction.Builder(meteringPoint1!!)
            .addPoint(meteringPoint1,FLAG_AF)
            .setAutoCancelDuration(3, TimeUnit.SECONDS)
            .build()
        camm?.cameraControl?.startFocusAndMetering(action)

        try {
            cameraSelector?.let { camSelector ->
                cameraProvider?.bindToLifecycle(  master, camSelector, analysisUseCase)
            }
        } catch (illegalStateException: IllegalStateException) {
            Log.e(APP_LOG_TAG, "IllegalStateException")
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(barcodeScanner: BarcodeScanner, imageProxy: ImageProxy) {
        imageProxy.cropRect
        val inputImage = imageProxy.image?.let { InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees) }
        if (inputImage != null) {
            barcodeScanner.process(inputImage).addOnSuccessListener { barcodes: List<Barcode> ->
                barcodes.forEach{
                    vibrate(master)
                    binding.pbWithdraw.visibilityVisible()
                    val uri= Uri.parse(it.rawValue)
                    val couponCode = uri.getQueryParameter("couponCode")
                    master.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    viewModel.callPendingWithdrawalViewModel(master,couponCode ?: "")
                    if (isFlashLightOn) enableDisableFlashLight()
                    cameraProvider?.unbindAll()
                }
            }
        }
        inputImage?.let { it ->
            barcodeScanner.process(it).addOnFailureListener {
                Log.d(APP_LOG_TAG, "barcode error message: " + it.message)
            }
        }
        inputImage?.let {
            barcodeScanner.process(it).addOnCompleteListener {
                imageProxy.close()
            }
        }
    }

    private val pendingWithdrawalRequest = Observer<ResponseStatus<PendingWithdrawalResponse>> { responseStatus ->
        binding.pbWithdraw.visibilityGone()
        master.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        when(responseStatus) {
            is ResponseStatus.Success -> {
                pendingWithdrawalResponse = responseStatus.response.data?.get(0)
                binding.btnApprove.visibilityVisible()
            }
            is ResponseStatus.Error -> {
                bindCameraUseCases()
                master.showToast(responseStatus.responseMessage)
            }
            is ResponseStatus.TechnicalError -> {
                bindCameraUseCases()
                master.showToast(responseStatus.responseMessage)
            }
        }
    }

    private val updateQrWithdrawalRequest = Observer<ResponseStatus<UpdateQrWithdrawalResponse>> { responseStatus ->
        binding.pbWithdraw.visibilityGone()
        master.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        bindCameraUseCases()

        when(responseStatus) {
            is ResponseStatus.Success -> {
                val data = responseStatus.response.data
                successDialog.arguments = Bundle().apply {
                    putString("amount", (data?.amount ?: "").toString())
                    putString("txnId", (data?.userTxnId ?: "").toString())
                }
                successDialog.show(parentFragmentManager, "")
            }
            is ResponseStatus.Error -> {
                master.showToast(responseStatus.responseMessage)
            }
            is ResponseStatus.TechnicalError -> {
                master.showToast(responseStatus.responseMessage)
            }
        }
    }

    private val observerError = Observer<ResponseStatusError> {
        binding.pbWithdraw.visibilityGone()
        when (it) {
            is ResponseStatusError.NoInternetError -> {
                master.showToast(getString(R.string.check_internet_connection))
            }
            else -> {}
        }
    }

    override fun onResume() {
        super.onResume()
        if(check) checkPermission()
        if (isCameraPermissionGranted()) bindCameraUseCases()
        resetUiButtons()
    }

    private fun resetUiButtons() {
        with(binding) {
            btnApprove.visibilityGone()
            pbWithdraw.visibilityGone()
            btnApprove.animate().scaleX(0F).setDuration(100).withEndAction {
                btnApprove.animate().scaleX(1F).duration = 180
            }
            master.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}
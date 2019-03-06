package com.lukakordic.signaturedetector.ui.login

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lukakordic.signaturedetector.R
import com.lukakordic.signaturedetector.utils.RC_CAPTURE_IMAGE
import com.lukakordic.signaturedetector.utils.onClick
import kotlinx.android.synthetic.main.bottom_sheet_dialog_fragment.*

class SignatureBottomSheet : BottomSheetDialogFragment() {

    companion object {
        fun newInstance() = SignatureBottomSheet()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()
    }

    private fun setListeners() {
        takeScreenshot.onClick { launchCamera() } //TODO launch camera
        selectFromGallery.onClick { } //TODO launch gallery
    }

    private fun launchCamera() {
        activity?.startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), RC_CAPTURE_IMAGE)
    }
}
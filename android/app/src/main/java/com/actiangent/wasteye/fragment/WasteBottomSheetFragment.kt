package com.actiangent.wasteye.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.actiangent.wasteye.databinding.FragmentWasteBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import model.WasteType


class WasteBottomSheetFragment(private val type: WasteType) : BottomSheetDialogFragment() {

    private var _fragmentWasteBottomSheetBinding: FragmentWasteBottomSheetBinding? = null
    private val fragmentWasteBottomSheetBinding get() = _fragmentWasteBottomSheetBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentWasteBottomSheetBinding =
            FragmentWasteBottomSheetBinding.inflate(inflater, container, false)

        return fragmentWasteBottomSheetBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _fragmentWasteBottomSheetBinding.apply {
            when(type) {
                WasteType.CARDBOARD -> {}
                WasteType.GLASS -> {}
                WasteType.METAL -> {}
                WasteType.PAPER -> {}
                WasteType.PLASTIC -> {}
                WasteType.UNKNOWN -> {
                    // Do nothing
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _fragmentWasteBottomSheetBinding = null
    }
}
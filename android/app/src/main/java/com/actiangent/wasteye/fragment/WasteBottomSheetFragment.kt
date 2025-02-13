package com.actiangent.wasteye.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.actiangent.wasteye.R
import com.actiangent.wasteye.databinding.FragmentWasteBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.actiangent.wasteye.model.WasteType

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
            val typeText = when (type) {
                WasteType.CARDBOARD -> requireContext().getString(R.string.waste_type_cardboard)
                WasteType.GLASS -> requireContext().getString(R.string.waste_type_glass)
                WasteType.METAL -> requireContext().getString(R.string.waste_type_metal)
                WasteType.PAPER -> requireContext().getString(R.string.waste_type_paper)
                WasteType.PLASTIC -> requireContext().getString(R.string.waste_type_plastic)
                WasteType.UNKNOWN -> requireContext().getString(R.string.waste_type_unknown)
            }
            fragmentWasteBottomSheetBinding.tvWasteName.text = typeText
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _fragmentWasteBottomSheetBinding = null
    }

    companion object {
        const val TAG = "WasteBottomSheetFragment"
    }
}
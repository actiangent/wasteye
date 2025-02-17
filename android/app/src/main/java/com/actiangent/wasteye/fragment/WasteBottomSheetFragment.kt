package com.actiangent.wasteye.fragment

import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import coil.size.Scale
import com.actiangent.wasteye.databinding.FragmentWasteBottomSheetBinding
import com.actiangent.wasteye.model.Waste
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.CornerFamily

class WasteBottomSheetFragment(private val waste: Waste) : BottomSheetDialogFragment() {

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

        fragmentWasteBottomSheetBinding.apply {
            itemWaste.textWasteName.text = requireContext().getString(waste.nameResId)
            itemWaste.imageWaste.apply {
                shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                    .setAllCorners(CornerFamily.ROUNDED, 16f)
                    .build()
                load(waste.imageResId) {
                    crossfade(50)
                }
            }
            textWasteDescription.text = waste.description

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                textWasteDescription.justificationMode = JUSTIFICATION_MODE_INTER_WORD
            }
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
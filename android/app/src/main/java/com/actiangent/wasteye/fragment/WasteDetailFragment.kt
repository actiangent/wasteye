package com.actiangent.wasteye.fragment

import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import com.actiangent.wasteye.databinding.FragmentWasteDetailBinding
import com.actiangent.wasteye.extension.buildBulletFromArraySpannableString
import com.actiangent.wasteye.model.Waste
import com.google.android.material.shape.CornerFamily

class WasteDetailFragment : Fragment() {

    private var _fragmentWasteDetailBinding: FragmentWasteDetailBinding? = null
    private val fragmentWasteDetailBinding get() = _fragmentWasteDetailBinding!!

    val args: WasteDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentWasteDetailBinding = FragmentWasteDetailBinding.inflate(inflater, container, false)

        return fragmentWasteDetailBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val waste = Waste.entries[args.wasteOrdinal]
        fragmentWasteDetailBinding.apply {
            with(requireContext().resources) {
                textWasteName.text = getString(waste.nameResId)
                textWasteExplanation.text = getString(waste.explanationResId)
                textWasteSortation.text =
                    buildBulletFromArraySpannableString(getStringArray(waste.sortationResId))
                textWasteRecycling.text = getString(waste.recyclingResId)
                imageWaste.apply {
                    shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                        .setAllCorners(CornerFamily.ROUNDED, 4f)
                        .build()
                    load(waste.imageResId) {
                        crossfade(50)
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                textWasteExplanation.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                textWasteSortation.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                textWasteRecycling.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            }
        }
    }
}
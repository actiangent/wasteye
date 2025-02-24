package com.actiangent.wasteye.model

import androidx.annotation.ArrayRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.actiangent.wasteye.R

enum class Waste(
    @StringRes val nameResId: Int,
    @DrawableRes val imageResId: Int,
    @StringRes val descriptionResId: Int,
    @StringRes val explanationResId: Int,
    @ArrayRes val sortationResId: Int,
    @StringRes val recyclingResId: Int,
) {
    CARDBOARD(
        nameResId = R.string.waste_type_cardboard,
        imageResId = R.drawable.thumbnail_waste_cardboard,
        descriptionResId = R.string.waste_type_description_cardboard,
        explanationResId = R.string.waste_type_explanation_cardboard,
        sortationResId = R.array.waste_type_sortation_cardboard,
        recyclingResId = R.string.waste_type_recycling_cardboard
    ),
    GLASS(
        nameResId = R.string.waste_type_glass,
        imageResId = R.drawable.thumbnail_waste_glass,
        descriptionResId = R.string.waste_type_description_glass,
        explanationResId = R.string.waste_type_explanation_glass,
        sortationResId = R.array.waste_type_sortation_glass,
        recyclingResId = R.string.waste_type_recycling_glass
    ),
    METAL(
        nameResId = R.string.waste_type_metal,
        imageResId = R.drawable.thumbnail_waste_metal,
        descriptionResId = R.string.waste_type_description_metal,
        explanationResId = R.string.waste_type_explanation_metal,
        sortationResId = R.array.waste_type_sortation_metal,
        recyclingResId = R.string.waste_type_recycling_metal
    ),
    PAPER(
        nameResId = R.string.waste_type_paper,
        imageResId = R.drawable.thumbnail_waste_paper,
        descriptionResId = R.string.waste_type_description_paper,
        explanationResId = R.string.waste_type_explanation_paper,
        sortationResId = R.array.waste_type_sortation_paper,
        recyclingResId = R.string.waste_type_recycling_paper
    ),
    PLASTIC(
        nameResId = R.string.waste_type_plastic,
        imageResId = R.drawable.thumbnail_waste_plastic,
        descriptionResId = R.string.waste_type_description_plastic,
        explanationResId = R.string.waste_type_explanation_plastic,
        sortationResId = R.array.waste_type_sortation_plastic,
        recyclingResId = R.string.waste_type_recycling_plastic
    ),
    UNKNOWN(
        nameResId = R.string.waste_type_unknown,
        imageResId = R.drawable.question_mark_24,
        descriptionResId = R.string.waste_type_description_unknown,
        explanationResId = R.string.waste_type_explanation_unknown,
        sortationResId = R.array.waste_type_sortation_unknown,
        recyclingResId = R.string.waste_type_recycling_unknown
    );
}

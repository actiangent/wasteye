package com.actiangent.wasteye.extension

import android.text.SpannableString
import android.text.Spanned
import android.text.style.BulletSpan

fun buildBulletFromArraySpannableString(points: Array<String>): SpannableString {
    val pointsString = points.reduce { accumulator, point -> accumulator + "\n" + point }
    val spannableString = SpannableString(pointsString)

    var spanStart = 0
    points.forEach { point ->
        spannableString.setSpan(
            BulletSpan(24),
            spanStart,
            spanStart + point.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spanStart += point.length + 1
    }

    return spannableString
}
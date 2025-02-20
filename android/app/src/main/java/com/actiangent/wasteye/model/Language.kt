package com.actiangent.wasteye.model

import java.util.Locale

enum class Language(val locale: Locale) {
    ENGLISH(Locale.ENGLISH),
    BAHASA_INDONESIA(Locale("id"));

    override fun toString(): String {
        // capitalize each word
        return name
            .split("_")
            .joinToString(" ") { word ->
                val range = 1 until word.length
                word.replaceRange(
                    range = range,
                    replacement = word.substring(range).lowercase()
                )
            }
    }
}
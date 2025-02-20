package com.actiangent.wasteye.model

data class UserData(
    val showDetectionScore: Boolean = false,
    val languagePreference: Language = Language.ENGLISH,
)

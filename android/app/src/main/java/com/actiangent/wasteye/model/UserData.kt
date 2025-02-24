package com.actiangent.wasteye.model

data class UserData(
    val isShowDetectionScore: Boolean = false,
    val languagePreference: Language = Language.ENGLISH,
)

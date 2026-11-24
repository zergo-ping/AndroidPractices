package com.example.androidpractise.domain.model

data class Profile(
    val fullName: String = "",
    val avatarUri: String = "",
    val resumeUrl: String = "",
    val classTime: String = ""
)

private val classTimePattern = Regex("(?:[01][0-9]|2[0-3]):[0-5][0-9]")
fun isValidClassTime(value: String) = classTimePattern.matches(value)

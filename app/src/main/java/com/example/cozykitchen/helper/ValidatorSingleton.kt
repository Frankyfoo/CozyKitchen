package com.example.cozykitchen.helper

import java.util.regex.Matcher
import java.util.regex.Pattern

object ValidatorSingleton {
    private val emailPattern: Pattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    private val minLengthPattern: Pattern = Pattern.compile("^.{8,}$")

    fun isValidEmail(email: String): Boolean {
        val emailMatcher: Matcher = emailPattern.matcher(email)
        return emailMatcher.matches()
    }

    fun isValidPasswordLength(password: String): Boolean {
        val passwordLengthChecker: Matcher = minLengthPattern.matcher(password)
        return passwordLengthChecker.matches()
    }
}
package com.example.cozykitchen.helper

import android.service.autofill.FieldClassification.Match
import java.util.regex.Matcher
import java.util.regex.Pattern

object ValidatorSingleton {
    private val emailPattern: Pattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    private val malaysiaPhoneNumberPattern: Pattern = Pattern.compile("^(?:\\+?6?01)[0-46-9]-*[0-9]{7,8}$")
    private val minLengthPattern: Pattern = Pattern.compile("^.{8,}$")
    private val minCardLengthPattern: Pattern = Pattern.compile("^.{16}$")
    private val minCvcLengthPattern: Pattern = Pattern.compile("^.{3}$")

    fun isValidEmail(email: String): Boolean {
        val emailMatcher: Matcher = emailPattern.matcher(email)
        return emailMatcher.matches()
    }

    fun isValidPasswordLength(password: String): Boolean {
        val passwordLengthChecker: Matcher = minLengthPattern.matcher(password)
        return passwordLengthChecker.matches()
    }

    fun isValidCardNumberLength(number: String): Boolean {
        val cardNumberLengthChecker: Matcher = minCardLengthPattern.matcher(number)
        return cardNumberLengthChecker.matches()
    }

    fun isValidCvcCodeLength(cvc: String): Boolean {
        val cvcCodeLengthChecker: Matcher = minCvcLengthPattern.matcher(cvc)
        return cvcCodeLengthChecker.matches()
    }

    fun isValidMalaysiaPhoneNumber(phoneNumber: String): Boolean {
        val phoneNumberChecker: Matcher = malaysiaPhoneNumberPattern.matcher(phoneNumber)
        return phoneNumberChecker.matches()
    }
}
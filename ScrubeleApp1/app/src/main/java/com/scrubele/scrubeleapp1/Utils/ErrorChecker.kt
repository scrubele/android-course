package com.scrubele.scrubeleapp1.Utils

import android.util.Patterns

object ErrorChecker {
    const val PHONE_PATTERN = "^[+]?[(]?[0-9]{1,4}[)]?[0-9]{9}"

    fun findInvalidData(
        email: String,
        name: String,
        phone: String
    ): Map<String, Boolean> {
        return mapOf(
            "emailTxt" to (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()),
            "phoneTxt" to (phone.isNotEmpty() && phone.matches(PHONE_PATTERN.run { toRegex() })),
            "nameTxt" to (name.isNotEmpty())
        ).filter { !it.value }
    }
}
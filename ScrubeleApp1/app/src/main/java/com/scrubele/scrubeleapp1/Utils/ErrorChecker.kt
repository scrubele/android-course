package com.scrubele.scrubeleapp1.Utils

import android.util.Patterns

object ErrorChecker {
    const val PHONE_PATTERN = "^[+]?[(]?[0-9]{1,4}[)]?[0-9]{9}"
    const val PASSWORD_PATTERN = ".{8,}"

    fun findInvalidData(
        email: String = "abc@gmail.com",
        password: String = "1234567890",
        name: String = "a",
        phone: String = "1234567890"
    ): Map<String, Boolean> {
        return mapOf(
            "emailTxt" to (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()),
            "passwordTxt" to (password.isNotEmpty() && password.matches(PASSWORD_PATTERN.run { toRegex() })),
            "phoneTxt" to (phone.isNotEmpty() && phone.matches(PHONE_PATTERN.run { toRegex() })),
            "nameTxt" to (name.isNotEmpty())
        ).filter { !it.value }
    }

}
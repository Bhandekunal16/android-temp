package com.example.myapp.vault

import com.nulabinc.zxcvbn.Zxcvbn

enum class PasswordStrength {
    WEAK,
    MEDIUM,
    STRONG,
}

data class StrengthResult(
    val strength: PasswordStrength,
    val score: Int,
)

fun evaluatePassword(password: String): StrengthResult {
    val result = Zxcvbn().measure(password)

    val strength =
        when (result.score) {
            0, 1 -> PasswordStrength.WEAK
            2, 3 -> PasswordStrength.MEDIUM
            4 -> PasswordStrength.STRONG
            else -> PasswordStrength.WEAK
        }

    return StrengthResult(
        strength = strength,
        score = result.score,
    )
}

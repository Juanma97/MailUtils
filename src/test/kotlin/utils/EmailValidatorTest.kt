package utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EmailValidatorTest {

    @Test
    fun should_validate_emails() {
        assertFalse(EmailValidator.isEmailValid("Julia.abc@"))
        assertFalse(EmailValidator.isEmailValid("Samantha@com"))
        assertFalse(EmailValidator.isEmailValid("Samantha_21."))
        assertFalse(EmailValidator.isEmailValid(".1Samantha"))
        assertFalse(EmailValidator.isEmailValid("Samantha@10_2A"))
        assertFalse(EmailValidator.isEmailValid("JuliaZ007"))
        assertTrue(EmailValidator.isEmailValid("Julia@007.com"))
        assertFalse(EmailValidator.isEmailValid("_Julia007.com"))
        assertFalse(EmailValidator.isEmailValid("_Julia007@abc.co.in"))
        assertTrue(EmailValidator.isEmailValid("Julia.007@abc.com"))
    }
}
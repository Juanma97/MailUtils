package request

import exceptions.EmptyFieldException
import exceptions.NotValidEmailException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import request.ContentRequest
import request.EmailRequest
import request.MailRequest

class MailRequestTest {

    private val emailFrom = EmailRequest.Builder().email("from@test.com").name("From").build()
    private val emailTo = EmailRequest.Builder().email("to@test.com").name("To").build()
    private val contentRequest = ContentRequest.Builder().value("Content").type("text/html").build()

    @Test
    fun should_throw_if_email_from_not_exists() {
        val mailRequest = MailRequest.Builder()
            .to(listOf(emailTo))
            .subject("Subject")
            .content(contentRequest)

        assertThrows<NotValidEmailException> { mailRequest.build() }
    }

    @Test
    fun should_throw_if_email_to_not_exists() {
        val mailRequest = MailRequest.Builder()
            .from(emailFrom)
            .subject("Subject")
            .content(contentRequest)

        assertThrows<NotValidEmailException> { mailRequest.build() }
    }

    @Test
    fun should_throw_if_email_not_have_subject() {
        val mailRequest = MailRequest.Builder()
            .to(listOf(emailTo))
            .from(emailFrom)
            .content(contentRequest)

        assertThrows<EmptyFieldException> { mailRequest.build() }
    }

    @Test
    fun should_throw_if_email_not_have_content() {
        val mailRequest = MailRequest.Builder()
            .to(listOf(emailTo))
            .from(emailFrom)
            .subject("Subject")

        assertThrows<EmptyFieldException> { mailRequest.build() }
    }

}
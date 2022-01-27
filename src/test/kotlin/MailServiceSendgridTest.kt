import api.ApiKey
import exceptions.EmptyFieldException
import exceptions.NotFoundApiKeyException
import exceptions.NotValidEmailException
import motherobjects.MailRequestMother
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import request.*
import kotlin.test.assertEquals

private const val OK_STATUS_CODE = 200
private const val ERROR_STATUS_CODE = 400
private const val UNAUTHORIZED_STATUS_CODE = 401
private const val BAD_STRUCTURE_STATUS_CODE = 403

private const val API_KEY_INCORRECT = "API KEY"

internal class MailServiceSendgridTest {

    private val API_KEY = ApiKey.API_KEY
    private val sut: MailService = MailServiceSendgrid()
    private val emailFrom = EmailRequest.Builder().email("from@test.com").name("From").build()
    private val emailTo = EmailRequest.Builder().email("to@test.com").name("To").build()
    private val contentRequest = ContentRequest.Builder().value("Content").type("text/html").build()

    private val mailRequest = MailRequestMother.createSimpleMailRequestBuilder(emailFrom, emailTo, contentRequest)

    @Test
    fun should_throw_if_api_key_is_empty() {
        sut.createMail(mailRequest.build())
        assertThrows<NotFoundApiKeyException> { sut.initializeService("") }
    }

    @Test
    fun should_not_throw_if_api_key_is_not_empty() {
        sut.initializeService(API_KEY_INCORRECT)
        sut.createMail(mailRequest.build())
        assertDoesNotThrow { sut.sendMail(false) }
    }

    @Test
    fun should_send_simple_email_test_if_structure_is_correct() {
        sut.initializeService(API_KEY)
        sut.createMail(mailRequest.build())
        val response = sut.sendMail(true)

        assertEquals(OK_STATUS_CODE, response.statusCode)
    }

    @Test
    fun should_return_unauthorized_status_code_if_api_key_is_not_correct() {
        sut.initializeService(API_KEY_INCORRECT)
        sut.createMail(mailRequest.build())
        val response = sut.sendMail(false)

        assertEquals(UNAUTHORIZED_STATUS_CODE, response.statusCode)
    }

    @Test
    fun should_throw_email_error_if_from_email_is_not_valid() {
        sut.initializeService(API_KEY)
        mailRequest.from = EmailRequest.Builder().email("test.com").name("test").build()
        assertThrows<NotValidEmailException> { sut.createMail(mailRequest.build()) }
    }

    @Test
    fun should_throw_email_error_if_to_email_is_not_valid() {
        sut.initializeService(API_KEY)
        mailRequest.to = EmailRequest.Builder().email("test.com").name("test").build()
        assertThrows<NotValidEmailException> { sut.createMail(mailRequest.build()) }
    }

    @Test
    fun should_send_email_with_attachments() {
        sut.initializeService(API_KEY)

        val base64Content = "dGVzdA=="
        val image = AttachmentRequest.Builder().content(base64Content).type("image/png").filename("image.png").build()
        val pdf = AttachmentRequest.Builder().content(base64Content).type("text/html").filename("test.pdf").build()

        mailRequest.attachments(listOf(image, pdf))
        sut.createMail(mailRequest.build())
        val response = sut.sendMail(true)

        assertEquals(OK_STATUS_CODE, response.statusCode)
    }

    @Test
    fun should_return_bad_structure_code_if_attachment_content_is_not_base64() {
        sut.initializeService(API_KEY)

        val notBase64Content = "test"
        val image = AttachmentRequest.Builder().content(notBase64Content).type("image/png").filename("image.png").build()
        val pdf = AttachmentRequest.Builder().content(notBase64Content).type("text/html").filename("test.pdf").build()

        mailRequest.attachments(listOf(image, pdf))
        sut.createMail(mailRequest.build())
        val response = sut.sendMail(false)

        assertEquals(BAD_STRUCTURE_STATUS_CODE, response.statusCode)
    }

    @Test
    fun should_show_error_when_send_email_with_template_id_not_valid() {
        sut.initializeService(API_KEY)

        val templateRequest = TemplateRequest.Builder().templateId("14  ").build()
        mailRequest.templateRequest(templateRequest)

        sut.createMail(mailRequest.build())
        val response = sut.sendMail(true)

        assertEquals(ERROR_STATUS_CODE, response.statusCode)
    }

    @Test
    fun should_send_email_with_template_id_valid() {
        sut.initializeService(API_KEY)

        val templateRequest = TemplateRequest.Builder().templateId("d-6409e016383c405383cc9f57b634b977").build()
        mailRequest.templateRequest(templateRequest)

        sut.createMail(mailRequest.build())
        val response = sut.sendMail(true)

        assertEquals(OK_STATUS_CODE, response.statusCode)
    }

    @Test
    fun should_send_email_with_template_attributes() {
        sut.initializeService(API_KEY)

        val templateAttributes = mapOf(Pair("username", "myName"))

        val templateRequest = TemplateRequest.Builder().templateId("d-6409e016383c405383cc9f57b634b977").templateData(templateAttributes).build()
        mailRequest.templateRequest(templateRequest)

        sut.createMail(mailRequest.build())
        val response = sut.sendMail(true)

        assertEquals(OK_STATUS_CODE, response.statusCode)
        // TODO: Mock verify??
    }
}
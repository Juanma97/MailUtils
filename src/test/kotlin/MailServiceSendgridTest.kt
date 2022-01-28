import api.ApiKey
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sendgrid.helpers.mail.Mail
import exceptions.NotFoundApiKeyException
import exceptions.NotValidEmailException
import motherobjects.EmailRequestMother
import motherobjects.MailRequestMother
import org.junit.jupiter.api.BeforeEach
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

private const val TEMPLATE_ID = "d-6409e016383c405383cc9f57b634b977"

internal class MailServiceSendgridTest {

    private val API_KEY = ApiKey.API_KEY
    private val sut: MailService = MailServiceSendgrid()
    val mapper = jacksonObjectMapper()
    private val emailFrom = EmailRequestMother.createEmailRequest("from@test.com", "from")
    private val emailTo = EmailRequestMother.createEmailRequest("to@test.com", "to")
    private val contentRequest = ContentRequest.Builder().value("Content").type("text/html").build()

    private val mailRequest = MailRequestMother.createSimpleMailRequestBuilder(emailFrom, emailTo, contentRequest)

    @BeforeEach
    fun setup() {
        sut.initializeService(API_KEY)
    }

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
        sut.createMail(mailRequest.build())
        val response = sut.sendMail(true)

        val mailSended: Mail = mapper.readValue(response.messageContent)

        assertEquals(OK_STATUS_CODE, response.statusCode)
        assertEquals(mailSended.from.email, mailRequest.from!!.email)
        assertEquals(mailSended.from.name, mailRequest.from!!.name)
        assertEquals(mailSended.content[0].value, mailRequest.content!!.value)
        assertEquals(mailSended.content[0].type, mailRequest.content!!.type)
        assertEquals(mailSended.subject, mailRequest.subject)
        assertEquals(mailSended.personalization[0].tos.size, 1)
        //assertEquals(mailSended.personalization[0].tos[0].email, mailRequest.to!!.email)
        //assertEquals(mailSended.personalization[0].tos[0].name, mailRequest.to!!.name)
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
        mailRequest.from = EmailRequest.Builder().email("test.com").name("test").build()
        assertThrows<NotValidEmailException> { sut.createMail(mailRequest.build()) }
    }

    @Test
    fun should_throw_email_error_if_to_email_is_not_valid() {
        mailRequest.to = listOf(EmailRequestMother.createEmailRequest("test.com", "test"))
        assertThrows<NotValidEmailException> { sut.createMail(mailRequest.build()) }
    }

    @Test
    fun should_send_email_with_attachments() {
        val base64Content = "dGVzdA=="
        val image = AttachmentRequest.Builder().content(base64Content).type("image/png").filename("image.png").build()
        val pdf = AttachmentRequest.Builder().content(base64Content).type("text/html").filename("test.pdf").build()

        mailRequest.attachments(listOf(image, pdf))
        sut.createMail(mailRequest.build())
        val response = sut.sendMail(true)

        val mailSended: Mail = mapper.readValue(response.messageContent)

        assertEquals(OK_STATUS_CODE, response.statusCode)
        assertEquals(mailSended.attachments.size, 2)
        assertEquals(mailSended.attachments[0].content, base64Content)
        assertEquals(mailSended.attachments[1].content, base64Content)
        assertEquals(mailSended.attachments[0].type, "image/png")
        assertEquals(mailSended.attachments[1].type, "text/html")
        assertEquals(mailSended.attachments[0].filename, "image.png")
        assertEquals(mailSended.attachments[1].filename, "test.pdf")

    }

    @Test
    fun should_return_bad_structure_code_if_attachment_content_is_not_base64() {
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
        val templateRequest = TemplateRequest.Builder().templateId("14  ").build()
        mailRequest.templateRequest(templateRequest)

        sut.createMail(mailRequest.build())
        val response = sut.sendMail(true)

        assertEquals(ERROR_STATUS_CODE, response.statusCode)
    }

    @Test
    fun should_send_email_with_template_id_valid() {
        val templateRequest = TemplateRequest.Builder().templateId(TEMPLATE_ID).build()
        mailRequest.templateRequest(templateRequest)

        sut.createMail(mailRequest.build())
        val response = sut.sendMail(true)

        val mailSended: Mail = mapper.readValue(sut.sendMail(true).messageContent)

        assertEquals(OK_STATUS_CODE, response.statusCode)
        assertEquals(mailSended.getTemplateId(), TEMPLATE_ID)
    }

    @Test
    fun should_send_email_with_template_attributes() {
        val templateAttributes = mapOf(Pair("username", "myName"))

        val templateRequest = TemplateRequest.Builder().templateId(TEMPLATE_ID).templateData(templateAttributes).build()
        mailRequest.templateRequest(templateRequest)

        sut.createMail(mailRequest.build())

        val mailSended: Mail = mapper.readValue(sut.sendMail(true).messageContent)

        assertEquals(mailSended.getTemplateId(), TEMPLATE_ID)
        assertEquals(mailSended.getPersonalization()[0].dynamicTemplateData.get("username"), "myName")
    }

    @Test
    fun should_send_email_with_multiple_recipients() {
        val emailList: List<EmailRequest> = listOf(
            EmailRequestMother.createEmailRequest("t0@test.com", "t0"),
            EmailRequestMother.createEmailRequest("t1@test.com", "t1"),
            EmailRequestMother.createEmailRequest("t2@test.com", "t2"),
            EmailRequestMother.createEmailRequest("t3@test.com", "t3")
        )

        mailRequest.to(emailList)

        sut.createMail(mailRequest.build())

        val mailSended: Mail = mapper.readValue(sut.sendMail(true).messageContent)

        assertEquals(4, mailSended.personalization[0].tos.size)
        for ((index, value) in mailSended.personalization[0].tos.withIndex()) {
            assertEquals(value.email, "t${index}@test.com")
        }
    }
}
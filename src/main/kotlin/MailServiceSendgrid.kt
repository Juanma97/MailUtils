import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.SendGrid
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Attachments
import com.sendgrid.helpers.mail.objects.MailSettings
import com.sendgrid.helpers.mail.objects.Email
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Setting
import exceptions.NotFoundApiKeyException
import exceptions.NotValidEmailException
import exception.enum.ExceptionMessages.NOT_FOUND_OR_INCORRECT_API_KEY
import exception.enum.ExceptionMessages.EMAIL_IS_NOT_VALID_OR_EMPTY
import request.EmailRequest
import request.MailRequest
import request.ContentRequest
import request.AttachmentRequest
import request.TemplateRequest
import response.MailResponse
import utils.EmailValidator.Companion.isEmailValid

private const val MAIL_SEND_ENDPOINT = "mail/send"

class MailServiceSendgrid: MailService {
    private lateinit var sendgrid: SendGrid

    private lateinit var mail: Mail
    private val mailSettings = MailSettings()

    override fun initializeService(apiKey: String) {
        if (apiKey.isEmpty()) throw NotFoundApiKeyException(NOT_FOUND_OR_INCORRECT_API_KEY.message)

        sendgrid = SendGrid(apiKey)
    }

    override fun sendMail(testMode: Boolean): MailResponse {
        if (testMode) enableTestMode()

        val request = Request()
        request.method = Method.POST
        request.endpoint = MAIL_SEND_ENDPOINT
        request.body = mail.build()

        val response = sendgrid.api(request)

        return MailResponse.Builder()
            .statusCode(response.statusCode)
            .message(response.body)
            .messageContent(request.body)
            .build()
    }

    override fun createMail(mailRequest: MailRequest) {
        mail = Mail(convertToEmailSendgrid(mailRequest.from),
                    mailRequest.subject,
                    convertToEmailSendgrid(mailRequest.to[0]),
                    convertToContentSendgrid(mailRequest.content))

        if (mailRequest.to.size > 1) {
            for (email in mailRequest.to.subList(1, mailRequest.to.size))
            mail.personalization[0].addTo(convertToEmailSendgrid(email))
        }

        if (mailRequest.attachments != null) {
            addAttachmentsToMail(mailRequest.attachments!!)
        }

        if (mailRequest.templateRequest != null) {
            addTemplateData(mailRequest.templateRequest)
        }

        if (mailRequest.bcc != null) {
            mail.personalization[0].addBcc(convertToEmailSendgrid(mailRequest.bcc!!))
        }

        if (mailRequest.cc != null) {
            mail.personalization[0].addCc(convertToEmailSendgrid(mailRequest.cc!!))
        }

        mail.mailSettings = mailSettings
    }

    private fun convertToEmailSendgrid(emailRequest: EmailRequest): Email {
        if (!isEmailValid(emailRequest.email)) throw NotValidEmailException(EMAIL_IS_NOT_VALID_OR_EMPTY.message)
        return Email(emailRequest.email, emailRequest.name)
    }

    private fun convertToContentSendgrid(contentRequest: ContentRequest): Content {
        return Content(contentRequest.type, contentRequest.value)
    }

    private fun addAttachmentsToMail(attachmentsRequest: List<AttachmentRequest>) {
        for (attachment in attachmentsRequest) {
            val attachments = Attachments()
            attachments.content = attachment.content
            attachments.type = attachment.type
            attachments.filename = attachment.filename

            mail.addAttachments(attachments)
        }
    }

    private fun addTemplateData(templateRequest: TemplateRequest?) {
        mail.templateId = templateRequest!!.templateId

        if (templateRequest.templateData != null) {
            for((key, value) in templateRequest.templateData!!) {
                mail.personalization[0].addDynamicTemplateData(key, value)
            }
        }
    }

    private fun enableTestMode() {
        val sandboxSetting = Setting()
        sandboxSetting.enable = true
        mailSettings.setSandboxMode(sandboxSetting)
    }
}
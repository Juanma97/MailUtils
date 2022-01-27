import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.SendGrid
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.*
import exceptions.NotFoundApiKeyException
import exceptions.NotValidEmailException
import exception.enum.ExceptionMessages.NOT_FOUND_OR_INCORRECT_API_KEY
import request.*
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

        return MailResponse.Builder().statusCode(response.statusCode).message(response.body).build()
    }

    override fun createMail(mailRequest: MailRequest) {
        // Email básico: from, to, subject, content
        mail = Mail(convertToEmailSendgrid(mailRequest.from),
                    mailRequest.subject,
                    convertToEmailSendgrid(mailRequest.to),
                    convertToContentSendgrid(mailRequest.content))

        // Email con adjuntos
        if (mailRequest.attachments != null) {
            addAttachmentsToMail(mailRequest.attachments!!)
        }

        // Email con plantilla
        if (mailRequest.templateRequest != null) {
            addTemplateData(mailRequest.templateRequest)
        }

        // Email con ajustes
        mail.mailSettings = mailSettings

        // Email con diferentes personalizaciones


        // Email con múltiples destinatarios

    }

    private fun convertToEmailSendgrid(emailRequest: EmailRequest): Email {
        if (!isEmailValid(emailRequest.email)) throw NotValidEmailException("Email from is not valid")
        return Email(emailRequest.email, emailRequest.name)
    }

/*    private fun validateEmails(emailFrom: EmailRequest?, emailTo: EmailRequest?) {
        if (!isEmailValid(emailFrom!!.email)) throw NotValidEmailException("Email from is not valid")
        if (!isEmailValid(emailTo!!.email)) throw NotValidEmailException("Email to is not valid")
    }

    private fun addEmailsToMail(from: EmailRequest, to: EmailRequest) {
        emailFrom = Email(from.email, from.name)
        emailTo = Email(to.email, to.name)
    }*/

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
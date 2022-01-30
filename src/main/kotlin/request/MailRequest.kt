package request

import exception.enum.ExceptionMessages.EMAIL_FROM_IS_NULL
import exception.enum.ExceptionMessages.EMAIL_TO_IS_NULL
import exception.enum.ExceptionMessages.EMAIL_SUBJECT_IS_NULL
import exception.enum.ExceptionMessages.EMAIL_CONTENT_IS_NULL
import exceptions.EmptyFieldException
import exceptions.NotValidEmailException

data class MailRequest private constructor(
    var from: EmailRequest,
    var to: List<EmailRequest>,
    var subject: String,
    var content: ContentRequest,
    var bcc: EmailRequest?,
    var cc: EmailRequest?,
    var attachments: List<AttachmentRequest>?,
    var templateRequest: TemplateRequest?) {

    data class Builder(
        var from: EmailRequest? = null,
        var to: List<EmailRequest>? = null,
        var subject: String? = null,
        var content: ContentRequest? = null,
        var bcc: EmailRequest? = null,
        var cc: EmailRequest? = null,
        var attachments: List<AttachmentRequest>? = null,
        var templateRequest: TemplateRequest? = null) {

        fun from(from: EmailRequest) = apply { this.from = from }
        fun to(to: List<EmailRequest>) = apply { this.to = to }
        fun subject(subject: String) = apply { this.subject = subject }
        fun content(content: ContentRequest) = apply { this.content = content }
        fun attachments(attachments: List<AttachmentRequest>) = apply { this.attachments = attachments }
        fun templateRequest(templateRequest: TemplateRequest) = apply { this.templateRequest = templateRequest }
        fun bcc(bcc: EmailRequest) = apply { this.bcc = bcc }
        fun cc(cc: EmailRequest) = apply { this.cc = cc }

        fun build(): MailRequest {
            if (from == null) throw NotValidEmailException(EMAIL_FROM_IS_NULL.message)
            if (to.isNullOrEmpty()) throw NotValidEmailException(EMAIL_TO_IS_NULL.message)

            if (subject.isNullOrBlank()) throw EmptyFieldException(EMAIL_SUBJECT_IS_NULL.message)
            if (content == null) throw EmptyFieldException(EMAIL_CONTENT_IS_NULL.message)

            return MailRequest(from!!, to!!, subject!!, content!!, bcc, cc, attachments, templateRequest)
        }


    }
}
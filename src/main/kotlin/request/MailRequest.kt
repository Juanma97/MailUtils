package request

import exceptions.EmptyFieldException
import exceptions.NotValidEmailException

data class MailRequest private constructor(
    var from: EmailRequest,
    var to: List<EmailRequest>,
    var subject: String,
    var content: ContentRequest,
    var attachments: List<AttachmentRequest>?,
    var templateRequest: TemplateRequest?) {

    data class Builder(
        var from: EmailRequest? = null,
        var to: List<EmailRequest>? = null,
        var subject: String? = null,
        var content: ContentRequest? = null,
        var attachments: List<AttachmentRequest>? = null,
        var templateRequest: TemplateRequest? = null) {

        fun from(from: EmailRequest) = apply { this.from = from }
        fun to(to: List<EmailRequest>) = apply { this.to = to }
        fun subject(subject: String) = apply { this.subject = subject }
        fun content(content: ContentRequest) = apply { this.content = content }
        fun attachments(attachments: List<AttachmentRequest>) = apply { this.attachments = attachments }
        fun templateRequest(templateRequest: TemplateRequest) = apply { this.templateRequest = templateRequest }

        fun build(): MailRequest {
            if (from == null) throw NotValidEmailException("Email from is null")
            if (to.isNullOrEmpty()) throw NotValidEmailException("Email to is null")

            if (subject.isNullOrBlank()) throw EmptyFieldException("Subject of mail is null")
            if (content == null) throw EmptyFieldException("Content of mail is null")

            return MailRequest(from!!, to!!, subject!!, content!!, attachments, templateRequest)
        }
    }
}
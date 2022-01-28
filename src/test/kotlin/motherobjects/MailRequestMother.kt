package motherobjects

import request.ContentRequest
import request.EmailRequest
import request.MailRequest

internal object MailRequestMother {

    fun createSimpleMailRequestBuilder(emailFrom: EmailRequest,
                                       emailTo: EmailRequest,
                                       contentRequest: ContentRequest): MailRequest.Builder {
        return MailRequest.Builder()
            .from(emailFrom)
            .to(listOf(emailTo))
            .subject("Subject")
            .content(contentRequest)
    }
}
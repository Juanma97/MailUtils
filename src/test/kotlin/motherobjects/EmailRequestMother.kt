package motherobjects

import request.EmailRequest

object EmailRequestMother {

    fun createEmailRequest(email: String, name: String): EmailRequest {
        return EmailRequest.Builder()
            .email(email)
            .name(name)
            .build()
    }
}
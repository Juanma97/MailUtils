import request.MailRequest
import response.MailResponse

interface MailService {

    fun initializeService(apiKey: String)
    fun createMail(mailRequest: MailRequest)
    fun sendMail(testMode: Boolean): MailResponse
}
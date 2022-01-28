package response

class MailResponse private constructor(
    var statusCode: Int,
    var message: String,
    var messageContent: String) {

    data class Builder(
        var statusCode: Int? = null,
        var message: String? = null,
        var messageContent: String? = null) {

        fun statusCode(statusCode: Int) = apply { this.statusCode = statusCode }
        fun message(message: String) = apply { this.message = message }
        fun messageContent(messageContent: String) = apply { this.messageContent = messageContent }
        fun build() = MailResponse(statusCode!!, message!!, messageContent!!)

    }
}
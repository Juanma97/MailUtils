package response

class MailResponse private constructor(
    var statusCode: Int,
    var message: String) {

    data class Builder(
        var statusCode: Int? = null,
        var message: String? = null) {

        fun statusCode(statusCode: Int) = apply { this.statusCode = statusCode }
        fun message(message: String) = apply { this.message = message }
        fun build() = MailResponse(statusCode!!, message!!)

    }
}
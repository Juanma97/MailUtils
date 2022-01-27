package request

class EmailRequest private constructor(
    var email: String,
    var name: String) {

    data class Builder(
        var email: String? = null,
        var name: String? = null) {

        fun email(email: String) = apply { this.email = email }
        fun name(name: String) = apply { this.name = name }
        fun build() = EmailRequest(email!!, name!!)
    }
}
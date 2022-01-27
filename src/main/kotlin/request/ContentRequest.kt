package request

class ContentRequest private constructor(
    var value: String,
    var type: String) {

    data class Builder(
        var value: String? = null,
        var type: String? = null) {

        fun value(value: String) = apply { this.value = value }
        fun type(type: String) = apply { this.type = type }
        fun build() = ContentRequest(value!!, type!!)
    }
}
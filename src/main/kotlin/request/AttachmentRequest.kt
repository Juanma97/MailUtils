package request

import exceptions.EmptyFieldException

class AttachmentRequest private constructor(
    var content: String,
    var filename: String,
    var type: String?) {

    data class Builder(
        var content: String? = null,
        var filename: String? = null,
        var type: String? ? = null) {

        fun content(content: String) = apply { this.content = content }
        fun filename(filename: String) = apply { this.filename = filename }
        fun type(type: String) = apply { this.type = type }
        fun build(): AttachmentRequest{
            if (content == null) throw EmptyFieldException("Content of attachment is null")
            if (filename == null) throw EmptyFieldException("Filename of attachment is null")

            return AttachmentRequest(content!!, filename!!, type)
        }
    }
}
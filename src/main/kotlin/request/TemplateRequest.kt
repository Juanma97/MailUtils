package request

class TemplateRequest private constructor(
    var templateId: String,
    var templateData: Map<String, String>?) {

    data class Builder(
        var templateId: String? = null,
        var templateData: Map<String, String>? = null) {

        fun templateId(templateId:String) = apply { this.templateId = templateId }
        fun templateData(templateData:Map<String, String>) = apply { this.templateData = templateData }
        fun build() = TemplateRequest(templateId!!, templateData)
    }
}
package exception.enum

enum class ExceptionMessages(val message: String) {
    NOT_FOUND_OR_INCORRECT_API_KEY("Api key is empty or not exists"),
    EMAIL_IS_NOT_VALID_OR_EMPTY("Email is not valid or empty"),
    EMAIL_FROM_IS_NULL("Email from is null"),
    EMAIL_TO_IS_NULL("Email to is null"),
    EMAIL_SUBJECT_IS_NULL("Subject of mail is null"),
    EMAIL_CONTENT_IS_NULL("Content of mail is null")
}
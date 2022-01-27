package exception.enum

enum class ExceptionMessages(val message: String) {
    NOT_FOUND_OR_INCORRECT_API_KEY("Api key is empty or not exists"),
    EMAIL_IS_NOT_VALID_OR_EMPTY("Email is not valid or empty")
}
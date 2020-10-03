package qlik.qliktest.validator

import qlik.qliktest.dto.Request
import qlik.qliktest.exception.BadRequestException

class RequestValidator : Validator<Request> {

    override fun validate(request: Request) {
        if (request.messageId.isNullOrEmpty()) {
            throw BadRequestException("MessageId cannot be empty or null")
        }
    }
}

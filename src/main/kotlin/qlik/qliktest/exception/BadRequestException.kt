package qlik.qliktest.exception

class BadRequestException(val error: String) : RuntimeException("Bad request: $error")
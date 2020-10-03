package qlik.qliktest.exception

class MessageNotFoundException(val error: String) : RuntimeException(error)
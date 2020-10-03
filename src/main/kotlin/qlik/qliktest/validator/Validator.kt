package qlik.qliktest.validator

interface Validator<T> {
    fun validate(obj: T)
}
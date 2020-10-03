package qlik.qliktest.checker

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PalindromeCheckerTest {

    private val sut = PalindromeChecker()

    @Test
    fun `GIVEN an empty message text THEN PalindromeChecker returns true`() {
        assertThat(sut.check("")).isTrue()
    }

    @Test
    fun `GIVEN an valid Palindromic message text of even length THEN PalindromeChecker returns true`() {
        assertThat(sut.check("abba")).isTrue()
    }

    @Test
    fun `GIVEN an valid Palindromic message text of odd length THEN PalindromeChecker returns true`() {
        assertThat(sut.check("racecar")).isTrue()
    }

    @Test
    fun `GIVEN an valid alphaNumeric Palindromic message text THEN PalindromeChecker returns true`() {
        assertThat(sut.check("10a2a01")).isTrue()
    }

    @Test
    fun `GIVEN an valid commas, hashes, hyphens and dot Palindromic message text THEN PalindromeChecker returns true`() {
        assertThat(sut.check(",#..#,")).isTrue()
    }

    @Test
    fun `GIVEN an hashes valid iPalindromic message text THEN PalindromeChecker returns false`() {
        assertThat(sut.check("a#bbb#a")).isTrue()
    }

    @Test
    fun `GIVEN an valid non Palindromic message text THEN PalindromeChecker returns false`() {
        assertThat(sut.check("racetcar")).isFalse()
    }
}

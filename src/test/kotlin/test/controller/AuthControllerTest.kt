package test.controller

import com.reidswan.reidit.common.AccountResult
import com.reidswan.reidit.controllers.AuthController
import com.reidswan.reidit.controllers.SALT_LENGTH
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class AuthControllerTest {

    @Test
    fun `test generated salt has the correct length`() {
        Assertions.assertEquals(AuthController.generateSalt().length, SALT_LENGTH)
    }

    @Test
    fun `test hashPassword`() {
        val src = "somepassword1"
        val salt = "somesalt"
        Assertions.assertEquals(
            AuthController.hashPassword(src, salt),
            AuthController.hashPassword(src, salt))
    }

    @Test
    fun `test generateJWT succeeds on properly encoded JWT`() {
        val account = AccountResult(
            1,
            "test",
            "test@example.com",
            false,
            "somefakehash",
            "somefakesalt")
        val jwt = AuthController.generateJWT(account)
        Assertions.assertDoesNotThrow { AuthController.jwtDecoder.verify(jwt) }
    }

    @Test
    fun `test generateJWT fails on fake JWT`() {
        val jwt = "somefakejwt.haha.no"
        Assertions.assertThrows(Exception::class.java){ AuthController.jwtDecoder.verify(jwt) }
    }
}

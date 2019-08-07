package test.controller

import com.reidswan.reidit.common.AccountResult
import com.reidswan.reidit.common.HttpException
import com.reidswan.reidit.controllers.AccountsController
import com.reidswan.reidit.controllers.AuthController
import com.reidswan.reidit.data.queries.AccountsQueries
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AccountControllerTest {
    @Test
    fun `test emailExists returns false if email does not exist`() {
        val mockAccountsQueries = mockk<AccountsQueries>()
        every { mockAccountsQueries.getAccountByEmail(any()) } returns null
        val controller = AccountsController(mockAccountsQueries)
        Assertions.assertFalse(controller.emailExists("test@example.com"))
    }

    @Test
    fun `test emailExists returns true if email exists`() {
        val mockAccountsQueries = mockk<AccountsQueries>()
        every { mockAccountsQueries.getAccountByEmail(any()) } returns AccountResult(
            1, "test", "test@example.com", false,
            "testPasswordHash", "testPasswordSalt"
        )
        val controller = AccountsController(mockAccountsQueries)
        Assertions.assertTrue(controller.emailExists("test@example.com"))
    }

    @Test
    fun `test usernameExists returns false if username does not exist`() {
        val mockAccountsQueries = mockk<AccountsQueries>()
        every { mockAccountsQueries.getAccountByUsername(any()) } returns null
        val controller = AccountsController(mockAccountsQueries)
        Assertions.assertFalse(controller.usernameExists("someUsername"))
    }

    @Test
    fun `test usernameExists returns true if username exists`() {
        val mockAccountsQueries = mockk<AccountsQueries>()
        every { mockAccountsQueries.getAccountByUsername(any()) } returns AccountResult(
            1, "someUsername", "test@example.com", false,
            "testPasswordHash", "testPasswordSalt"
        )
        val controller = AccountsController(mockAccountsQueries)
        Assertions.assertTrue(controller.usernameExists("someUsername"))
    }

    @Test
    fun `test createAccount fails if email invalid`() {
        val mockAccountsQueries = mockk<AccountsQueries>()
        every { mockAccountsQueries.createAccount(any(), any(), any(), any()) } returns null
        every { mockAccountsQueries.getAccountByEmail(any()) } returns null
        every { mockAccountsQueries.getAccountByUsername(any()) } returns null
        val controller = AccountsController(mockAccountsQueries)
        val invalidEmail = "invalid@o"
        Assertions.assertThrows(HttpException::class.java){
            controller.createAccount("uname", invalidEmail, "@98wfw3598HUIhiivbvsihui")
        }
    }

    @Test
    fun `test createAccount fails if username taken`() {
        val mockAccountsQueries = mockk<AccountsQueries>()
        every { mockAccountsQueries.createAccount(any(), any(), any(), any()) } returns null
        every { mockAccountsQueries.getAccountByEmail(any()) } returns null
        every { mockAccountsQueries.getAccountByUsername(any()) } returns AccountResult(
            1, "test", "test@example.com", false, "hash", "salt"
        )
        val controller = AccountsController(mockAccountsQueries)
        Assertions.assertThrows(HttpException::class.java){
            controller.createAccount("uname", "test@example.com", "@98wfw3598HUIhiivbvsihui")
        }
    }

    @Test
    fun `test createAccount fails if password invalid`() {
        val mockAccountsQueries = mockk<AccountsQueries>()
        every { mockAccountsQueries.createAccount(any(), any(), any(), any()) } returns null
        every { mockAccountsQueries.getAccountByEmail(any()) } returns null
        every { mockAccountsQueries.getAccountByUsername(any()) } returns null
        val controller = AccountsController(mockAccountsQueries)
        Assertions.assertThrows(HttpException::class.java){
            controller.createAccount("uname", "test@example.com", "badpwd")
        }
    }

    @Test
    fun `test createAccount succeeds on valid input`() {
        val mockAccountsQueries = mockk<AccountsQueries>()
        every { mockAccountsQueries.createAccount(any(), any(), any(), any()) } returns null
        every { mockAccountsQueries.getAccountByEmail(any()) } returns null
        every { mockAccountsQueries.getAccountByUsername(any()) } returns null
        val controller = AccountsController(mockAccountsQueries)
        Assertions.assertDoesNotThrow {
            controller.createAccount("someuname", "test@example.com", "@98wfw3598HUIhiivbvsihui")
        }
    }

    @Test
    fun `test login gets account using email if email address provided`() {
        val testPassword = "testpassword"
        val testPasswordSalt = "salt"
        val testPasswordHash = AuthController.hashPassword(testPassword, testPasswordSalt)
        val mockAccountsQueries = mockk<AccountsQueries>()
        every { mockAccountsQueries.getAccountByEmail(any()) } returns AccountResult(
            1, "username", "test@example.com", false, testPasswordHash, testPasswordSalt)
        every { mockAccountsQueries.getAccountByUsername(any()) } returns null
        val controller = AccountsController(mockAccountsQueries)
        Assertions.assertNotNull(controller.login("test@example.com", testPassword))
    }

    @Test
    fun `test login gets account using username if non email provided`() {
        val testPassword = "testpassword"
        val testPasswordSalt = "salt"
        val testPasswordHash = AuthController.hashPassword(testPassword, testPasswordSalt)
        val mockAccountsQueries = mockk<AccountsQueries>()
        every { mockAccountsQueries.getAccountByEmail(any()) } returns null
        every { mockAccountsQueries.getAccountByUsername(any()) } returns AccountResult(
            1, "username", "not_an_email", false, testPasswordHash, testPasswordSalt)
        val controller = AccountsController(mockAccountsQueries)
        Assertions.assertNotNull(controller.login("not_an_email", testPassword))
    }
}

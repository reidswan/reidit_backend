package test.controller

import com.reidswan.reidit.common.AccountResult
import com.reidswan.reidit.controllers.AccountsController
import com.reidswan.reidit.data.queries.AccountsQueries
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AccountControllerTest {
    //
    // fun clearDatabase() {
    //     transaction(Configuration.dependencies.database) {
    //         val conn = TransactionManager.current().connection
    //         val statement = conn.createStatement()
    //         listOf("community", "account", "post", "vote", "comment", "subscription", "email_verification").forEach{
    //             statement.execute("TRUNCATE TABLE $it CASCADE;")
    //         }
    //     }
    // }

    @Test
    fun `test emailExists returns false if email does not exist`() {
        val mockAccountsQueries = mockk<AccountsQueries>()
        every { mockAccountsQueries.getAccountByEmail(any(), any()) } returns null
        val controller = AccountsController(mockAccountsQueries)
        Assertions.assertFalse(controller.emailExists("test@example.com"))
    }

    @Test
    fun `test emailExists returns true if email exists`() {
        val mockAccountsQueries = mockk<AccountsQueries>()
        every { mockAccountsQueries.getAccountByEmail(any(), any()) } returns AccountResult(
            1, "test", "test@example.com", false,
            "testPasswordHash", "testPasswordSalt"
        )
        val controller = AccountsController(mockAccountsQueries)
        Assertions.assertTrue(controller.emailExists("test@example.com"))
    }

    @Test
    fun `test usernameExists returns false if username does not exist`() {
        val mockAccountsQueries = mockk<AccountsQueries>()
        every { mockAccountsQueries.getAccountByUsername(any(), any()) } returns null
        val controller = AccountsController(mockAccountsQueries)
        Assertions.assertFalse(controller.usernameExists("someUsername"))
    }

    @Test
    fun `test usernameExists returns true if username exists`() {
        val mockAccountsQueries = mockk<AccountsQueries>()
        every { mockAccountsQueries.getAccountByUsername(any(), any()) } returns AccountResult(
            1, "someUsername", "test@example.com", false,
            "testPasswordHash", "testPasswordSalt"
        )
        val controller = AccountsController(mockAccountsQueries)
        Assertions.assertTrue(controller.usernameExists("someUsername"))
    }

    @Test
    fun `test createAccount fails if email invalid`() {
        val mockAccountsQueries = mockk<AccountsQueries>()
        every { mockAccountsQueries.createAccount(any(), any(), any(), any(), any()) } returns Unit
        val controller = AccountsController(mockAccountsQueries)
    }
}
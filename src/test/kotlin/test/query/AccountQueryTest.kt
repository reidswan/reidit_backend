package test.query

import com.reidswan.reidit.config.Configuration
import com.reidswan.reidit.data.queries.AccountsQueries
import org.junit.jupiter.api.*


object AccountQueryTest {
    @BeforeEach
    fun fillDatabase() {
        Fixtures.fillDatabase()
    }

    @AfterEach
    fun clearDatabase() {
        Fixtures.clearDatabase()
    }

    @Test
    fun `test createAccount performs insertion`() {
        val queries = AccountsQueries(Configuration.dependencies.database)
        queries.createAccount("someusername", "test@example.com", "hash", "salt")
        val result = queries.getAccountByUsername("someusername")
        Assertions.assertNotNull(result)
        if (result == null) return
        Assertions.assertEquals(result.username, "someusername")
        Assertions.assertEquals(result.emailAddress, "test@example.com")
        Assertions.assertEquals(result.passwordHash, "hash")
        Assertions.assertEquals(result.passwordSalt, "salt")
    }

    @Test
    fun `test getAccountByUsername returns correct account`() {
        val queries = AccountsQueries(Configuration.dependencies.database)
        val result = queries.getAccountByUsername("account1")
        Assertions.assertNotNull(result)
        if (result == null) return
        Assertions.assertEquals(result.emailAddress, "account1@example.com")
        Assertions.assertEquals(result.username, "account1")
    }

    @Test
    fun `test getAccountByEmail returns correct account`() {
        val queries = AccountsQueries(Configuration.dependencies.database)
        val result = queries.getAccountByEmail("account1@example.com")
        Assertions.assertNotNull(result)
        if (result == null) return
        Assertions.assertEquals(result.emailAddress, "account1@example.com")
        Assertions.assertEquals(result.username, "account1")
    }
}

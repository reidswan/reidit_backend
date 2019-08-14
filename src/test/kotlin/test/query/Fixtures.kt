package test.query

import com.reidswan.reidit.config.Configuration
import com.reidswan.reidit.data.Account
import com.reidswan.reidit.common.AccountResult
import com.reidswan.reidit.common.CommunityResult
import com.reidswan.reidit.data.Community
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

object Fixtures {
    var createdId: Int? = null

    fun clearDatabase() {
        val tables = listOf("account", "community", "post", "vote", "subscription", "comment", "email_verification")
        transaction (Configuration.dependencies.database) {
            val conn = TransactionManager.current().connection
            val statement = conn.createStatement()
            tables.forEach { statement.execute("TRUNCATE $it CASCADE") }
        }
    }

    fun fillDatabase() {
        transaction(Configuration.dependencies.database) {
            createdId = Account.batchInsert(listOf(
                AccountResult(1, "account1", "account1@example.com", false, "hash", "salt")))
            {
                this[Account.username] = it.username
                this[Account.emailAddress] = it.emailAddress
                this[Account.verified] = it.verified
                this[Account.passwordHash] = it.passwordHash
                this[Account.passwordSalt] = it.passwordSalt
            }.first().get(Account.accountId)

            Community.batchInsert(1 .. 10) {i ->
                this[Community.name] = "community_$i"
                this[Community.description] = "Community number $i"
                this[Community.createdBy] = createdId!!
            }
        }
    }

}
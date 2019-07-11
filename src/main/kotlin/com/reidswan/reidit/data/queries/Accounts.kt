/**
 * Functions querying accounts in the database
 */

package com.reidswan.reidit.data.queries

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import com.reidswan.reidit.data.Account

data class AccountResult (
    val username: String,
    val emailAddress: String?,
    val verified: Boolean,
    val passwordHash: String,
    val passwordSalt: String
)

fun ResultRow.toAccountResult(): AccountResult {
    return AccountResult(
        this[Account.username],
        this[Account.emailAddress],
        this[Account.verified],
        this[Account.passwordHash],
        this[Account.passwordSalt]
    )
}

object AccountsQueries {
    fun getAccountByUsername(db: Database, username: String): AccountResult? {
        return transaction(db) {
            Account.select { Account.username eq username }.firstOrNull()?.toAccountResult()
        }
    }

    fun getAccountByEmail(db: Database, emailAddress: String): AccountResult? {
        return transaction(db) {
            Account.select { Account.emailAddress eq emailAddress }.firstOrNull()?.toAccountResult()
        }
    }

}
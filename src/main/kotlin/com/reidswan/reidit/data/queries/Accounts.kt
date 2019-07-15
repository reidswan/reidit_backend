/**
 * Functions querying accounts in the database
 */

package com.reidswan.reidit.data.queries

import com.reidswan.reidit.common.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import com.reidswan.reidit.data.Account

fun ResultRow.toAccountResult(): AccountResult {
    return AccountResult(
        this[Account.accountId],
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

    fun createAccount(db: Database, username: String, emailAddress: String?, passwordHash: String, passwordSalt: String) {
        transaction(db) {
            Account.insert {
                it[Account.username] = username
                it[Account.emailAddress] = emailAddress
                it[Account.verified] = false
                it[Account.passwordHash] = passwordHash
                it[Account.passwordSalt] = passwordSalt
            }
        }
    }

}
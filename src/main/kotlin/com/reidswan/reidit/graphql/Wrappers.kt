package com.reidswan.reidit.graphql

import com.expedia.graphql.annotations.GraphQLContext
import com.expedia.graphql.annotations.GraphQLName
import com.reidswan.reidit.common.HttpException
import com.reidswan.reidit.common.PublicAccountResult
import com.reidswan.reidit.common.UserPrincipal
import com.reidswan.reidit.config.Configuration
import com.reidswan.reidit.controllers.AccountsController
import com.reidswan.reidit.data.queries.AccountsQueries
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authentication
import io.ktor.http.HttpStatusCode

class AccountQueries {
    val accountsController = AccountsController(AccountsQueries(Configuration.dependencies.database))

    fun emailExists(emailAddress: String): Boolean = accountsController.emailExists(emailAddress)
    fun usernameExists(username: String): Boolean = accountsController.usernameExists(username)
    fun getAccountByUsername(username: String): PublicAccountResult? {
        return accountsController.getAccountByUsername(username)?.toPublic()
    }
    fun getAccountByEmail(emailAddress: String): PublicAccountResult? {
        return accountsController.getAccountByEmail(emailAddress)?.toPublic()
    }

    fun me(@GraphQLContext context: ApplicationCall): PublicAccountResult {
        val loggedInUser = context.authentication.principal<UserPrincipal>() ?:
            throw HttpException("You must be logged in to access this resource", HttpStatusCode.Unauthorized)
        return getAccountByUsername(loggedInUser.username)!!
    }
}

class AccountMutations {
    val accountsController = AccountsController(AccountsQueries(Configuration.dependencies.database))
    fun createAccount(username: String, emailAddress: String?, password: String): PublicAccountResult? {
        return accountsController.createAccount(username, emailAddress, password)?.toPublic()
    }
}

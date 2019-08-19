package com.reidswan.reidit.graphql

import com.expedia.graphql.annotations.GraphQLContext
import com.expedia.graphql.annotations.GraphQLName
import com.reidswan.reidit.config.Configuration
import com.reidswan.reidit.controllers.*
import com.reidswan.reidit.data.queries.*
import com.reidswan.reidit.common.*
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authentication
import io.ktor.http.HttpStatusCode

val DEFAULT_PAGE_SIZE: Int = 50;

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

class CommunityQueries {
    val communitiesController = CommunitiesController(
        CommunitiesQueries(Configuration.dependencies.database),
        PostsQueries(Configuration.dependencies.database))

    fun communityExists(communityName: String): Boolean = communitiesController.communityExists(communityName)
    
    fun getCommunities(pageParameters: PageParameters?): List<CommunityResult> {
        return communitiesController.getCommunities(pageParameters ?: PageParameters(1, DEFAULT_PAGE_SIZE))
    }

    fun getPostsInCommunity(communityName: String, pageParameters: PageParameters?): List<PostResult> {
        val posts = communitiesController.getPostsInCommunity(communityName, pageParameters ?: PageParameters(1, DEFAULT_PAGE_SIZE))
        if (posts == null) throw HttpException("Could not find community $communityName", HttpStatusCode.NotFound)
        return posts
    }
}

class CommunityMutations {
    val communitiesController = CommunitiesController(
        CommunitiesQueries(Configuration.dependencies.database),
        PostsQueries(Configuration.dependencies.database))

    fun createCommunity(@GraphQLContext context: ApplicationCall, communityName: String, communityDescription: String?): CommunityResult? {
        val loggedInUser = context.authentication.principal<UserPrincipal>() ?:
            throw HttpException("You must be logged in to create a community", HttpStatusCode.Unauthorized)
        return communitiesController.createCommunity(communityName, communityDescription, loggedInUser)
    }

    fun createPost(@GraphQLContext context: ApplicationCall, communityName: String, title: String, postType: ContentType, content: String): PostResult? {
        val loggedInUser = context.authentication.principal<UserPrincipal>() ?:
            throw HttpException("You must be logged in to create a post", HttpStatusCode.Unauthorized)
        return communitiesController.createPost(communityName, title, postType, content, loggedInUser)
    }
}

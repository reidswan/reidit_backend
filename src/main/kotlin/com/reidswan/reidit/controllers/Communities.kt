package com.reidswan.reidit.controllers

import com.reidswan.reidit.common.*
import com.reidswan.reidit.data.queries.CommunitiesQueries
import com.reidswan.reidit.data.queries.PostsQueries
import com.reidswan.reidit.config.Configuration
import com.reidswan.reidit.data.queries.Wrap
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.exceptions.ExposedSQLException
import java.sql.SQLIntegrityConstraintViolationException

const val MAX_TITLE_LENGTH: Int = 100
const val MIN_TITLE_LENGTH: Int = 4

class CommunitiesController(private val communityQueries: CommunitiesQueries, private val postsQueries: PostsQueries) {
    fun getCommunities(pageParameters: PageParameters): List<CommunityResult> {
        return communityQueries.getCommunities(pageParameters).map {
            it.copy(posts=getPostsInCommunity(it.name, SAMPLE_PAGE_PARAMETERS) ?: listOf())
        }
    }

    fun communityExists(communityName: String): Boolean = communityQueries.getCommunityByName(communityName.toLowerCase()) != null

    fun getCommunity(communityName: String, pageParameters: PageParameters=DEFAULT_PAGE_PARAMETERS): CommunityResult? {
        return communityQueries.getCommunityByName(communityName)
            ?.copy(posts=this.getPostsInCommunity(communityName, pageParameters) ?: listOf())
    }

    fun getPostsInCommunity(communityName: String, pageParameters: PageParameters): List<PostResult>? {
        return if (this.communityExists(communityName)) {
            postsQueries.getPostsByCommunityName(communityName, pageParameters)
        } else null
    }

    fun createCommunity(communityName: String, communityDescription: String?, createdBy: UserPrincipal): CommunityResult? {
        val description = communityDescription ?: ""
        val name = communityName.toLowerCase()
        if (this.communityExists(name)) throw HttpException("Community '$communityName' already exists", HttpStatusCode.Conflict)
        return try {
            communityQueries.createCommunity(name, description, createdBy.accountId)
        } catch (e: ExposedSQLException) {
            when (e.cause) {
                is SQLIntegrityConstraintViolationException -> {
                    throw HttpException("Community name must be unique", HttpStatusCode.Conflict)
                }
                else -> throw e
            }
        }
    }

    fun createPost(communityName: String, title: String, postType: ContentType, content: String, createdBy: UserPrincipal): PostResult? {
        val community = this.getCommunity(communityName)
        if (community == null) throw HttpException("Community $communityName does not exist", HttpStatusCode.BadRequest)
        if (!this.communityExists(communityName)) throw HttpException("Community $communityName does not exist", HttpStatusCode.BadRequest)
        if (title.length > MAX_TITLE_LENGTH) throw HttpException("Title violates maximum $MAX_TITLE_LENGTH character limit", HttpStatusCode.BadRequest)
        if (title.length < MIN_TITLE_LENGTH) throw HttpException("Title violates minimum $MIN_TITLE_LENGTH character limit", HttpStatusCode.BadRequest)
        val created = postsQueries.createPost(community.communityId, title, postType, content, createdBy.accountId, 1)
        // TODO: create a vote
        return created
    }

}
package com.reidswan.reidit.controllers

import com.reidswan.reidit.common.*
import com.reidswan.reidit.data.queries.CommunitiesQueries
import com.reidswan.reidit.data.queries.PostsQueries
import com.reidswan.reidit.config.Configuration
import com.reidswan.reidit.data.queries.Wrap
import io.ktor.http.HttpStatusCode

class CommunitiesController(private val communityQueries: CommunitiesQueries, private val postsQueries: PostsQueries) {
    fun getCommunities(pageParameters: PageParameters): Wrap<List<CommunityResult>> {
        return communityQueries.getCommunities(pageParameters)
    }

    fun communityExists(communityName: String): Boolean = communityQueries.getCommunityByName(communityName) != null

    fun getPostsInCommunity(communityName: String, pageParameters: PageParameters): Wrap<List<PostResult>>? {
        return if (this.communityExists(communityName)) {
            postsQueries.getPostsByCommunityName(communityName, pageParameters)
        } else null
    }

    fun createCommunity(communityName: String, communityDescription: String?, createdBy: Int) {
        val description = communityDescription ?: ""
        if (this.communityExists(communityName)) throw HttpException("Community '$communityName' already exists", HttpStatusCode.Conflict)

    }

}
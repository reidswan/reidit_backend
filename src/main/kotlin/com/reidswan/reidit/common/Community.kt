package com.reidswan.reidit.common
import com.expedia.graphql.annotations.GraphQLName

data class CommunityResult(val communityId: Int, val name: String, val description: String, val createdBy: Int, val posts: List<PostResult>)

data class CreateCommunityRequest(val description: String?)
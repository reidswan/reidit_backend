package com.reidswan.reidit.data.queries

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import com.reidswan.reidit.common.*
import com.reidswan.reidit.data.Post
import com.reidswan.reidit.data.Community
import kotlin.math.min

fun ResultRow.toPostResult(): PostResult {
    return PostResult(
        this[Post.postId],
        this[Post.communityId],
        this[Post.title],
        this[Post.postType],
        this[Post.content],
        this[Post.votes],
        this[Post.accountId],
        this[Post.isDeleted]
    )
}

class PostsQueries(private val database: Database) {
    fun getPostsByCommunityName(communityName: String, pageParameters: PageParameters): Wrap<List<PostResult>> {
        val posts = transaction(database) {
            (Post innerJoin Community)
                .slice(
                    Post.postId,
                    Post.communityId,
                    Post.title,
                    Post.postType,
                    Post.content,
                    Post.votes,
                    Post.accountId,
                    Post.isDeleted
                )
                .select { (Post.communityId eq Community.communityId) and (Community.name eq communityName) }
                .limit(pageParameters.from, min(pageParameters.size, MAX_PAGE_SIZE))
                .map { it.toPostResult() }
        }
        return mapOf("posts" to posts)
    }
}
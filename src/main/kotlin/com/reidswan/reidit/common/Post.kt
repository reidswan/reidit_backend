package com.reidswan.reidit.common

enum class ContentType { text, link }

data class PostResult(
    val postId: Int,
    val communityId: Int,
    val title: String,
    val postType: ContentType,
    val content: String,
    val votes: Int,
    val accountId: Int,
    val isDeleted: Boolean
)

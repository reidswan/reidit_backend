package com.reidswan.reidit.data

import org.jetbrains.exposed.sql.*
import org.postgresql.util.PGobject

object Account: Table(name="account") {
    val accountId: Column<Int> = integer("account_id").autoIncrement().primaryKey()
    val username: Column<String> = varchar("username", 50).uniqueIndex()
    val emailAddress: Column<String?> = varchar("email_address", 255).uniqueIndex().nullable()
    val verified: Column<Boolean> = bool("verified").default(false)
    val passwordHash: Column<String> = varchar("password_hash", 128)
    val passwordSalt: Column<String> = varchar("password_salt", 16)
}

object Community: Table(name="community") {
    val communityId: Column<Int> = integer("community_id").autoIncrement().primaryKey()
    val name: Column<String> = varchar("name", 50).uniqueIndex()
    val description: Column<String> = varchar("description", 300)
}

class PGEnum<T:Enum<T>> (enumTypeName: String, enumValue: T?): PGobject() {
    init {
        value = enumValue?.name
        type = enumTypeName
    }
}

enum class ContentType { text, link }

object Post: Table(name="post") {
    val postId: Column<Int> = integer("post_id").autoIncrement().primaryKey()
    val communityId = reference("community_id", Community.communityId)
    val title = varchar("title", 100)
    val postType = customEnumeration(
        "post_type", "CONTENT_TYPE",
        {value -> ContentType.valueOf(value as String)},
        { PGEnum("CONTENT_TYPE", it) })
    val content = text("content")
    val votes = integer("votes").default(0)
    val accountId = reference("account_id", Account.accountId)
    val isDeleted = bool("is_deleted").default(false)
}

object Vote: Table(name="vote") {
    val accountId = reference("account_id", Account.accountId)
    val postId = reference("post_id", Post.postId)
    val voteValue = integer("vote_value")
}

object Subsctiption: Table(name="subscription") {
    val accountId = reference("account_id", Account.accountId)
    val communityId = reference("communityId", Community.communityId)
}

object Comment: Table(name="comment") {
    val commentId = integer("comment_id").autoIncrement().primaryKey()
    val postId = reference("post_id", Post.postId)
    val accountId = reference("account_id", Account.accountId)
    val content = text("content")
    val votes = integer("votes").default(0)
    val isDeleted = bool("is_deleted").default(false)
    val parentComment = reference("parent_comment", commentId).nullable()
}

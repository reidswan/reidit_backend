package com.reidswan.reidit.data.queries

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val MAX_PAGE_SIZE: Int = 100
val logger = LoggerFactory.getLogger("QueryLogger")
typealias Wrap<V> = Map<String, V>

class QuerySource(private val database: Database) {
    val communitiesQueries: CommunitiesQueries = CommunitiesQueries(database)
    val postQueries: PostsQueries = PostsQueries(database)
    val accountQueries: AccountsQueries = AccountsQueries(database)
}


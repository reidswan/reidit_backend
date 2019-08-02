/**
 * Functions querying the database
 */

package com.reidswan.reidit.data.queries

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import com.reidswan.reidit.common.*
import com.reidswan.reidit.data.Community
import kotlin.math.min

fun ResultRow.toCommunityResult(): CommunityResult {
    return CommunityResult(
        this[Community.communityId],
        this[Community.name],
        this[Community.description],
        this[Community.createdBy]
    )
}

class CommunitiesQueries(private val database: Database) {
    fun getCommunities(pageParameters: PageParameters): Wrap<List<CommunityResult>> {
        return transaction(database) {
            val communities = Community.selectAll().limit(
                min(pageParameters.size,
                    MAX_PAGE_SIZE
                ), offset=pageParameters.from
            ).map { it.toCommunityResult() }
            mapOf("communities" to communities)
        }
    }

    fun getCommunityByName(communityName: String): CommunityResult? {
        return transaction(database) {
            Community.select { Community.name eq communityName }.firstOrNull()?.toCommunityResult()
        }
    }

}



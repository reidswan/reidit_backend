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
        this[Community.description]
    )
}

object CommunitiesQueries {
    fun getCommunities(db: Database, pageParameters: PageParameters): Wrap<List<CommunityResult>> {
        return transaction(db) {
            val communities = Community.selectAll().limit(
                pageParameters.from, min(
                    pageParameters.size,
                    MAX_PAGE_SIZE
                )
            ).map { it.toCommunityResult() }
            mapOf("communities" to communities)
        }
    }

    fun getCommunityByName(db: Database, communityName: String): CommunityResult? {
        return transaction(db) {
            Community.select { Community.name eq communityName }.firstOrNull()?.toCommunityResult()
        }
    }

}



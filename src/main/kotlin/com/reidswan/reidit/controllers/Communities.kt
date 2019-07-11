package com.reidswan.reidit.controllers

import com.reidswan.reidit.common.*
import com.reidswan.reidit.data.queries.CommunitiesQueries
import com.reidswan.reidit.config.Configuration
import com.reidswan.reidit.data.queries.CommunityResult
import com.reidswan.reidit.data.queries.Wrap

object CommunitiesController {
    fun getCommunities(pageParameters: PageParameters): Wrap<List<CommunityResult>> {
        return CommunitiesQueries.getCommunities(Configuration.dependencies.database, pageParameters)
    }
}
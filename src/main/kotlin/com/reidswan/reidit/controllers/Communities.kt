package com.reidswan.reidit.controllers

import com.reidswan.reidit.common.*
import com.reidswan.reidit.data.queries.CommunitiesQueries
import com.reidswan.reidit.config.Configuration
import com.reidswan.reidit.data.queries.Wrap

class CommunitiesController(private val querySource: CommunitiesQueries) {
    fun getCommunities(pageParameters: PageParameters): Wrap<List<CommunityResult>> {
        return querySource.getCommunities(pageParameters)
    }
}
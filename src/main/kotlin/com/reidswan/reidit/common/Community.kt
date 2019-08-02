package com.reidswan.reidit.common

data class CommunityResult(val communityId: Int, val name: String, val description: String, val createdBy: Int)

data class CreateCommunityRequest(val description: String?)
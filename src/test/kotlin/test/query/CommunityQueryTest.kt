package test.query

import com.reidswan.reidit.common.PageParameters
import com.reidswan.reidit.config.Configuration
import com.reidswan.reidit.data.queries.AccountsQueries
import com.reidswan.reidit.data.queries.CommunitiesQueries
import org.junit.jupiter.api.*


object CommunityQueryTest {
    @BeforeEach
    fun fillDatabase() {
        Fixtures.fillDatabase()
    }

    @AfterEach
    fun clearDatabase() {
        Fixtures.clearDatabase()
    }

    @Test
    fun `test getCommunityByName returns correct community`() {
        val queries = CommunitiesQueries(Configuration.dependencies.database)
        val result = queries.getCommunityByName("community_2")
        Assertions.assertNotNull(result)
        if (result == null) return
        Assertions.assertEquals("community_2", result.name)
    }

    @Test
    fun `test getCommunities respects page parameters`() {
        val pageParams = PageParameters(1, 5)
        val queries = CommunitiesQueries(Configuration.dependencies.database)
        val result = queries.getCommunities(pageParams)
        val communities = result["communities"]!!
        Assertions.assertEquals(communities.size, pageParams.size)
    }
}

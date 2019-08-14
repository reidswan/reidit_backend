package test.controller

import com.reidswan.reidit.common.*
import com.reidswan.reidit.controllers.*
import com.reidswan.reidit.data.queries.CommunitiesQueries
import com.reidswan.reidit.data.queries.PostsQueries
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CommunityControllerTest {
    @Test
    fun `test something`() {
        Assertions.assertTrue(true);
    }

    @Test
    fun `test communityExists returns true when community exists`() {
        val mockCommunityQueries = mockk<CommunitiesQueries>()
        val mockPostsQueries = mockk<PostsQueries>()
        every { mockCommunityQueries.getCommunityByName(any()) } returns CommunityResult(
            1, "something", "something", 1, listOf()
        );

        val controller = CommunitiesController(mockCommunityQueries, mockPostsQueries)
        Assertions.assertTrue(controller.communityExists("something"))
    }

    @Test
    fun `test communityExists returns false when community does not exist`() {
        val mockCommunityQueries = mockk<CommunitiesQueries>()
        val mockPostsQueries = mockk<PostsQueries>()
        every { mockCommunityQueries.getCommunityByName(any()) } returns null;
        val pageParams = PageParameters(1, 50)
        val controller = CommunitiesController(mockCommunityQueries, mockPostsQueries)
        Assertions.assertFalse(controller.communityExists("something"))
    }

    @Test
    fun `test getPostsInCommunity returns null when community does not exist`() {
        val mockCommunityQueries = mockk<CommunitiesQueries>()
        val mockPostsQueries = mockk<PostsQueries>()
        every { mockCommunityQueries.getCommunityByName(any()) } returns null
        val controller = CommunitiesController(mockCommunityQueries, mockPostsQueries)
        val pageParams = PageParameters(1, 50)
        Assertions.assertNull(controller.getPostsInCommunity("something", pageParams))
    }

    @Test
    fun `test getPostsInCommunity does not return null if the community has no posts`() {
        val mockCommunityQueries = mockk<CommunitiesQueries>()
        val mockPostsQueries = mockk<PostsQueries>()
        every { mockCommunityQueries.getCommunityByName(any()) } returns CommunityResult(
            1, "something", "something", 1, listOf()
        )
        every { mockPostsQueries.getPostsByCommunityName(any(), any()) } returns listOf();
        val pageParams = PageParameters(1, 50);
        val controller = CommunitiesController(mockCommunityQueries, mockPostsQueries)
        Assertions.assertNotNull(controller.getPostsInCommunity("something", pageParams))
        
    }

}

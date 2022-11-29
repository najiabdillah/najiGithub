package com.example.najigithub.data.remote.api

import com.example.najigithub.data.remote.dto.FollowResponse
import com.example.najigithub.data.remote.dto.SearchResponse
import com.example.najigithub.data.remote.dto.UserDetailResponse
import com.example.najigithub.domain.utils.Extensions
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {
    @GET(Extensions.SEARCH_EP)
    suspend fun getSearchUser(
        @Query("q") username: String
    ): SearchResponse

    @GET(Extensions.DETAIL_EP)
    suspend fun getUsersDetail(
        @Path("username") username: String
    ) : UserDetailResponse

    @GET(Extensions.FOLLOWER_EP)
    suspend fun getUsersFollower(
        @Path("username") username: String
    ) : List<FollowResponse>

    @GET(Extensions.FOLLOWING_EP)
    suspend fun getUserFollowing(
        @Path("username") username: String
    ) : List<FollowResponse>
}
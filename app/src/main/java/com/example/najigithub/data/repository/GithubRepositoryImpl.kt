package com.example.najigithub.data.repository

import com.example.najigithub.data.remote.api.GithubApi
import com.example.najigithub.data.remote.dto.FollowResponse
import com.example.najigithub.data.remote.dto.SearchResponse
import com.example.najigithub.data.remote.dto.UserDetailResponse
import com.example.najigithub.domain.repository.GithubRepository
import com.example.najigithub.domain.utils.Result
import com.example.najigithub.domain.utils.unwrapAsFlow
import kotlinx.coroutines.flow.Flow

class GithubRepositoryImpl(
    private val service: GithubApi
): GithubRepository {

    override fun searchUser(username: String): Flow<Result<SearchResponse>> = unwrapAsFlow {
        service.getSearchUser(username)
    }

    override fun userDetail(username: String): Flow<Result<UserDetailResponse>> = unwrapAsFlow {
        service.getUsersDetail(username)
    }

    override fun userFollower(username: String): Flow<Result<List<FollowResponse>>> = unwrapAsFlow {
        service.getUsersFollower(username)
    }

    override fun userFollowing(username: String): Flow<Result<List<FollowResponse>>> = unwrapAsFlow  {
        service.getUserFollowing(username)
    }

}
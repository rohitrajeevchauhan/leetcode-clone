package com.rohit.leetcode.platform.service;

public interface FollowService {

    void follow(String username);

    void unfollow(String username);

    boolean isFollowing(String username);

    long getFollowersCount(String username);

    long getFollowingCount(String username);
}
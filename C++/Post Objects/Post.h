#pragma once

#include <iostream>

using namespace std;

class Post
{
private:
    string postContent;
    int likeCount;
    string username;
    string postDate;

public:
    Post(string, int, string, string); // postContent, likeCount, username, postDate
    string getPostContent();
    int getLikeCount();
    string getUsername();
    string getPostDate();
};
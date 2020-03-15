#pragma once

#include <iostream>

#include "Post.h"

using namespace std;

class RedditPost : public Post{
private:
    string p_subreddit;
    string p_link;
    int p_commentcount;
    // list for comments?

public:
    RedditPost(string, int, string, string, string, string, int);    // postContent, likeCount, username, postDate, subreddit, link, commentcount
    string getPostSubreddit();
    int getPostCommentCount();
    string getPostLink();
};
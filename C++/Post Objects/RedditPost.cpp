#pragma once

#ifndef RPOST_H
#define RPOST_H

#include "RedditPost.h"

RedditPost::RedditPost(string title, int likecount, string uname, string postdate, string postsub, string postlink, int postcommentcount) : Post(title, likecount, uname, postdate){
    p_subreddit = postsub;
    p_link = postlink;
    p_commentcount = postcommentcount;
}

string RedditPost::getPostSubreddit()
{
    return p_subreddit;
}

int RedditPost::getPostCommentCount()
{
    return p_commentcount;
}

string RedditPost::getPostLink()
{
    return p_link;
}

#endif
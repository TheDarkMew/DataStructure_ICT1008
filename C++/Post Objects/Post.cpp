#pragma once

#ifndef POST_H
#define POST_H

#include "Post.h"

Post::Post(string title, int likecount, string uname, string postdate)
{
    postContent = title;
    likeCount = likecount;
    username = uname;
    postDate = postdate;
}

string Post::getPostContent()
{
    return postContent;
}

int Post::getLikeCount()
{
    return likeCount;
}

string Post::getUsername()
{
    return username;
}

string Post::getPostDate()
{
    return postDate;
}

#endif
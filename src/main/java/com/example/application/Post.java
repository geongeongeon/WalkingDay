package com.example.application;

public class Post {

    private String posttitle; //게시글 제목
    private String postcontents; //게시글 내용
    private String postuser; //게시글 작성자
    private String posttime; //게시글 작성시간
    private int postCount; //게시글 번호
    private int postCommentCount; //게시글 댓글번호
    private String imageUrl; //프로필 사진 url

    public Post() {}

    public Post(String postuser, String posttitle, String postcontents) {
        this.postuser = postuser;
        this.posttitle = posttitle;
        this.postcontents = postcontents;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPostCommentCount() {
        return postCommentCount;
    }

    public void setPostCommentCount(int postCommentCount) {
        this.postCommentCount = postCommentCount;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public String getPosttitle() {
        return posttitle;
    }

    public void setPosttitle(String posttitle) {
        this.posttitle = posttitle;
    }

    public String getPostcontents() {
        return postcontents;
    }

    public void setPostcontents(String postcontents) {
        this.postcontents = postcontents;
    }

    public String getPostuser() {
        return postuser;
    }

    public void setPostuser(String postuser) {
        this.postuser = postuser;
    }

    public String getPosttime() { return posttime; }

    public void setPosttime(String posttime) { this.posttime = posttime; }

}

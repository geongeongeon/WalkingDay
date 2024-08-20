package com.example.application;

public class Comment {
    private String commentcontents; //댓글 내용
    private String commentuser; //댓글 작성자
    private String commenttime; //댓글 작성시간
    private int commentcount; //게시글 댓글번호

    public Comment() {}

    public Comment(String commentuser, String commentcontents) {
        this.commentuser = commentuser;
        this.commentcontents = commentcontents;
    }

    public int getCommentcount() {
        return commentcount;
    }

    public void setCommentcount(int commentcount) {
        this.commentcount = commentcount;
    }

    public String getCommentcontents() {
        return commentcontents;
    }

    public void setCommentcontents(String commentcontents) {
        this.commentcontents = commentcontents;
    }

    public String getCommentuser() {
        return commentuser;
    }

    public void setCommentuser(String commentuser) {
        this.commentuser = commentuser;
    }

    public String getCommenttime() {
        return commenttime;
    }

    public void setCommenttime(String commenttime) {
        this.commenttime = commenttime;
    }

}

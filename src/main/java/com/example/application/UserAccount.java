package com.example.application;

//사용자 계정 정보 모델 클래스
public class UserAccount
{
    private String idToken; //파이어베이스 uid (고유 토큰 정보)
    private String emailId; //이메일 아이디
    private String password; //비밀번호
    private String nickname; //닉네임
    private int level; //레벨
    private int point; //포인트
    private String imageUrl; //프로필 사진 url
    private int userCount; //유저 카운트
    private int rankCount; //랭킹 카운트

    public UserAccount() { }

    public UserAccount(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public UserAccount(String nickname, String emailId, String password, int level, int point)
    {
        this.nickname = nickname;
        this.emailId = emailId;
        this.password = password;
        this.level = level;
        this.point = point;
    }

    public int getUserCount() {return userCount;}

    public void setUserCount(int userCount) {this.userCount = userCount;}

    public int getRankCount() { return rankCount; }

    public void setRankCount(int rankCount) { this.rankCount = rankCount; }

    public String getImageUrl(){
        return imageUrl;
    }

    public void setImageUrl(String imageUrl){ this.imageUrl = imageUrl; }

    public int getPoint() { return point; }

    public void setPoint(int point) { this.point = point; }

    public int getLevel() { return level; }

    public void setLevel(int level) { this.level = level; }

    public String getIdToken() { return idToken; }

    public void setIdToken(String idToken) { this.idToken = idToken; }

    public String getEmailId() { return emailId; }

    public void setEmailId(String emailId) { this.emailId = emailId; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getNickname() { return nickname; }

    public void setNickname(String nickname) { this.nickname = nickname; }

}

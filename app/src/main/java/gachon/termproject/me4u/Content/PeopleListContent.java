package gachon.termproject.me4u.Content;

import java.util.ArrayList;

public class PeopleListContent {
    public String userId;
    public String nickname;
    public String profileImg;
    public String pushToken;
    public String intro;
    public ArrayList<String> location;

    public PeopleListContent(String userId, String nickname, String profileImg, String pushToken, String intro, ArrayList<String> location) {
        this.userId = userId;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.pushToken = pushToken;
        this.intro = intro;
        this.location = location;
    }

    public String getUserId() { return userId; }
    public String getNickname() { return nickname; }
    public String getProfileImg() { return profileImg; }
    public String getPushToken() { return pushToken; }
    public String getIntro() { return intro; }
    public ArrayList<String> getLocation() { return location; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setProfileImg(String profileImg) { this.profileImg = profileImg; }
    public void setLocation(ArrayList<String> location) { this.location = location; }
}

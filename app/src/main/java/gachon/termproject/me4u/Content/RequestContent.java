package gachon.termproject.me4u.Content;

import java.util.ArrayList;

public class RequestContent {
    String userId;
    String nickname;
    String profileImg;
    String pushToken;
    String intro;
    ArrayList<String> location;
    boolean isMatched;
    
    public RequestContent() {}
    
    public RequestContent(String nickname, String profileImg, String pushToken, String intro, ArrayList<String> location, boolean isMatched) {
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.pushToken = pushToken;
        this.intro = intro;
        this.location = location;
        this.isMatched = isMatched;
    }

    public String getUserId() { return userId; }
    public String getNickname() {
        return nickname;
    }
    public String getProfileImg() {
        return profileImg;
    }
    public String getPushToken() { return pushToken; }
    public String getIntro() { return intro; }
    public ArrayList<String> getLocation() {
        return location;
    }
    public boolean getIsMatched() {
        return isMatched;
    }
    public void setUserId(String userId) { this.userId = userId; }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public void setProfileImg(String profileImg) { this.profileImg = profileImg; }
    public void setLocation(ArrayList<String> location) { this.location = location; }
    public void setIsMatched(boolean isMatched) {
        this.isMatched = isMatched;
    }
}

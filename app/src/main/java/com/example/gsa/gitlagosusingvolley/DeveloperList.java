package com.example.gsa.gitlagosusingvolley;

/**
 * Created by GSA on 10/10/2017.
 * This holds the details of each developer
 * {@param login } the  username
 * {@param html_url} the link to the github profile
 * {@param avatar_url} the link to the profile image
 */
public class DeveloperList {
    private String login;
    private String html_url;
    private String avatar_url;

    public DeveloperList(String login,String html_url,String avatar_url){
        this.login = login;
        this.html_url = html_url;
        this.avatar_url = avatar_url;
    }

    public String getLogin(){
        return login;
    }

    public String getHtmlUrl(){
        return html_url;
    }

    public String getAvatarUrl(){
        return avatar_url;
    }

}

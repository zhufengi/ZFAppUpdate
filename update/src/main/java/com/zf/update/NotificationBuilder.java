package com.zf.update;

public class NotificationBuilder {

    public int icon;
    public String contentTitle;
    public String ticker;
    public String contentText;
    public boolean isRingtone;

    public static NotificationBuilder create(){

        return new NotificationBuilder();
    }

    private NotificationBuilder(){
        isRingtone = false;
    }
}

package ru.shift.chat.DTO;


public class MessageDTO {

    private int chatId;

    private int userId;

    private String text;

    private String sendTime;

    private Integer lifetimeSec;

    private int delaySec;

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getLifetimeSec() {
        return lifetimeSec;
    }

    public void setLifetimeSec(int lifetimeSec) {
        this.lifetimeSec = lifetimeSec;
    }

    public int getDelaySec() {
        return delaySec;
    }

    public void setDelaySec(int delaySec) {
        this.delaySec = delaySec;
    }
}

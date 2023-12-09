package ro.ubbcluj.map.socialnetwork.domain;

import java.time.LocalDateTime;

public class Message extends Entity<Long> {

    private String text;
    private Utilizator sender;
    private Utilizator receiver;
    private Long idReply;
    private LocalDateTime date;

    public Message(Utilizator sender, Utilizator receiver, String text) {
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
        this.date = LocalDateTime.now();
    }

    public long getIdReply() {
        if(idReply!=null)
            return idReply;
        else
            return 0;
    }

    public void setIdReply(Long idReply) {
        this.idReply = idReply;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Utilizator getSender() {
        return sender;
    }

    public void setSender(Utilizator sender) {
        this.sender = sender;
    }

    public Utilizator getReceiver() {
        return receiver;
    }

    public void setReceiver(Utilizator receiver) {
        this.receiver = receiver;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
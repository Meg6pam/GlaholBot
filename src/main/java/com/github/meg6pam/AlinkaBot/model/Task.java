package com.github.meg6pam.AlinkaBot.model;

public class Task {
    private Long id;
    private String recipient;
    private String type;
    private String message;
    private String fileUri;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", recipient='" + recipient + '\'' +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", fileUri='" + fileUri + '\'' +
                '}';
    }
}

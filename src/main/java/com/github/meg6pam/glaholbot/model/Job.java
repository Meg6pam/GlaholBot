package com.github.meg6pam.alinkabot.model;

public class Job {
    Integer jobId;
    String message;
    Integer fileId;
    Integer chatId;

    public Integer getFileId() { return fileId; }

    public void setFileId(Integer fileId) { this.fileId = fileId; }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof Job)) {
            return false;
        }
        return this.getJobId().equals(((Job) o).getJobId());
    }

}

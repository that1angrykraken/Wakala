package com.kraken.wakala.dtos;

public class Group {
    String id;
    String createdDate, leader;

    public Group(String id, String createdDate, String leader) {
        this.id = id;
        this.createdDate = createdDate;
        this.leader = leader;
    }

    public Group() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }
}

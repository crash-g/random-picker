package com.muciaccia.bot.pojo;

import java.util.Date;

public class PersonInfo {
    private final Date excludedDate;

    public PersonInfo(Date excludedDate) {
        this.excludedDate = excludedDate;
    }

    public Date getExcludedDate() {
        return excludedDate;
    }

}

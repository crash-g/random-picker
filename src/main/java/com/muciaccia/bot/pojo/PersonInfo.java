package com.muciaccia.bot.pojo;

import java.util.Date;

/**
 * A class containing additional info about a Person. It is used to save the date when a Person is marked as absent.
 */
public class PersonInfo {
    private final Date excludedDate;

    public PersonInfo(Date excludedDate) {
        this.excludedDate = excludedDate;
    }

    public Date getExcludedDate() {
        return excludedDate;
    }

}

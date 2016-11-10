package com.muciaccia.bot.service;

import com.muciaccia.bot.pojo.Person;

import java.util.List;
import java.util.Random;

/**
 * This class implements the main routine of the application.
 */
public class RandomPicker {

    /**
     * Extracts a random participant from a list.
     *
     * @param participants a list of participants (without absentees).
     * @return a random choice from the list.
     */
    public static Person pickOne(final List<Person> participants) {
        Random rand = new Random(System.currentTimeMillis());
        return participants.get(rand.nextInt(participants.size()));
    }
}

package com.muciaccia.bot.service;

import com.muciaccia.bot.pojo.Person;

import java.util.List;
import java.util.Random;

public class RandomPicker {
    public static Person pickOne(final List<Person> participants) {
        Random rand = new Random(System.currentTimeMillis());
        return participants.get(rand.nextInt(participants.size()));
    }
}

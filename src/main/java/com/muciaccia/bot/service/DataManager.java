package com.muciaccia.bot.service;

import com.muciaccia.bot.pojo.Person;
import com.muciaccia.bot.pojo.PersonInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DataManager {
    void addPerson(final String name);

    void removePerson(final String name);

    void addAbsentee(final String name);

    void addAbsentee(final String name, final Date date);

    void removeAbsentee(final String name);

    List<Person> getParticipantsList();

    Map<Person, PersonInfo> getAbsenteesMap();

    List<Person> getParticipantsWithNoAbsentee();
}

package com.muciaccia.bot.service;

import com.muciaccia.bot.pojo.Person;
import com.muciaccia.bot.pojo.PersonInfo;

import java.util.*;
import java.util.stream.Collectors;

public class DataManagerImpl implements DataManager {
    private final Set<Person> participants;
    private final Map<Person, PersonInfo> absentees;

    public DataManagerImpl() {
        participants = new HashSet<>();
        absentees = new HashMap<>();
    }

    @Override
    public void addPerson(final String name) {
        participants.add(new Person(name));
    }

    @Override
    public void removePerson(final String name) {
        Person person = new Person(name);
        participants.remove(person);
        absentees.remove(person);
    }

    @Override
    public void addAbsentee(final String name) {
        addAbsentee(name, new Date());
    }

    @Override
    public void addAbsentee(final String name, final Date date) {
        Person person = new Person(name);
        if (participants.contains(person)) {
            absentees.put(person, new PersonInfo(date));
        }
    }

    @Override
    public void removeAbsentee(final String name) {
        absentees.remove(new Person(name));
    }

    @Override
    public List<Person> getParticipantsList() {
        return new ArrayList<>(participants);
    }

    @Override
    public Map<Person, PersonInfo> getAbsenteesMap() {
        return new HashMap<>(absentees);
    }

    @Override
    public List<Person> getParticipantsWithNoAbsentee() {
        return participants.stream().filter(p -> !absentees.containsKey(p)).collect(Collectors.toList());
    }
}

package com.muciaccia.bot.service;

import com.muciaccia.bot.pojo.Person;
import com.muciaccia.bot.pojo.PersonInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The interface for the back-end services.
 */
public interface DataManager {
    /**
     * Adds a name to the list of participants.
     *
     * @param name the name of the participant to add.
     */
    void addPerson(final String name);

    /**
     * Removes a name from the list of participants.
     *
     * @param name the name of the participant to remove.
     */
    void removePerson(final String name);

    /**
     * Adds a name to the list of absentees.
     *
     * @param name the name of the participant who is absent.
     */
    void addAbsentee(final String name);

    /**
     * Adds a name to the list of absentees.
     *
     * @param name the name of the participant who is absent.
     * @param date the date when the participant was absent.
     */
    void addAbsentee(final String name, final Date date);

    /**
     * Removes a name from the list of absentees.
     *
     * @param name the name to remove from the list.
     */
    void removeAbsentee(final String name);

    /**
     * Produces the complete list of participants.
     *
     * @return a complete list of participants.
     */
    List<Person> getParticipantsList();

    /**
     * Produces a map with all absentees together with the dates they were marked as absent.
     *
     * @return a map where the keys are absent persons and values are the dates they were absent.
     */
    Map<Person, PersonInfo> getAbsenteesMap();

    /**
     * Produces a list of participants, without including absent ones.
     *
     * @return a list of participants which are not marked as absent.
     */
    List<Person> getParticipantsWithNoAbsentee();
}

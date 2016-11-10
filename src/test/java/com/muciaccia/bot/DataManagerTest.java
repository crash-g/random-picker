package com.muciaccia.bot;

import com.muciaccia.bot.pojo.Person;
import com.muciaccia.bot.service.DataManagerImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DataManagerTest {
    @Test
    public void testGetParticipantsWithNoAbsentee() {
        DataManagerImpl dataManager = new DataManagerImpl();
        dataManager.addPerson("A B C");
        dataManager.addPerson("B");
        dataManager.addPerson("C");
        dataManager.addPerson("D D");
        dataManager.addPerson("E");
        dataManager.addPerson("F");

        dataManager.addAbsentee("B");
        dataManager.addAbsentee("E");
        dataManager.addAbsentee("D D");

        List<Person> resultList = dataManager.getParticipantsWithNoAbsentee();
        Assert.assertEquals(3, resultList.size());
        Assert.assertTrue(resultList.contains(new Person("A B C")));
        Assert.assertTrue(resultList.contains(new Person("C")));
        Assert.assertTrue(resultList.contains(new Person("F")));

        Assert.assertFalse(resultList.contains(new Person("B")));
        Assert.assertFalse(resultList.contains(new Person("E")));
        Assert.assertFalse(resultList.contains(new Person("D D")));
    }
}

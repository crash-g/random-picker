package com.muciaccia.bot;

import com.muciaccia.bot.exception.QuitException;
import com.muciaccia.bot.front.InputParser;
import com.muciaccia.bot.pojo.Person;
import com.muciaccia.bot.pojo.PersonInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;

public class InputParserTest {
    private static final String NEW_LINE = System.getProperty("line.separator");

    private static ByteArrayOutputStream stream;
    private static InputParser inputParser;
    private static Field dataManager;

    @Before
    public void resetPrintStream() throws NoSuchFieldException {
        stream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(stream);
        inputParser = new InputParser(printStream);
        dataManager = inputParser.getClass().getDeclaredField("dataManager");
        dataManager.setAccessible(true);
    }

    @Test
    public void testParseNameList() {
        try {
            Method method = InputParser.class.getDeclaredMethod("parseNameList", String.class);
            method.setAccessible(true);
            String nameList = "A, a, bb, fwfr, a, fef,a,ggr,t";
            List<String> parsedNameList = (List<String>) method.invoke(inputParser, nameList);
            Assert.assertEquals(9, parsedNameList.size());
            Assert.assertTrue(parsedNameList.contains("A"));
            Assert.assertTrue(parsedNameList.contains("a"));
            Assert.assertTrue(parsedNameList.contains("bb"));
            Assert.assertTrue(parsedNameList.contains("fwfr"));
            Assert.assertTrue(parsedNameList.contains("fef"));
            Assert.assertTrue(parsedNameList.contains("ggr"));
            Assert.assertTrue(parsedNameList.contains("t"));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testInserisci() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, QuitException {
        final String n1 = "a";
        final String n2 = "b";
        final String n3 = "c g";
        final String n4 = "dfr  1a";
        final String n5 = "FeÈ";
        final String n6 = "òćdd";

        Object dataManagerValue = dataManager.get(inputParser);
        Method getParticipantsList = dataManagerValue.getClass().getDeclaredMethod("getParticipantsList");

        inputParser.parseInput("i   , ");
        List<Person> list = (List<Person>) getParticipantsList.invoke(dataManagerValue);
        Assert.assertEquals(0, list.size());

        inputParser.parseInput("i " + n1 + "," + n2 + "," + n3 + "," + n2 + ",");
        list = (List<Person>) getParticipantsList.invoke(dataManagerValue);
        Assert.assertEquals(3, list.size());
        Set<Person> expected = new HashSet<>();
        expected.add(new Person(n1));
        expected.add(new Person(n2));
        expected.add(new Person(n3));
        Assert.assertThat(new HashSet<>(list), is(expected));

        inputParser.parseInput("insErisCi  " + n4 + ",    " + n5 + "  ,  " + n6);
        list = (List<Person>) getParticipantsList.invoke(dataManagerValue);
        Assert.assertEquals(6, list.size());
        expected.add(new Person(n4));
        expected.add(new Person(n5));
        expected.add(new Person(n6));
        Assert.assertThat(new HashSet<>(list), is(expected));
    }

    @Test
    public void testRimuovi() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, QuitException {
        final String n1 = "a";
        final String n2 = "dfr1a";
        final String n3 = "FeÈ";
        final String n4 = "aB1";

        Object dataManagerValue = dataManager.get(inputParser);
        Method getParticipantsList = dataManagerValue.getClass().getDeclaredMethod("getParticipantsList");

        inputParser.parseInput("i " + n1 + "," + n2 + "," + n3);
        inputParser.parseInput("rimuovi " + n1);
        List<Person> list = (List<Person>) getParticipantsList.invoke(dataManagerValue);
        Assert.assertEquals(2, list.size());
        Set<Person> expected = new HashSet<>();
        expected.add(new Person(n2));
        expected.add(new Person(n3));
        Assert.assertThat(new HashSet<>(list), is(expected));

        inputParser.parseInput("Inserisci " + n4);
        inputParser.parseInput("rimuovi " + n2 + " ,, " + n3 + ", " + n1);
        list = (List<Person>) getParticipantsList.invoke(dataManagerValue);
        Assert.assertEquals(1, list.size());
        expected = new HashSet<>();
        expected.add(new Person(n4));
        Assert.assertThat(new HashSet<>(list), is(expected));
    }

    @Test
    public void testAssenti() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, QuitException {
        final String n1 = "a";
        final String n2 = "dfr1a";
        final String n3 = "FeÈ";
        final String n4 = "aB1";
        final String n5 = "aBf1";

        Object dataManagerValue = dataManager.get(inputParser);
        Method getAbsenteesMap = dataManagerValue.getClass().getDeclaredMethod("getAbsenteesMap");

        inputParser.parseInput("Inserisci " + n1 + "," + n2 + "," + n3 + "," + n4);
        inputParser.parseInput("a " + n2 + ",,, " + n5);
        Map<Person, PersonInfo> map = (Map<Person, PersonInfo>) getAbsenteesMap.invoke(dataManagerValue);
        Assert.assertEquals(1, map.size());
        Set<Person> expected = new HashSet<>();
        expected.add(new Person(n2));
        Assert.assertThat(map.keySet(), is(expected));
        Assert.assertNotNull(map.get(new Person(n2)).getExcludedDate());

        inputParser.parseInput("assENTI  " + n2 + ", " + n3 + "," + n4);
        map = (Map<Person, PersonInfo>) getAbsenteesMap.invoke(dataManagerValue);
        Assert.assertEquals(3, map.size());
        expected.add(new Person(n3));
        expected.add(new Person(n4));
        Assert.assertThat(map.keySet(), is(expected));
    }

    @Test
    public void testPresenti() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, QuitException {
        final String n1 = "a";
        final String n2 = "dfr1a";
        final String n3 = "FeÈ";
        final String n4 = "aB1";
        final String n5 = "kkfi  dwd";

        Object dataManagerValue = dataManager.get(inputParser);
        Method getAbsenteesMap = dataManagerValue.getClass().getDeclaredMethod("getAbsenteesMap");

        inputParser.parseInput("Inserisci " + n1 + "," + n2 + "," + n3 + "," + n4 + "," + n5);
        inputParser.parseInput("a " + n2 + ", " + n4 + "," + n3);
        inputParser.parseInput("p  " + n2 + ", " + n4 + ",");
        Map<Person, PersonInfo> map = (Map<Person, PersonInfo>) getAbsenteesMap.invoke(dataManagerValue);
        Assert.assertEquals(1, map.size());
        Set<Person> expected = new HashSet<>();
        expected.add(new Person(n3));
        Assert.assertThat(map.keySet(), is(expected));

        inputParser.parseInput("p  ," + n3);
        map = (Map<Person, PersonInfo>) getAbsenteesMap.invoke(dataManagerValue);
        Assert.assertEquals(0, map.size());
    }

    @Test
    public void testEstrai() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, QuitException {
        final String n1 = "a";
        final String n2 = "dfr1a";
        final String n3 = "FeÈ";
        final String n4 = "aB1";
        final String n5 = "kkfi  dwd";

        Object dataManagerValue = dataManager.get(inputParser);
        Method getParticipantsWithNoAbsentee = dataManagerValue.getClass().getDeclaredMethod("getParticipantsWithNoAbsentee");

        inputParser.parseInput("Inserisci " + n1 + "," + n2 + "," + n3 + "," + n4 + "," + n5);
        inputParser.parseInput("a " + n2 + ", " + n4 + "," + n3);

        inputParser.parseInput("e");
        String result = new String(stream.toByteArray(), StandardCharsets.UTF_8);
        List<Person> list = (List<Person>) getParticipantsWithNoAbsentee.invoke(dataManagerValue);
        Assert.assertTrue(list.contains(new Person(result.substring(0, result.length() - NEW_LINE.length()))));
        stream.reset();

        inputParser.parseInput("ESTRai");
        result = new String(stream.toByteArray(), StandardCharsets.UTF_8);
        Assert.assertTrue(list.contains(new Person(result.substring(0, result.length() - NEW_LINE.length()))));
    }

    @Test
    public void testListe() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, QuitException {
        final String n1 = "a";
        final String n2 = "dfr1a";
        final String n3 = "FeÈ";
        final String n4 = "aB1";
        final String n5 = "kkfi  dwd";

        inputParser.parseInput("Inserisci " + n1 + "," + n2 + "," + n3 + "," + n4 + "," + n5);
        inputParser.parseInput("a " + n2 + ", " + n4 + "," + n3);

        inputParser.parseInput("l");
        String result = new String(stream.toByteArray(), StandardCharsets.UTF_8);
        int index = result.indexOf(NEW_LINE);
        int count = 0;
        while (index != -1) {
            ++count;
            index = result.indexOf(NEW_LINE, index + 1);
        }
        Assert.assertEquals(11, count);
        stream.reset();

        inputParser.parseInput("liSTE");
        result = new String(stream.toByteArray(), StandardCharsets.UTF_8);
        index = result.indexOf(NEW_LINE);
        count = 0;
        while (index != -1) {
            ++count;
            index = result.indexOf(NEW_LINE, index + 1);
        }
        Assert.assertEquals(11, count);
    }

    @Test
    public void testSalva() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, QuitException {
        final String n1 = "a";
        final String n2 = "dfr1a";
        final String n3 = "FeÈ";
        final String n4 = "aB1";
        final String n5 = "kkfi  dwd";

        inputParser.parseInput("Inserisci " + n1 + "," + n2 + "," + n3 + "," + n4 + "," + n5);
        inputParser.parseInput("a " + n2 + ", " + n4 + "," + n3);

        inputParser.parseInput("S test.txt");
        String result = new String(stream.toByteArray(), StandardCharsets.UTF_8);
        Assert.assertEquals("Liste esportate con successo." + NEW_LINE, result);
    }

    @Test
    public void testCarica() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, QuitException {
        final String n1 = "a";
        final String n2 = "dfr1a";
        final String n3 = "FeÈ";
        final String n4 = "aB1";
        final String n5 = "kkfi  dwd";

        inputParser.parseInput("CArica test.txt");
        String result = new String(stream.toByteArray(), StandardCharsets.UTF_8);
        Assert.assertEquals("Liste caricate con successo." + NEW_LINE, result);

        Object dataManagerValue = dataManager.get(inputParser);
        Method getParticipantsList = dataManagerValue.getClass().getDeclaredMethod("getParticipantsList");
        Method getAbsenteesMap = dataManagerValue.getClass().getDeclaredMethod("getAbsenteesMap");

        List<Person> list = (List<Person>) getParticipantsList.invoke(dataManagerValue);
        Assert.assertEquals(5, list.size());
        Set<Person> expected = new HashSet<>();
        expected.add(new Person(n1));
        expected.add(new Person(n2));
        expected.add(new Person(n3));
        expected.add(new Person(n4));
        expected.add(new Person(n5));
        Assert.assertThat(new HashSet<>(list), is(expected));
        Map<Person, PersonInfo> map = (Map<Person, PersonInfo>) getAbsenteesMap.invoke(dataManagerValue);
        expected = new HashSet<>();
        expected.add(new Person(n2));
        expected.add(new Person(n3));
        expected.add(new Person(n4));
        Assert.assertThat(map.keySet(), is(expected));
    }

    @Test
    public void testIstruzioni() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, QuitException {
        inputParser.parseInput("h");
        String result = new String(stream.toByteArray(), StandardCharsets.UTF_8);
        Assert.assertFalse(result.isEmpty());
        stream.reset();

        inputParser.parseInput("HElp");
        result = new String(stream.toByteArray(), StandardCharsets.UTF_8);
        Assert.assertFalse(result.isEmpty());
    }

    @Test(expected = QuitException.class)
    public void testEsciShort() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, QuitException {
        inputParser.parseInput("q");
    }

    @Test(expected = QuitException.class)
    public void testEsci() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, QuitException {
        inputParser.parseInput("Quit");
    }
}
package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void create() {
        Meal created = service.create(getNew(), USER_ID);
        Integer newId = created.getId();
        Meal newMeal = getNew();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, USER_ID), newMeal);
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        assertMatch(service.get(updated.getId(), USER_ID), getUpdated());
    }

    @Test
    public void get() {
        Meal meal = service.get(dinner.getId(), USER_ID);
        assertMatch(meal, dinner);
    }

    @Test
    public void delete() {
        service.delete(breakfast.getId(), USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(breakfast.getId(), USER_ID));
    }

    @Test
    public void getAll() {
        List<Meal> all = service.getAll(USER_ID);
        assertMatch(all, breakfast, dinner, lunch, supper);
    }

//    public List<Meal> getBetweenInclusive(@Nullable LocalDate startDate, @Nullable LocalDate endDate, int userId) {
//        return repository.getBetweenHalfOpen(atStartOfDayOrMin(startDate), atStartOfNextDayOrMax(endDate), userId);
//    }

    @Test
    public void duplicateDateCreate() {
        assertThrows(DataAccessException.class, () ->
                service.create(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000), USER_ID));
    }

    @Test
    public void duplicateDateUpdate() {
        assertThrows(DataAccessException.class, () ->
                service.update(getUpdatedDuplicate(), USER_ID));
    }

    @Test
    public void updateNotFound() {
        Meal updated = getUpdated();
        assertThrows(NotFoundException.class, () -> service.update(updated, NOT_FOUND));
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(supper.getId(), NOT_FOUND));
    }

    @Test
    public void deletedNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(dinner.getId(), NOT_FOUND));
    }
}
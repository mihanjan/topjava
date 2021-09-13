package ru.javawebinar.topjava.service.datajpa;

import org.junit.AfterClass;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.AbstractUserServiceTest;

import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;

@ActiveProfiles(Profiles.DATAJPA)
public class DataJpaUserServiceTest extends AbstractUserServiceTest {

    @Test
    public void getUserMeals() {
        User actual = service.getUserWithMeals(ADMIN_ID);
        MealTestData.MATCHER.assertMatch(actual.getMeals(), MealTestData.adminMeal2, MealTestData.adminMeal1);
    }
}

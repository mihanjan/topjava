package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;
import static org.assertj.core.api.Assertions.assertThat;

public class MealTestData {
    public static final int USER_ID = START_SEQ;
//    public static final int ADMIN_ID = START_SEQ + 1;
    public static final int NOT_FOUND = 10;

    public static final Meal breakfast = new Meal(START_SEQ + 2, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000);
    public static final Meal dinner = new Meal(START_SEQ + 3, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500);
    public static final Meal lunch = new Meal(START_SEQ + 4, LocalDateTime.of(2020, Month.JANUARY, 31, 17, 0), "Ланч", 100);
    public static final Meal supper = new Meal(START_SEQ + 5, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410);

    public static Meal getNew() {
        return new Meal(START_SEQ + 6, LocalDateTime.of(2020, Month.FEBRUARY, 1, 13, 0), "Новая еда", 1111);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(supper);
        updated.setDateTime(LocalDateTime.of(2020, Month.JANUARY, 31, 21, 0));
        updated.setDescription("Ужин с добавкой");
        updated.setCalories(400);
        return updated;
    }

    public static Meal getUpdatedDuplicate() {
        Meal updated = new Meal(lunch);
        updated.setDateTime(supper.getDateTime());
        updated.setDescription("Ланч к ужину");
        updated.setCalories(500);
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).isEqualTo(expected);
    }
}

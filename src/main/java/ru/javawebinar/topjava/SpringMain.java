package ru.javawebinar.topjava;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.web.SecurityUtil;
import ru.javawebinar.topjava.web.meal.MealRestController;
import ru.javawebinar.topjava.web.user.AdminRestController;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

public class SpringMain {
    public static void main(String[] args) {
        // java 7 automatic resource management (ARM)
        try (ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml")) {
            System.out.println("Bean definition names: " + Arrays.toString(appCtx.getBeanDefinitionNames()));
            AdminRestController adminUserController = appCtx.getBean(AdminRestController.class);
            adminUserController.create(new User(null, "userName", "email@mail.ru", "password", Role.ADMIN));

            MealRestController mealRestController = appCtx.getBean(MealRestController.class);
            mealRestController.getAll(SecurityUtil.authUserId(), SecurityUtil.authUserCaloriesPerDay()).forEach(System.out::println);
            System.out.println();
//            mealRestController.delete(6, SecurityUtil.authUserId());
//            mealRestController.getAll(SecurityUtil.authUserId(), SecurityUtil.authUserCaloriesPerDay()).forEach(System.out::println);
//            System.out.println();
//            mealRestController.create(new Meal(1, LocalDateTime.of(2020, Month.JANUARY, 31, 14, 0), "Обед", 1000), SecurityUtil.authUserId());
//            mealRestController.getAll(SecurityUtil.authUserId(), SecurityUtil.authUserCaloriesPerDay()).forEach(System.out::println);
//            System.out.println();
            mealRestController.update(new Meal(1, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин_1", 500), 3, SecurityUtil.authUserId());
            mealRestController.getAll(SecurityUtil.authUserId(), SecurityUtil.authUserCaloriesPerDay()).forEach(System.out::println);
            System.out.println();
        }
    }
}

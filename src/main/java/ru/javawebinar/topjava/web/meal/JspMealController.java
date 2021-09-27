package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
@RequestMapping(value = "/meals")
public class JspMealController extends AbstractMealController {

    MealService service;

    public JspMealController(MealService service) {
        super(service);
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("meals", getAll());
        return "meals";
    }

    @GetMapping("/filter")
    public String getBetween(HttpServletRequest request) {
        LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
        LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
        LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
        LocalTime endTime = parseLocalTime(request.getParameter("endTime"));

        request.setAttribute("meals", getBetween(startDate, startTime, endDate, endTime));
        return "meals";
    }

    @GetMapping("/delete")
    public String delete(HttpServletRequest request) {
        delete(getId(request));
        return "redirect:/meals";
    }


    @GetMapping("/create")
    public String create(HttpServletRequest request) {
        final Meal meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000);
        request.setAttribute("meal", meal);
        request.setAttribute("action", "create");
        return "mealForm";
    }

    @GetMapping("/update")
    public String update(HttpServletRequest request) {
        final Meal meal = get(getId(request));
        request.setAttribute("meal", meal);
        request.setAttribute("action", "update");
        return "mealForm";
    }

    @PostMapping
    public String saveMeal(HttpServletRequest request) {
        if (request.getParameter("id").isEmpty()) {
            create(getMeal(request));
        } else {
            update(getMeal(request), getId(request));
        }
        return "redirect:/meals";
    }

    private Meal getMeal(HttpServletRequest request) {
        return new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}

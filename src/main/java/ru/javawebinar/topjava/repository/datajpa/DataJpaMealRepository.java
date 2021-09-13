package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {

    private final CrudMealRepository crudMealRepository;
    private final CrudUserRepository crudUserRepository;

    public DataJpaMealRepository(CrudMealRepository crudMealRepository, CrudUserRepository crudUserRepository) {
        this.crudMealRepository = crudMealRepository;
        this.crudUserRepository = crudUserRepository;
    }

    @Transactional
    @Override
    public Meal save(Meal meal, int userId) {
        meal.setUser(crudUserRepository.getById(userId));
        if (!meal.isNew() && get(meal.id(), userId) == null) {
            return null;
        }
        return crudMealRepository.save(meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        return crudMealRepository.deleteByIdAndUserId(id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
    /*
        Сначала хотел релизовать через crudMealRepository.findByIdAndUserId(id, userId),
        но тогда бы к запросу добавился join к таблице users
     */

        return crudMealRepository
                .findById(id)
                .filter(meal -> meal.getUser().getId() == userId)
                .orElse(null);

//        Meal meal = crudMealRepository.findById(id).orElse(null);
//        return meal != null && meal.getUser().getId() == userId ? meal : null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudMealRepository.findAllByUserIdOrderByDateTimeDesc(userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return crudMealRepository.findAllBetweenHalfOpen(startDateTime, endDateTime, userId);
    }

    @Override
    public Meal getMealWithUser(int id, int userId) {
        Meal meal = get(id, userId);
        if (meal != null) {
            meal.setUser(crudUserRepository.findById(userId).orElse(null));
        }
        return meal;
    }
}

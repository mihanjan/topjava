package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private UserResultSetExtractor extractor = new UserResultSetExtractor();

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else if (namedParameterJdbcTemplate.update("""
                   UPDATE users SET name=:name, email=:email, password=:password, 
                   registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                """, parameterSource) == 0) {
            return null;
        }

//        jdbcTemplate.batchUpdate(
//                "update user_roles set user_id = ? where id = ?",
//                new BatchPreparedStatementSetter() {
//
//                    public void setValues(PreparedStatement ps, int i)
//                            throws SQLException {
//                        ps.setBigDecimal(1, user.get(i).getPrice());
//                        ps.setLong(2, user.get(i).getId());
//                    }
//
//                    public int getBatchSize() {
//                        return books.size();
//                    }
//
//                });


//        namedParameterJdbcTemplate.batchUpdate("",
//                user,
//                200,
//                new ParameterizedPreparedStatementSetter<User>() {
//                    public void setValues(PreparedStatement ps, User user) throws SQLException {
//                        ps.setBig
//                        ps.setBigDecimal(1, argument.getPrice());
//                        ps.setLong(2, argument.getId());
//                    }
//                }); )

        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT u.*, ur.role AS role FROM users u LEFT JOIN user_roles ur on u.id = ur.user_id WHERE id=?", extractor, id);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
        List<User> users = jdbcTemplate.query("SELECT u.*, ur.role AS role FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id WHERE email=?", extractor, email);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("SELECT u.*, ur.role AS role FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id ORDER BY name, email", extractor);
    }

    private static class UserResultSetExtractor implements ResultSetExtractor<List<User>> {
        @Override
        public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Integer, User> users = new ConcurrentHashMap<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                if (!users.containsKey(id)) {
                    User user = ROW_MAPPER.mapRow(rs, rs.getRow());
                    user.setRoles(null);
                    users.put(id, user);
                }

                String role = rs.getString("role");
                if (role != null) {
                    users.get(id).getRoles().add(Role.valueOf(role));
                }
            }
            return List.copyOf(users.values());
        }
    }
}

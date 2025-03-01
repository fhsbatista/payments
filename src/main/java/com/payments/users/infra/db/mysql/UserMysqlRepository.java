package com.payments.users.infra.db.mysql;

import com.payments.users.data.repositories.CreateUserRepository;
import com.payments.users.data.repositories.GetUserByEmailRepository;
import com.payments.users.domain.entities.User;
import com.payments.users.domain.usecases.CreateUserInput;
import com.payments.util.MysqlUtil;

import java.sql.*;
import java.util.Optional;

public class UserMysqlRepository implements
        CreateUserRepository,
        GetUserByEmailRepository {

    @Override
    public Optional<User> create(CreateUserInput input) {
        final String sql = "INSERT INTO USERS (name, cpf, email) VALUES (?, ?, ?)";

        try (Connection conn = MysqlUtil.conn();
             PreparedStatement statement = conn.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {
            statement.setString(1, input.name());
            statement.setString(2, input.cpf());
            statement.setString(3, input.email());

            int affectedRows = statement.executeUpdate();
            boolean isPersisted = affectedRows > 0;
            if (isPersisted) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return Optional.of(new User(
                                generatedKeys.getLong(1),
                                input.name(),
                                input.cpf(),
                                input.email()
                        ));
                    }
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> getByEmail(String email) {
        final String sql = "SELECT * FROM USERS WHERE email = ?";

        try (Connection conn = MysqlUtil.conn();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, email);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(new User(
                            result.getLong("id"),
                            result.getString("name"),
                            result.getString("cpf"),
                            result.getString("email")
                    ));
                }

                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return Optional.empty();
    }
}

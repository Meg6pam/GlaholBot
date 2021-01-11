package com.github.meg6pam.AlinkaBot.telegram.util;

import com.github.meg6pam.AlinkaBot.model.Recipient;
import com.github.meg6pam.AlinkaBot.model.Task;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class DatabaseManager {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    private static final String dblogin = System.getenv("DB_LOGIN");
    private static final String dbpassword = System.getenv("DB_PASSWORD");
    private static final PGSimpleDataSource ds = new PGSimpleDataSource();

    static {
        ds.setServerNames(new String[]{"ec2-54-247-92-167.eu-west-1.compute.amazonaws.com"});
        ds.setPortNumbers(new int[]{5432});
        ds.setDatabaseName("d31rocj9hve66e");
        ds.setUser(dblogin);
        ds.setPassword(dbpassword);
    }

    public static void addUser(Recipient recipient) {
        String username = String.format("'%s'", recipient.getUserName());
        String firstname = recipient.getFirstName() != null ? String.format("'%s'", recipient.getFirstName()) : "NULL";
        String lastname = recipient.getLastName() != null ? String.format("'%s'", recipient.getLastName()) : "NULL";
        Boolean isBot = recipient.getIsBot();
        String telephone = recipient.getTelephone() != null ? String.format("'%s'", recipient.getTelephone()) : "NULL";
        String email = recipient.getEmail() != null ? String.format("'%s'", recipient.getEmail()) : "NULL";
        String role = recipient.getRole() != null ? String.format("'%s'", recipient.getRole()) : "DEFAULT";
        String description = recipient.getDescription() != null ? String.format("'%s'", recipient.getDescription()) : "DEFAULT";

        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement();
        ) {
            String query = String.format("INSERT INTO users " +
                            "(username, firstname, lastname, is_bot, telephone, email, date, role, description)" +
                            " VALUES (%s, %s, %s, %b, %s, %s, DEFAULT, %s, %s)",
                    username, firstname, lastname, isBot, telephone, email, role, description);
            statement.executeUpdate(query);
            logger.trace("New User added to database {}", recipient);
        } catch (SQLException throwables) {
            logger.error("Can't add User to database {}", recipient);
            throwables.printStackTrace();
        }
    }

    public static void addUser(User user, Long chatId) {
        String username = String.format("'%s'", user.getUserName());
        String firstname = user.getFirstName() != null ? String.format("'%s'", user.getFirstName()) : "NULL";
        String lastname = user.getLastName() != null ? String.format("'%s'", user.getLastName()) : "NULL";
        Boolean isBot = user.getIsBot();

        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement();
        ) {
            String query = String.format("INSERT INTO users " +
                            "(id, username, firstname, lastname, is_bot, date, role, chat_id)" +
                            " VALUES (%d, %s, %s, %s, %b, DEFAULT, DEFAULT, %d)",
                    user.getId(), username, firstname, lastname, isBot, chatId);
            statement.executeUpdate(query);
            connection.commit();
            logger.trace("New User added to database {}", user);
        } catch (SQLException throwables) {
            logger.error("Can't add User to database {}", user);
            throwables.printStackTrace();
        }
    }

    public static String getUserRole(User user) {
        String role = "USER";
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement();
        ) {
            String query = String.format("SELECT role FROM users WHERE username = '%s'", user.getUserName());
            ResultSet resultSet = statement.executeQuery(query);
            role = resultSet.getString(0);
        } catch (SQLException throwables) {
            logger.error("Can't get Role from database for user {}", user.getUserName());
            throwables.printStackTrace();
        }
        return role;
    }

    public static String getUserRole(String userName) {
        String role = "USER";
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement();
        ) {
            String query = String.format("SELECT role FROM users WHERE username = '%s'", userName);
            ResultSet resultSet = statement.executeQuery(query);
            role = resultSet.getString(0);
        } catch (SQLException throwables) {
            logger.error("Can't get Role from database for user {}", userName);
            throwables.printStackTrace();
        }
        return role;
    }

    public static Integer getUncompletedTasks(User user){
        Integer taskId = null;
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement();
        ) {
            String query = String.format("SELECT id FROM tasks WHERE status = 'DRAFT' AND author = %d", user.getId());
            ResultSet resultSet = statement.executeQuery(query);
            taskId = resultSet.getInt(0);
        } catch (SQLException throwables) {
            logger.error("Can't get Role from database for user {}", user.getUserName());
            throwables.printStackTrace();
        }
        return taskId;
    }

    public static Optional<Integer> getLastTask(){
        Integer taskId = null;
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement();
        ) {
            String query = "SELECT id FROM tasks WHERE status = 'DRAFT' ORDER BY id DESC LIMIT 1";
            ResultSet resultSet = statement.executeQuery(query);
            taskId = resultSet.getInt(0);
        } catch (SQLException throwables) {
            logger.error("Can't get last Task from database");
            throwables.printStackTrace();
        }
        return Optional.ofNullable(taskId);
    }

    public static void newTask(String type, Integer authorUserId){
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement();
        ) {
            String query = String.format("INSERT INTO tasks VALUES (type, status, author, creation_date) " +
                    "'%s', 'DRAFT', '%d', DEFAULT", type, authorUserId);
            statement.executeUpdate(query);
            connection.commit();
        } catch (SQLException throwables) {
            logger.error("Can't add new Task with type {} for user with id {} to database ", type, authorUserId);
            throwables.printStackTrace();
        }
    }

    public static void setTaskFileId(Integer taskId, String fileId){
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement();
        ) {
            String query = String.format("UPDATE tasks SET file_id = %s WHERE id = %d ", fileId, taskId);
            statement.executeUpdate(query);
            connection.commit();
        } catch (SQLException throwables) {
            logger.error("Can't set new file_id {} for task with id {} to database ", fileId, taskId);
            throwables.printStackTrace();
        }
    }

    public static void setTaskMessage(Integer taskId, String message){
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement();
        ) {
            String query = String.format("UPDATE tasks SET message = %s WHERE id = %s ", message, taskId);
            statement.executeUpdate(query);
            connection.commit();
        } catch (SQLException throwables) {
            logger.error("Can't set new message {} for task with id {} to database ", message, taskId);
            throwables.printStackTrace();
        }
    }
}

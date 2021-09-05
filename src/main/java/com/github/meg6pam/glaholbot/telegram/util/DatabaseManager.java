package com.github.meg6pam.alinkabot.telegram.util;

import com.github.meg6pam.alinkabot.enums.Status;
import com.github.meg6pam.alinkabot.model.Job;
import com.github.meg6pam.alinkabot.model.MessageTuple;
import com.github.meg6pam.alinkabot.model.Recipient;
import com.github.meg6pam.alinkabot.model.Task;
import org.postgresql.ds.PGSimpleDataSource;
import org.telegram.telegrambots.meta.api.objects.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseManager {

//    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    private static final String dblogin = System.getenv("DB_LOGIN");
    private static final String dbpassword = System.getenv("DB_PASSWORD");
    private static final PGSimpleDataSource ds = new PGSimpleDataSource();

    static {
//        ds.setServerNames(new String[]{"ec2-54-247-92-167.eu-west-1.compute.amazonaws.com"});
        ds.setServerNames(new String[]{"localhost"});
        ds.setPortNumbers(new int[]{5432});
//        ds.setDatabaseName("d31rocj9hve66e");
        ds.setDatabaseName("postgres");
        ds.setUser(dblogin);
        ds.setPassword(dbpassword);
    }

    public static void addUser(Recipient recipient) {
        final String username = String.format("'%s'", recipient.getUserName());
        final String firstname = recipient.getFirstName() != null ? String.format("'%s'", recipient.getFirstName()) : "NULL";
        final String lastname = recipient.getLastName() != null ? String.format("'%s'", recipient.getLastName()) : "NULL";
        final Boolean isBot = recipient.getIsBot();
        final String telephone = recipient.getTelephone() != null ? String.format("'%s'", recipient.getTelephone()) : "NULL";
        final String email = recipient.getEmail() != null ? String.format("'%s'", recipient.getEmail()) : "NULL";
        final String role = recipient.getRole() != null ? String.format("'%s'", recipient.getRole()) : "DEFAULT";
        final String description = recipient.getDescription() != null ? String.format("'%s'", recipient.getDescription()) : "DEFAULT";

        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement()
        ) {
            final String query = String.format("INSERT INTO users " +
                            "(username, firstname, lastname, is_bot, telephone, email, date, role, description)" +
                            " VALUES (%s, %s, %s, %b, %s, %s, DEFAULT, %s, %s)",
                    username, firstname, lastname, isBot, telephone, email, role, description);
            statement.executeUpdate(query);
//            logger.trace("New User added to database {}", recipient);
        } catch (SQLException throwables) {
//            logger.error("Can't add User to database {}", recipient);
//            throwables.printStackTrace();
        }
    }

    public static int addUser(User user, Long chatId) {
        final String username = String.format("'%s'", user.getUserName());
        final String firstname = user.getFirstName() != null ? String.format("'%s'", user.getFirstName()) : "NULL";
        final String lastname = user.getLastName() != null ? String.format("'%s'", user.getLastName()) : "NULL";
        final Boolean isBot = user.getIsBot();

        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement()
        ) {
            final String query = String.format("INSERT INTO users " +
                            "(id, username, firstname, lastname, is_bot, date, role, chat_id)" +
                            " VALUES (%d, %s, %s, %s, %b, DEFAULT, DEFAULT, %d) ON CONFLICT (id) DO NOTHING",
                    user.getId(), username, firstname, lastname, isBot, chatId);
            return statement.executeUpdate(query);
//            logger.trace("New User added to database {}", user);
        } catch (SQLException throwables) {
//            logger.error("Can't add User to database {}", user);
            throwables.printStackTrace();
        }
        return 0;
    }

    public static String getUserRole(User user) {
        String role = "USER";
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement()
        ) {
            final String query = String.format("SELECT role FROM users WHERE username = '%s'", user.getUserName());
            final ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                role = resultSet.getString(1);
            }
        } catch (SQLException throwables) {
//            logger.error("Can't get Role from database for user {}", user.getUserName());
            throwables.printStackTrace();
        }
        return role;
    }

    public static String getUserRole(String userName) {
        String role = "USER";
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement()
        ) {
            final String query = String.format("SELECT role FROM users WHERE username = '%s'", userName);
            final ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                role = resultSet.getString(1);
            }
        } catch (SQLException throwables) {
//            logger.error("Can't get Role from database for user {}", userName);
            throwables.printStackTrace();
        }
        return role;
    }

    public static Integer getUncompletedTasks(User user) {
        Integer taskId = null;
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement()
        ) {
            final String query = String.format("SELECT id FROM tasks WHERE status = 'DRAFT' AND author = %d", user.getId());
            final ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        } catch (SQLException throwables) {
//            logger.error("Can't get Role from database for user {}", user.getUserName());
            throwables.printStackTrace();
        }
        return taskId;
    }

    public static Optional<Integer> getLastTaskId() {
        Integer taskId = null;
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement()
        ) {
            final String query = "SELECT id FROM tasks WHERE (status = 'DRAFT' OR status = 'READY') ORDER BY id DESC LIMIT 1";
            final ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                taskId = resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
//            logger.error("Can't get last Task from database");
            throwables.printStackTrace();
        }
        return Optional.ofNullable(taskId);
    }

    public static void newTask(String type, Integer authorUserId, String status) {
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement()
        ) {
            final String query = String.format("INSERT INTO tasks (type, status, author, creation_date) VALUES ('%s', '%s', %d, DEFAULT)", type, status, authorUserId);
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
//            logger.error("Can't add new Task with type {} for user with id {} to database ", type, authorUserId, throwables);
            throwables.printStackTrace();
        }
    }

    public static void setTaskFileId(Integer taskId, String fileId) {
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement()
        ) {
            final String query = String.format("UPDATE tasks SET file_id = '%s' WHERE id = %d ", fileId, taskId);
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
//            logger.error("Can't set new file_id {} for task with id {} to database ", fileId, taskId);
            throwables.printStackTrace();
        }
    }

    public static void setTaskMessage(Integer taskId, String message) {
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement()
        ) {
            final String query = String.format("UPDATE tasks SET message = '%s' WHERE id = %s ", message, taskId);
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
//            logger.error("Can't set new message {} for task with id {} to database ", message, taskId);
            throwables.printStackTrace();
        }
    }

    /**
     * Find last task by author
     *
     * @param userId authors id
     * @param status of task
     * @return last task
     */
    public static Task getLastTask(Integer userId, Status status) {
        final Task task = new Task();
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement()
        ) {
            final String query = String.format(
                    "SELECT id, message, file_id, type, status, author, creation_date FROM tasks WHERE author = %d AND status = '%s' ORDER BY id DESC LIMIT 1", userId, status
            );
            final ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                task.setId(resultSet.getInt(1));
                task.setMessage(resultSet.getString(2));
                task.setFileId(resultSet.getString(3));
                task.setType(resultSet.getString(4));
                task.setStatus(resultSet.getString(5));
                task.setAuthorId(resultSet.getInt(6));
                task.setCreationDate(resultSet.getTimestamp(7).toLocalDateTime());
            }
        } catch (SQLException throwables) {
//            logger.error("Can't get last Task from database ");
            throwables.printStackTrace();
        }
        return task;
    }

    public static void setTaskStatus(Integer taskId, Status status) {
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement()
        ) {
            final String query = String.format("UPDATE tasks SET status = '%s' WHERE id = %d ", status, taskId);
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
//            logger.error("Can't set new file_id {} for task with id {} to database ", status, taskId);
            throwables.printStackTrace();
        }
    }

    public static List<Long> getAllChatIds() {
        final List<Long> chatIds = new ArrayList<>();
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement()
        ) {
            final String query = "SELECT chat_id FROM users";
            final ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                chatIds.add(resultSet.getLong(1));
            }
        } catch (SQLException throwables) {
//            logger.error("Can't get all chat_id from database");
            throwables.printStackTrace();
        }
        return chatIds;
    }

    public static void deleteLastDraftTask(Integer authorId) {
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement()
        ) {
            final String query = String.format("DELETE FROM tasks WHERE author = %d ", authorId);
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
//            logger.error("Can't delete task with status 'DRAFT' or 'READY' by author {}", authorId);
            throwables.printStackTrace();
        }
    }

    public static void addAllTasksToUser(Integer userId) {
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement()
        ) {
            final String query = String.format("SELECT addAllTasksToUser(user_id => %d::integer)", userId);
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
//            logger.error("Can't delete task with status 'DRAFT' or 'READY' by author {}", authorId);
            throwables.printStackTrace();
        }
    }

    public static void addTaskToAllUsers(Integer taskId) {
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement()
        ) {
            final String query = String.format("SELECT addTaskToAllUsers(task => %d::integer)", taskId);
            statement.execute(query);
        } catch (SQLException throwables) {
//            logger.error("Can't delete task with status 'DRAFT' or 'READY' by author {}", authorId);
            throwables.printStackTrace();
        }
    }

    public static void deleteJob(Integer jobId) {
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement()
        ) {
            final String query = String.format("DELETE FROM jobs WHERE id = %d", jobId);
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
//            logger.error("Can't delete task with status 'DRAFT' or 'READY' by author {}", authorId);
            throwables.printStackTrace();
        }
    }

    public static List<Job> getAllJobs() {
        final List<Job> jobs = new ArrayList<>();
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement()
        ) {
            final String query = "SELECT j.id, " +
                    "(SELECT message FROM tasks AS t WHERE t.id = j.task_id), " +
                    "(SELECT file_id FROM tasks AS t WHERE t.id = j.task_id), " +
                    "(SELECT chat_id FROM users AS u WHERE u.id = j.recipient) " +
                    "FROM jobs AS j WHERE j.time_of_execute < NOW()";
            final ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                final Job job = new Job();
                job.setJobId(resultSet.getInt(1));
                job.setMessage(resultSet.getString(2));
                job.setFileId(resultSet.getInt(3));
                job.setChatId(resultSet.getInt(4));
                jobs.add(job);
            }
        } catch (SQLException throwables) {
//            logger.error("Can't get last Task from database ");
            throwables.printStackTrace();
        }
        return jobs;
    }

    public static Optional<MessageTuple> getCodeFileAndMessage(String codeword) {
        MessageTuple messageTuple = null;
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement()
        ) {
            final String query = String.format("SELECT file_id, message FROM codewords WHERE codeword = %s", codeword);
            final ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                messageTuple = new MessageTuple();
                messageTuple.setFileId(resultSet.getInt("file_id"));
                messageTuple.setMessage(resultSet.getString("message"));
            }
        } catch (SQLException throwables) {
//            logger.error("Can't get last Task from database ");
            throwables.printStackTrace();
        }
        return Optional.ofNullable(messageTuple);
    }

    public static int newCodeword(String codeword, String fileId, String message) {
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement()
        ) {
            final String query = String.format("INSERT INTO codewords " +
                            "(codeword_id, codeword, file_id, message)" +
                            " VALUES (DEFAULT, %s, %s, %s) ON CONFLICT (id) DO NOTHING", codeword, fileId, message);
            return statement.executeUpdate(query);
//            logger.trace("New User added to database {}", user);
        } catch (SQLException throwables) {
//            logger.error("Can't add User to database {}", user);
            throwables.printStackTrace();
        }
        return 0;
    }
}

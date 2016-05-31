package jsf.jdbc;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DbUtil {

    private static DbUtil instance;
    private DataSource dataSource;
    private String jndiName = "java:comp/env/jdbc/student_tracker";

    public static DbUtil getInstance() throws Exception {
        if (instance == null) {
            instance = new DbUtil();
        }

        return instance;
    }

    private DbUtil() throws Exception {
        this.dataSource = getDataSource();
    }


    private DataSource getDataSource() throws NamingException {
        Context context = new InitialContext();

        return (DataSource) context.lookup(this.jndiName);
    }

    public List<Student> getStudents() throws Exception {
        List<Student> students = new ArrayList<>();

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();

            String sql = "select * from student order by last_name";
            statement = connection.createStatement();

            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");

                Student student = new Student(id, firstName, lastName, email);

                students.add(student);
            }

            return students;

        } finally {
            close(connection, statement, resultSet);
        }
    }

    public void addStudent(Student s) throws Exception {

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();

            String sql = "insert into student (first_name, last_name, email) values (?, ?, ?)";
            statement = connection.prepareStatement(sql);

            statement.setString(1, s.getFirstName());
            statement.setString(2, s.getLastName());
            statement.setString(3, s.getEmail());

            statement.execute();

        } finally {
            close(connection, statement);
        }
    }

    public void updateStudent(Student student) throws Exception {

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();

            String sql = "update student "
                    + " set first_name=?, last_name=?, email=?"
                    + " where id=?";
            statement = connection.prepareStatement(sql);

            statement.setString(1, student.getFirstName());
            statement.setString(2, student.getLastName());
            statement.setString(3, student.getEmail());
            statement.setInt(4, student.getId());

            statement.execute();

        } finally {
            close(connection, statement);
        }
    }

    public void deleteStudent(int studentId) throws Exception {

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();

            String sql = "delete from student "
                   + " where id=?";
            statement = connection.prepareStatement(sql);

            statement.setInt(1, studentId);

            statement.execute();

        } finally {
            close(connection, statement);
        }
    }

    public Student getStudent(int studentId) throws Exception {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Student s = null;

        try {
            connection = getConnection();
            String sql = "select * from student where id = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, studentId);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                s = new Student(resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("email"));
            } else {
                throw new Exception("Could not find student id: " + studentId);
            }

            return s;

        } finally {
            close(connection, preparedStatement, resultSet);
        }

    }


    private Connection getConnection() throws Exception {

        return dataSource.getConnection();

    }

    private void close(Connection theConn, Statement theStmt) {
        close(theConn, theStmt, null);
    }

    private void close(Connection theConn, Statement theStmt, ResultSet theRs) {

        try {
            if (theRs != null) {
                theRs.close();
            }

            if (theStmt != null) {
                theStmt.close();
            }

            if (theConn != null) {
                theConn.close();
            }

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}

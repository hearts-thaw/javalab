import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class JdbcUtil {
    private Connection connect() {
        try {
            Context initCtxt = new InitialContext();
            Context envCtxt = (Context) initCtxt.lookup("java:comp/env");
            DataSource ds = (DataSource) envCtxt.lookup("jdbc/temp_flyway");
            return ds.getConnection();
        } catch (NamingException e) {
            throw new IllegalArgumentException("Can't instantiate context");
        } catch (SQLException e) {
            throw new IllegalStateException("Can't establish connection");
        }
    }

    int update(final String sql, Object... args) {
        int res;

        try (Connection conn = connect()) {
            PreparedStatement stmt = wrapObjects(conn.prepareStatement(sql), args);

            res = stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }

        return res;
    }

    <T> List<T> findAll(final String sql, RowMapper<T> rowMapper) {

        List<T> res = new LinkedList<>();

        try (Connection conn = connect()) {
            Statement stmt = conn.createStatement();

            ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next()) {
                res.add(rowMapper.mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }

        return res;
    }

    <T> List<T> findAll(final String sql, RowMapper<T> rowMapper, Object... args) {

        List<T> res = new LinkedList<>();

        try (Connection conn = connect()) {
            PreparedStatement stmt = wrapObjects(conn.prepareStatement(sql), args);

            ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next()) {
                res.add(rowMapper.mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }

        return res;
    }

    <T> T findOne(final String sql, RowMapper<T> rowMapper, Object... args) {

        T res = null;

        try (Connection conn = connect()) {
            PreparedStatement stmt = wrapObjects(conn.prepareStatement(sql), args);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                res = rowMapper.mapRow(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
        return res;

    }

    private PreparedStatement wrapObjects(PreparedStatement stmt, Object... args) throws SQLException {
        int k = 1;
        for (Object arg : args) {
//            Custom objects ( that's not checking in PreparedStatement.setObject() )
            if (arg instanceof Role) {
                arg = ((Role) arg).name();
                stmt.setObject(k++, arg);
                continue;
            }
            stmt.setObject(k++, arg);
        }
        return stmt;
    }
}

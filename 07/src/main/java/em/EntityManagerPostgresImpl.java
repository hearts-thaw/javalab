package em;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EntityManagerPostgresImpl implements EntityManager {

    private final DataSource dataSource;

    private Connection getConnection() {
        try {
            return dataSource.getConnection("postgres", "zyrf14aa");
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    //    region Class-To-SQL-Type Converters
    private final ClassToSqlTypeConverter shortClassToSqlTypeConverter = shortClass -> "smallint";
    private final ClassToSqlTypeConverter integerClassToSqlTypeConverter = integerClass -> "integer";
    private final ClassToSqlTypeConverter longClassToSqlTypeConverter = longClass -> "bigint";
    private final ClassToSqlTypeConverter idClassToSqlTypeConverter = idClass -> "bigserial";
    private final ClassToSqlTypeConverter doubleClassToSqlTypeConverter = doubleClass -> "double precision";
    private final ClassToSqlTypeConverter floatClassToSqlTypeConverter = floatClass -> "real";
    private final ClassToSqlTypeConverter stringClassToSqlTypeConverter = stringClass -> "text";
    private final ClassToSqlTypeConverter characterClassToSqlTypeConverter = characterClass -> "char(1)";
    private final ClassToSqlTypeConverter booleanClassToSqlTypeConverter = booleanClass -> "boolean";
//    endregion

    private Map<String, List<String>> convertFields(Field[] fields, Object entity) {
        Map<String, List<String>> fieldsConverted = new HashMap<>();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldTypeConverted;
            String fieldValue = "";
            if (entity != null) {
                try {
                    fieldValue = field.get(entity).toString();
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
            switch (field.getType().getName()) {
                case "java.lang.Short":
                case "short":
                    fieldTypeConverted = shortClassToSqlTypeConverter.convert(field.getType());
                    break;
                case "java.lang.Integer":
                case "int":
                    fieldTypeConverted = integerClassToSqlTypeConverter.convert(field.getType());
                    break;
                case "java.lang.Long":
                case "long":
                    if (field.getName().toLowerCase().equals("id")) {
                        fieldTypeConverted = idClassToSqlTypeConverter.convert(field.getType());
                    } else {
                        fieldTypeConverted = longClassToSqlTypeConverter.convert(field.getType());
                    }
                    break;
                case "java.lang.Double":
                case "double":
                    fieldTypeConverted = doubleClassToSqlTypeConverter.convert(field.getType());
                    break;
                case "java.lang.Float":
                case "float":
                    fieldTypeConverted = floatClassToSqlTypeConverter.convert(field.getType());
                    break;
                case "java.lang.String":
                    fieldValue = "'" + fieldValue + "'";
                    fieldTypeConverted = stringClassToSqlTypeConverter.convert(field.getType());
                    break;
                case "java.lang.Character":
                case "char":
                    fieldValue = "'" + fieldValue + "'";
                    fieldTypeConverted = characterClassToSqlTypeConverter.convert(field.getType());
                    break;
                case "java.lang.Boolean":
                case "boolean":
                    fieldTypeConverted = booleanClassToSqlTypeConverter.convert(field.getType());
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + field.getType().getName());
            }
            fieldsConverted.put(field.getName(), List.of(fieldTypeConverted, fieldValue));
        }
        return fieldsConverted;
    }


    private <T> T retrieveObjectFromResultSet(ResultSet rs, List<String> fieldNames, Class<T> entityClass) {
        T entity;
        try {
            entity = entityClass.getConstructor().newInstance();
            for (String name : fieldNames) {
                Field field = entityClass.getDeclaredField(name);
                field.setAccessible(true);
                switch (entityClass.getDeclaredField(name).getType().getName()) {
                    case "java.lang.Short":
                    case "short":
                        field.set(entity, rs.getShort(name));
                        break;
                    case "java.lang.Integer":
                    case "int":
                        field.set(entity, rs.getInt(name));
                        break;
                    case "java.lang.Long":
                    case "long":
                        field.set(entity, rs.getLong(name));
                        break;
                    case "java.lang.Double":
                    case "double":
                        field.set(entity, rs.getDouble(name));
                        break;
                    case "java.lang.Float":
                    case "float":
                        field.set(entity, rs.getFloat(name));
                        break;
                    case "java.lang.String":
                        field.set(entity, rs.getString(name));
                        break;
                    case "java.lang.Character":
                    case "char":
                        field.set(entity, rs.getString(name).substring(0, 1));
                        break;
                    case "java.lang.Boolean":
                    case "boolean":
                        field.set(entity, rs.getBoolean(name));
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + name);
                }
            }
        } catch (SQLException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
        return entity;
    }

    // TODO: make for arrays

    public EntityManagerPostgresImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public <T> void createTable(String tableName, Class<T> entityClass) {
        Connection conn = getConnection();
        try (Statement stmt = conn.createStatement()) {
            String start = "CREATE TABLE IF NOT EXISTS " + tableName + "(", end = ");";
            StringBuilder sb = new StringBuilder(start);
            convertFields(entityClass.getDeclaredFields(), null)
                    .forEach((name, info) -> sb.append(name).append(" ").append(info.get(0)).append(","));
            sb.deleteCharAt(sb.length() - 1);
            sb.append(end);
            System.out.println(sb.toString());

            stmt.executeUpdate(sb.toString());
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(String tableName, Object entity) {
        Connection conn = getConnection();
        Class<?> entityClass = entity.getClass();
        String start = "INSERT INTO " + tableName + " (", end = ");";
        StringBuilder sb = new StringBuilder(start), sbNames = new StringBuilder(), sbValues = new StringBuilder();
        convertFields(entityClass.getDeclaredFields(), entity)
                .forEach((name, info) -> {
                    sbNames.append(name).append(",");
                    sbValues.append(info.get(1)).append(",");
                });
        sbNames.deleteCharAt(sbNames.length() - 1);
        sbValues.deleteCharAt(sbValues.length() - 1);
        sb.append(sbNames).append(") VALUES (").append(sbValues).append(end);
        try (Statement stmt = conn.createStatement()) {
            System.out.println(sb.toString());
            stmt.executeUpdate(sb.toString());
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T, ID> T findById(String tableName, Class<T> resultType, Class<ID> idType, ID idValue) {
        Connection conn = getConnection();
        T res;
        try {
            Statement stmt = conn.createStatement();
            List<String> fields = new LinkedList<>();
            convertFields(resultType.getDeclaredFields(), null)
                    .forEach((name, info) -> fields.add(name));
            String sql = "SELECT " + String.join(", ", fields) + " FROM " + tableName + " WHERE id = " + idValue.toString() + " LIMIT 1;";
            System.out.println(sql);
            stmt.execute(sql);
            ResultSet rs = stmt.getResultSet();
            rs.next();
            res = retrieveObjectFromResultSet(rs, fields, resultType);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return res;
    }
}

package em;

public interface EntityManager {
    /**
     * сгенерировать CREATE TABLE на основе класса
     * create table account ( id integer, firstName varchar(255), ...))
     * createTable("account", User.class);
     */
    <T> void createTable(String tableName, Class<T> entityClass);

    /**
     * сканируем его поля
     * сканируем значения этих полей
     * генерируем insert into
     */
    void save(String tableName, Object entity);

    /**
     * сгенеририровать select
     * User user = entityManager.findById("account", User.class, Long.class, 10L);
     */
    <T, ID> T findById(String tableName, Class<T> resultType, Class<ID> idType, ID idValue);
}

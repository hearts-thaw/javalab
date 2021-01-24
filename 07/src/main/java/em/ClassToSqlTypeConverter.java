package em;

@FunctionalInterface
public interface ClassToSqlTypeConverter {
    String convert(Class<?> className);
}

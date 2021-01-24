import app.Component;

import java.lang.reflect.*;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> aClass = Class.forName("app.Component");
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            System.out.println(Modifier.toString(field.getModifiers()) + " " + field.getName());
        }

        Constructor constructor = aClass.getDeclaredConstructor(int.class, String.class);
        Component component = (Component) constructor.newInstance(1, "Asd");

        Method method = aClass.getDeclaredMethod("methodWithArgs", int.class, int.class);
        method.setAccessible(true);
        System.out.println(method.invoke(component, 1, 2));
    }
}

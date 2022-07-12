package net.breezeware.propel.di;

import net.breezeware.propel.annotation.Autowired;
import net.breezeware.propel.annotation.Component;
import net.breezeware.propel.annotation.ComponentScan;
import net.breezeware.propel.annotation.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.Boolean.TRUE;

@Configuration
public class ApplicationContext {

    private static final Map<Class<?>, Object> beanContext = new HashMap<>();

    public ApplicationContext(Class<?> gClass) {
        CustomInitializer.initialize(gClass);
    }

    public <T> T getBean(Class<T> gClass) throws IllegalAccessException {
        T t = (T) beanContext.get(gClass);
        Field[] declaredFields = gClass.getDeclaredFields();
        injectBean(t, declaredFields);
        return t;
    }

    private <T> void injectBean(T t, Field[] declaredFields) throws IllegalAccessException {
        for (Field field :
                declaredFields) {
            field.setAccessible(TRUE);
            if (field.isAnnotationPresent(Autowired.class)) {
                Class<?> fieldClass = field.getType();
                Object o = beanContext.get(fieldClass);
                field.set(t, o);
                injectBean(o, fieldClass.getDeclaredFields());
            }
        }
    }

    private static class CustomInitializer {

        private static void initialize(Class<?> gClass) {
            if (gClass.isAnnotationPresent(Configuration.class)) {
                ComponentScan componentScanAnnotation = gClass.getAnnotation(ComponentScan.class);
                String[] componentScanPackageNames = componentScanAnnotation.packageNames();
                for (String componentScanPackageName :
                        componentScanPackageNames) {
                    try {
//                        System.out.println(componentScanPackageName);
                        String packageStructure = "target/classes/" + componentScanPackageName.replace(".", "/");
                        File[] files = getClassFiles(new File(packageStructure));
//                        System.out.println(Arrays.stream(files).toList());
                        for (File file : files) {
                            String classFileName = componentScanPackageName + "." + file.getName().replace(".class", "");
//                            System.out.println(classFileName);
                            Class<?> aClass = Class.forName(classFileName);
                            if (aClass.isAnnotationPresent(Component.class)) {
                                Object aClassObj = aClass.getConstructor().newInstance();
                                beanContext.put(aClass, aClassObj);
//                                System.out.println("beanContext - " + beanContext.toString());
                            }
                        }
                    } catch (FileNotFoundException | ClassNotFoundException | InvocationTargetException |
                             NoSuchMethodException | IllegalAccessException | InstantiationException e) {
                        throw new RuntimeException(e);
                    }
                }
//                System.out.println("beanContext - " + beanContext.toString());
//                String componentScanPackageName = componentScanAnnotation.packageName();
//                String packageStructure = "target/classes/" + componentScanPackageName.replace(".", "/");
//
//                try {
//                    File[] files = getClassFiles(new File(packageStructure));
//                    for (File file : files) {
//                        String classFileName = componentScanPackageName + "." + file.getName().replace(".class", "");
//                        Class<?> aClass = Class.forName(classFileName);
//                        if (aClass.isAnnotationPresent(Component.class)) {
//                            Object aClassObj = aClass.getConstructor().newInstance();
//                            beanContext.put(aClass, aClassObj);
//                        }
//                    }
//                } catch (FileNotFoundException | ClassNotFoundException | InvocationTargetException |
//                         NoSuchMethodException | IllegalAccessException | InstantiationException e) {
//                    throw new RuntimeException(e);
//                }
            } else
                throw new RuntimeException();
        }

        private static File[] getClassFiles(File file) throws FileNotFoundException {
            if (file.exists()) {
//                String[] fileList = file.list();
//                System.out.println(Arrays.stream(fileList).toList());
//                for (String f : fileList) {
//                    File internalFile = new File(file.getPath() + "/" + f);
//                    System.out.println(internalFile);
//                    if (internalFile.isDirectory()) {
//                        System.out.println("isDirectory - " + true);
//                        File[] classFiles = getClassFiles(internalFile);
////                        files += classFiles;
//                        System.out.println("classFiles - " + classFiles);
//                    }
//                }
//
//                System.out.println("files - " + file);
////                if (file.isDirectory()) {
////                    System.out.println("files - " + file.toString());
//////                    getClassFiles(new File(file.getPath()));
////                }
//                System.out.println(Arrays.stream(file.list()).toList());
                File[] files = file.listFiles(f -> f.getName().endsWith(".class"));
                return files;
            } else
                throw new FileNotFoundException();
        }
    }
}

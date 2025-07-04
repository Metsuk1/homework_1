package com.example.homework_1.runner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassScanner {
    public static Set<Class<?>> getClasses(String packageName) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        Set<Class<?>> classes = new HashSet<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                classes.addAll(findClasses(new File(resource.getFile()), packageName));
            }
        }
        return classes;
    }

    private static Set<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        if (!directory.exists()) {
            return Collections.emptySet();
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return Collections.emptySet();
        }

        return Arrays.stream(files)
                .flatMap(file -> {
                    try {
                        if (file.isDirectory()) {
                            return findClasses(file, packageName + "." + file.getName()).stream();
                        }
                        else if (file.getName().endsWith(".class")) {
                            String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);

                            return Stream.of(Class.forName(className));
                        }

                        return Stream.empty();
                    } catch (ClassNotFoundException e) {

                        return Stream.empty();
                    }
                })
                .collect(Collectors.toSet());
    }
}
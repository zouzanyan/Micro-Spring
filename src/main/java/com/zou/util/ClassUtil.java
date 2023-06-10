package com.zou.util;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author zou
 */
public class ClassUtil {
    public static List<Class<?>> getClasses(String pack) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        String packageDirUrl = pack.replace('.', '/');
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packageDirUrl);

        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            if ("file".equals(url.getProtocol())) {
                String urlFix = URLDecoder.decode(url.getFile(), "utf8");
                //  /C:/Users/zou/IdeaProjects/Micro-Spring/out/production/Micro-Spring/com/zou/util/entity
                classes = findClassesByFile(urlFix, pack);
            }
        }

        return classes;

    }

    private static List<Class<?>> findClassesByFile(String url, String pack) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        File file = new File(url);
        //文件不存在或者不是目录
        if (!file.exists() || !file.isDirectory()) {
            return null;
        }
        //以获取

        File[] files = file.listFiles((file1) -> file1.isDirectory() || file1.getName().endsWith(".class"));
        for (File f : files) {
            if (f.isDirectory()) {
                findClassesByFile(url, pack);
            } else {
                String className = f.getName().substring(0, f.getName().length() - 6);
                classes.add(Thread.currentThread().getContextClassLoader().loadClass(pack + "." + className));
            }
        }
        return classes;
    }

    public static void main(String[] args) throws Exception {
        List<Class<?>> classes = getClasses("com.zou.entity");
        System.out.println(classes);
    }
}

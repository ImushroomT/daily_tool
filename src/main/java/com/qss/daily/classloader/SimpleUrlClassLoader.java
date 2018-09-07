package com.qss.daily.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author qiushengsen
 * @dateTime 2018/7/21 下午7:07
 * @descripiton
 **/
public class SimpleUrlClassLoader extends URLClassLoader{

    private String classPath;

    public SimpleUrlClassLoader(URL[] urls, String cp) {
        super(urls);
        this.classPath = cp;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class c = findLoadedClass(name);
        if (c != null) {
            return c;
        }
        if (!name.startsWith(classPath)) {
            return super.loadClass(name);
        }
        return super.findClass(name);
    }
}

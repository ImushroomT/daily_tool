package com.qss.daily;

import com.qss.daily.classloader.SimpleUrlClassLoader;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author qiushengsen
 * @company 京东成都研究院-供应链
 * @dateTime 2018/7/21 下午7:14
 * @descripiton
 **/
public class SimpleUrlClassLoaderTest {
    @Test
    public void simple_test() throws MalformedURLException, ClassNotFoundException {
        URL url= new URL("file:///Users/cdqiushengsen/Documents/project/cgliblearn/LoadClass/target/classes/");
        SimpleUrlClassLoader classLoader = new SimpleUrlClassLoader(new URL[]{url}, "com.qss.cglib.load");
        Class c = classLoader.loadClass("com.qss.cglib.load.TestClass");
        System.out.println(c.getName());
    }

    @Test
    public void test_url() throws IOException, ClassNotFoundException {
        URL url= new URL("file:///Users/cdqiushengsen/Documents/project/cgliblearn/LoadClass/target/classes/");
        Object o = url.getContent();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{url});
        Class c = classLoader.loadClass("com.qss.cglib.load.TestClass");
        System.out.println(c.getName());
    }
}

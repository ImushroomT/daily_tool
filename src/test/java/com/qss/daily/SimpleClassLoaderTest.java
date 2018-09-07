package com.qss.daily;

import com.qss.daily.classloader.SimpleClassLoader;
import org.junit.Test;

/**
 * @author qiushengsen
 * @company 京东成都研究院-供应链
 * @dateTime 2018/7/21 下午6:31
 * @descripiton
 **/
public class SimpleClassLoaderTest {
    @Test
    public void simple_test() throws ClassNotFoundException {
        SimpleClassLoader classLoader = new SimpleClassLoader(
                "/Users/cdqiushengsen/Documents/project/cgliblearn/LoadClass/target/classes"
                , "com.qss.cglib.load");
        Class c = classLoader.loadClass("com.qss.cglib.load.TestClass");
        System.out.println(c.getName());
    }
}

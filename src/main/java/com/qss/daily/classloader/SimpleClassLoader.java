package com.qss.daily.classloader;

import java.io.*;

/**
 * @author qiushengsen
 * @dateTime 2018/7/21 下午6:06
 * @descripiton
 **/
public class SimpleClassLoader extends ClassLoader {
   private String classPath;

   private String pos;

    public SimpleClassLoader(String pos, String classPath) {
        this.classPath = classPath;
        this.pos = pos;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.startsWith(classPath)) {
            try {
                byte[] data = getClassData(name);
                return defineClass(name, data, 0, data.length);
            } catch (IOException e) {
                throw new ClassNotFoundException(e.getMessage());
            }
        } else {
            return super.findClass(name);
        }
    }

    private byte[] getClassData(String className) throws IOException {
        String path = pos + File.separatorChar +
                className.replace('.', File.separatorChar) + ".class";
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
        byte[] buffer = new byte[1024];
        int readLen;
        while (bis.available() > 0) {
            readLen = bis.read(buffer);
            byteArrayOutputStream.write(buffer, 0, readLen);
        }
        return byteArrayOutputStream.toByteArray();
    }
}

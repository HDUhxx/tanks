package com.threetanks.config;

/**
 * author: haiyangp
 * date:  2017/9/23
 * desc: 配置常量
 */
public class Constans {
    private Constans() {
    }

    public static final String clasName = "Solution";
    //部署的时候可能要更改，现在用类来实现，应该也可以用配置文件的形式实现，以后可以改成配置文件的形式
    //要考虑不同用户情况，可以选择在处理代码中加上uuid作为文件名，顺便把这个作为返回值返回给前台。
    public static final String classPath = "C:\\compile\\";
//    public static final String classPath = "/opt/compile/";
    public static final String excuteMainMethodName = "excuteMainMethodWithClass";
}

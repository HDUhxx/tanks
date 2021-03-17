package com.threetanks.service.impl;

import com.threetanks.controller.viewobject.ReturnResult;
import com.threetanks.dataobject.Model;
import com.threetanks.error.BusinessException;
import com.threetanks.error.EmBusinessError;
import com.threetanks.mapper.ModelMapper;
import com.threetanks.service.JavaComplieService;
import com.threetanks.util.ClassClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;

import static com.threetanks.config.Constans.clasName;
import static com.threetanks.config.Constans.classPath;

@Service
public class JavaComplieServiceImpl implements JavaComplieService {

    @Autowired
    ModelMapper modelMapper;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Class complie(String javaSource) throws Exception {

        //可以获得环境下边的编译器的引用
        //通过 ToolProvider 取得 JavaCompiler 对象，JavaCompiler 对象是动态编译工具的主要对象
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        // 通过 JavaCompiler 取得标准 StandardJavaFileManager 对象，StandardJavaFileManager 对象主要负责
        // 编译文件对象的创建，编译的参数等等，我们只对它做些基本设置比如编译 CLASSPATH 等。
        if (javaCompiler == null){
            logger.info("javaCompile 为 null");
        }
        StandardJavaFileManager standardFileManager = javaCompiler.getStandardFileManager(null, null, null);

        if (standardFileManager == null){
            logger.info("standardFileManager 为null");
        }
        // 因为是从内存中读取 Java 源文件，所以需要创建我们的自己的 JavaFileObject，即 InMemoryJavaFileObject
        StringObject so = new StringObject(clasName, javaSource);
        if (so == null){
            logger.info("so 为null");
        }
        //JavaFileObject是个接口
        Boolean result = null;
        try {
            JavaFileObject file = so;
            //代码指定的被编译Java文件所依赖的源文件所在的目录。
            //这里的classPath以后部署的时候需要修改，-d也有可能会修改
            // 编译目的地设置
            Iterable options = Arrays.asList("-d", classPath);
            Iterable<? extends JavaFileObject> files = Arrays.asList(file);
            //获取编译任务
            JavaCompiler.CompilationTask task = javaCompiler.getTask(null, standardFileManager, null, options, null, files);
            //执行编译任务
            result = task.call();
        } catch (Exception e) {
            logger.info("some null");
            throw new BusinessException(EmBusinessError.COMPILE_ERROR);
        }
        if (result) {
            logger.info("complie java source success!");
        } else {
            logger.info("complie java scoure fail!");
            throw new BusinessException(EmBusinessError.COMPILE_ERROR);
        }
        return loadClass(clasName);
    }

    @Override
    public ReturnResult excuteMainMethod(Class clazz) throws Exception
    {
        return excuteMainMethodWithClass(clazz);
    }

    /**
     * 加载CLASS
     *
     * @param clasName 类名
     * @return class文件
     */
    public Class loadClass(String clasName) throws Exception {
//        Class<?> clazz = Class.forName("Main");
        //用自定义classLoader加载这个class
        //getClass()：取得当前对象所属的Class对象
        //getClassLoader()：取得该Class对象的类装载器
        ClassClassLoader classClassLoader = new ClassClassLoader(getClass().getClassLoader());
        Class<?> clazz = classClassLoader.loadClass(clasName);
        return clazz;
    }


    @Override
    public ReturnResult excuteMainMethod(Class clazz,double R, double T,String modelId) throws Exception {
        if(modelId==null){
            throw new BusinessException(EmBusinessError.MODELID_NOT_EXIST);
        }
        Model model = modelMapper.selectByPrimaryKey(modelId);
        double k1 = model.getK1();
        double k2 = model.getK2();
        double a1 = model.getA1();
        double a2 = model.getA2();
        return excuteMainMethodWithClass(clazz);
    }

    @Override
    public ReturnResult excuteMainMethod(Class clazz, String[] args) throws Exception {
        return null;
    }



    /*private ReturnResult excuteMainMethodWithClass(Class clazz, double R, double T,double k1,double k2, double a1, double a2) throws Exception {
        int rangeT = 2000;
        double[][] result = new double[2][rangeT];//返回的结果
        double[] y0 = new double[rangeT];//液位1
        double[] y1 = new double[rangeT];//液位2
        double[] e = new double[rangeT];
        double[] u = new double[rangeT];
        Method alg = clazz.getMethod("alg", double.class, double.class, double.class, double.class);
        Object obj = clazz.newInstance();

        double mu = 1+(a1+a2)*T+a1*a2*T*T;
        e[0]=R;
        u[0]= (double) alg.invoke(obj,0.0,0.0,0.0,e[0]);
        y0[0]=(k1*T*T*u[0])/(1+a1*T);
        y1[0]=(k1*k2*T*T*T*u[0])/mu;

        e[1]=R-y1[0];
        u[1]= (double) alg.invoke(obj,u[0],0.0,e[0],e[1]);
        y0[1]=((2+a1*T)*y0[0]+k1*T*T*(u[1]-u[0]))/(1+a1*T);
        y1[1]=(k1*k2*T*T*T*(u[1]-u[0])+(3+2*(a1+a2)*T+a1*a2*T*T)*y1[0])/mu;

        e[2]=R-y1[1];
        u[2]= (double) alg.invoke(obj,u[1],e[0],e[1],e[2]);
        y0[2]=((2+a1*T)*y0[1]-y0[0]+k1*T*T*(u[2]-u[1]))/(1+a1*T);
        y1[2]=(k1*k2*T*T*T*(u[2]-u[1])+(3+2*(a1+a2)*T+a1*a2*T*T)*y1[1]+(-3-(a1+a2)*T)*y1[0])/mu;

        for (int k = 3; k < rangeT; k++) {
            e[k]=R-y1[k-1];
            u[k]= (double) alg.invoke(obj,u[k-1],e[k-2],e[k-1],e[k]);
            y0[k]=((2+a1*T)*y0[k-1]-y0[k-2]+k1*T*T*(u[k]-u[k-1]))/(1+(a1)*T);
            y1[k]=(k1*k2*T*T*T*(u[k]-u[k-1])+(3+2*(a1+a2)*T+a1*a2*T*T)*y1[k-1]+(-3-(a1+a2)*T)*y1[k-2]+y1[k-3])/mu;
        }
        try {
            saveTwo(y1);
            saveTwo(y0);
        }catch (Exception ex){
            throw new BusinessException(EmBusinessError.MODEL_PARAMS_EX);
        }
        result[0] = y0;
        result[1] = y1;
        ReturnResult returnResult = new ReturnResult();
        //临时id,并没有实现数据库的插入，以后可以加上。
        returnResult.setPidId("66666666");
        returnResult.setResult(result);
        return returnResult;
    }*/
    private ReturnResult excuteMainMethodWithClass(Class clazz) throws Exception {
        Method excuteMainMethodWithClass = clazz.getMethod("excuteMethod");
        Object obj = clazz.newInstance();
        double[][] result =(double[][]) excuteMainMethodWithClass.invoke(obj);
        try {
            /*if (result.length == 2){
                saveTwo(result[0]);
                saveTwo(result[1]);
            }else {
                saveTwo(result[0]);
            }*/
            for (int i = 0; i < result.length; i++) {
                saveTwo(result[i]);
            }

        }catch (Exception ex){
            throw new BusinessException(EmBusinessError.MODEL_PARAMS_EX);
        }
        ReturnResult returnResult = new ReturnResult();
        //临时id,并没有实现数据库的插入，以后可以加上。
        returnResult.setPidId("66666666");
        returnResult.setResult(result);
        return returnResult;
    }

    /**
     * 将args参数设为程序运行时的参数
     *
     * @param args 参数数组
     */
    private void setInputArgs(String[] args) {
        StringBuffer argSb = new StringBuffer();
        for (String argItem : args) {
            argSb.append(argItem);
            argSb.append(" ");
        }
        BufferedInputStream argInputStrem = new BufferedInputStream(new ByteArrayInputStream(argSb.toString().getBytes()));
        System.setIn(argInputStrem);
    }


    // 自定义 StringObject 实现了 SimpleJavaFileObject，指定 string 为 java 源代码，这样就不用将源代码
    // 存在内存中，直接从变量中读入即可。
    //为了让代码不从java文件中加载，直接从各种渠道得到字符代码，从字符中加载，需要自己继承 SimpleJavaFileObject 类来实现。
    private class StringObject extends SimpleJavaFileObject {
        private String contents = null;

        public StringObject(String clasName, String contents) throws Exception {
            //Kind枚举类，SOURCE表示java
            super(URI.create("String:///" + clasName + Kind.SOURCE.extension), Kind.SOURCE);
            this.contents = contents;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return contents;
        }
    }

    public void saveTwo(double y[]) {
        for (int i = 0; i < y.length; i++) {
            BigDecimal bg = new BigDecimal(y[i]);
            y[i] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
    }

}

package com.threetanks.service;

import com.threetanks.controller.viewobject.ReturnResult;

/**
 * desc: JAVA编译器service接口
 */
public interface JavaComplieService {

    /**
     * 编译，并获取编译后的class类
     *
     * @param javaSource JAVA代码
     * @return 编译后的CLASS
     */
    Class complie(String javaSource) throws Exception;

    /**
     * 执行MAIN方法
     *
     * @param clazz 编译后的CLASS
     * @return 执行结果
     */
    ReturnResult excuteMainMethod(Class clazz) throws Exception;

    /**
     * 执行MAIN方法
     *
     * @param clazz 编译后的CLASS
     * @return 执行结果
     */
    ReturnResult excuteMainMethod(Class clazz,double R, double T,String modelId) throws Exception;

    /**
     * 执行MAIN方法
     *
     * @param clazz 编译后的CLASS
     * @param args  运行参数数组
     * @return 执行结果
     */
    ReturnResult excuteMainMethod(Class clazz, String[] args) throws Exception;
}


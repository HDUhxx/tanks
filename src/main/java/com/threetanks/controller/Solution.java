package com.threetanks.controller;

/**
 * R:输入值
 * T:采样周期
 * k1：水箱1的比例系数
 * a1：水箱1的时间常数
 * k2：水箱2的比例系数
 * a2：水箱2的时间常数
 * u：控制量
 * kp：比例系数
 * ki：积分系数
 * kd：微分系数
 */
public class Solution {
    public double[][] excuteMethod() throws Exception {
        double R = 100;
        double T = 0.1;
        double k1 = 5;
        double a1 = 2;
        double k2 = 1;
        double a2 = 1;
        double kp = 100;
        double ki = 1;
        double kd = 1;
        int rangeT = 2000;
        double[][] result = new double[2][rangeT];//返回的结果
        double[] y0 = new double[rangeT];//液位1
        double[] y1 = new double[rangeT];//液位2
        double[] e = new double[rangeT];
        double[] u = new double[rangeT];

        double mu = 1+(a1+a2)*T+a1*a2*T*T;
        e[0]=R;
        u[0]= kp*e[0]+ki*e[0]+kd*e[0];
        y0[0]=(k1*T*T*u[0])/(1+a1*T);
        y1[0]=(k1*k2*T*T*T*u[0])/mu;

        e[1]=R-y1[0];
        u[1]= u[0]+kp*(e[1]-e[0])+ki*e[1]+kd*(e[1]-2*e[0]);
        y0[1]=((2+a1*T)*y0[0]+k1*T*T*(u[1]-u[0]))/(1+a1*T);
        y1[1]=(k1*k2*T*T*T*(u[1]-u[0])+(3+2*(a1+a2)*T+a1*a2*T*T)*y1[0])/mu;

        e[2]=R-y1[1];
        u[2]= u[1]+kp*(e[2]-e[1])+ki*e[2]+kd*(e[2]-2*e[1]+e[0]);
        y0[2]=((2+a1*T)*y0[1]-y0[0]+k1*T*T*(u[2]-u[1]))/(1+a1*T);
        y1[2]=(k1*k2*T*T*T*(u[2]-u[1])+(3+2*(a1+a2)*T+a1*a2*T*T)*y1[1]+(-3-(a1+a2)*T)*y1[0])/mu;

        for (int k = 3; k < rangeT; k++) {
            e[k]=R-y1[k-1];
            u[k]= u[k-1]+kp*(e[k]-e[k-1])+ki*e[k]+kd*(e[k]-2*e[k-1]+e[k-2]);
            y0[k]=((2+a1*T)*y0[k-1]-y0[k-2]+k1*T*T*(u[k]-u[k-1]))/(1+(a1)*T);
            y1[k]=(k1*k2*T*T*T*(u[k]-u[k-1])+(3+2*(a1+a2)*T+a1*a2*T*T)*y1[k-1]+(-3-(a1+a2)*T)*y1[k-2]+y1[k-3])/mu;
        }
        result[0]=y0;
        result[1]=y1;
        return result;
    }

}



















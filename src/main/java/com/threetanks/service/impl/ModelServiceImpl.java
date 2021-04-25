package com.threetanks.service.impl;

import com.threetanks.controller.viewobject.ReturnResult;
import com.threetanks.dataobject.*;
import com.threetanks.error.BusinessException;
import com.threetanks.error.EmBusinessError;
import com.threetanks.mapper.*;
import com.threetanks.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ModelServiceImpl implements ModelService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ModelOneMapper modelOneMapper;

    @Autowired
    ModelPidMapper modelPidMapper;

    @Autowired
    ModelChuanPidMapper modelChuanPidMapper;

    @Autowired
    ModelDisturbPidMapper modelDisturbPidMapper;

    @Autowired
    ModelDisturbChuanPidMapper modelDisturbChuanPidMapper;

    @Override
    @Transactional
    public String createModel(Double k1, Double k2, Double a1, Double a2) throws BusinessException {
        if(k1==null||k2==null||a1==null||a2==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        Model model = new Model();
        model.setK1(k1);
        model.setK2(k2);
        model.setA1(a1);
        model.setA2(a2);
        modelMapper.insert(model);
        return model.getModelId();
    }

    @Override
    @Transactional
    public String createModel(Double k1, Double a1) throws BusinessException {
        if(k1==null||a1==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        ModelOne modelOne = new ModelOne();
        modelOne.setK1(k1);
        modelOne.setA1(a1);
        modelOneMapper.insert(modelOne);
        return modelOne.getModelId();
    }

    @Override
    @Transactional
    //easypid控制实验
    public ReturnResult easyPID(Double kp, Double ki, Double kd,
                                String modelId, Double T, Double R) throws BusinessException {
        if(kp==null||ki==null||kd==null||T==null||R==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        if(modelId==null||modelId.length()==0){
            throw new BusinessException(EmBusinessError.MODELID_NOT_EXIST);
        }
        ModelOne modelOne = modelOneMapper.selectByPrimaryKey(modelId);
        double k1 = modelOne.getK1();
        double a1 = modelOne.getA1();
        int rangeT = 5000;
        double[][] result = easypid(kp, ki, kd, k1, a1, T, rangeT, R);
        ModelPid modelPid = new ModelPid();
        modelPid.setKp(kp);
        modelPid.setKi(ki);
        modelPid.setKd(kd);
        modelPid.setT(T);
        modelPid.setR(R);
        modelPid.setModelId(modelId);
        modelPidMapper.insert(modelPid);
        ReturnResult returnResult = new ReturnResult();
        returnResult.setPidId(modelPid.getPidId());
        returnResult.setResult(result);
        return returnResult;
    }

    @Override
    @Transactional
    //integralSeparationPID控制实验
    public ReturnResult integralSeparationPID(Double kp, Double ki, Double kd,
                                String modelId, Double T, Double R,Double yu) throws BusinessException {
        if(kp==null||ki==null||kd==null||T==null||R==null||yu==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        if(modelId==null||modelId.length()==0){
            throw new BusinessException(EmBusinessError.MODELID_NOT_EXIST);
        }
        ModelOne modelOne = modelOneMapper.selectByPrimaryKey(modelId);
        double k1 = modelOne.getK1();
        double a1 = modelOne.getA1();
        int rangeT = 5000;
        double[][] result = integralSeparationPID(kp, ki, kd, k1, a1, T, rangeT, R,yu);
        ModelPid modelPid = new ModelPid();
        modelPid.setKp(kp);
        modelPid.setKi(ki);
        modelPid.setKd(kd);
        modelPid.setT(T);
        modelPid.setR(R);
        modelPid.setModelId(modelId);
        modelPidMapper.insert(modelPid);
        ReturnResult returnResult = new ReturnResult();
        returnResult.setPidId(modelPid.getPidId());
        returnResult.setResult(result);
        return returnResult;
    }

    @Override
    @Transactional
    //oneCascade控制
    public ReturnResult oneCascade(Double kp, Double ki, Double kd, String modelId, Double T, Double R, Double kp1, Double ki1, Integer signal, String disturbvalue) throws BusinessException {
        if(kp==null||ki==null||kd==null||kp1==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        if(modelId==null||modelId.length()==0){
            throw new BusinessException(EmBusinessError.MODELID_NOT_EXIST);
        }
        ModelOne modelOne = modelOneMapper.selectByPrimaryKey(modelId);
        double k1 = modelOne.getK1();
        double a1 = modelOne.getA1();
        int rangeT = 5000;
        double[][] result = oneCascade(kp,ki,kd,k1,a1,T,rangeT,R,kp1,ki1,signal,disturbvalue);
        ModelChuanPid modelChuanPid = new ModelChuanPid();
        modelChuanPid.setKp(kp);
        modelChuanPid.setKi(ki);
        modelChuanPid.setKd(kd);
        modelChuanPid.setKp1(kp1);
        modelChuanPid.setKi1(ki1);
        modelChuanPid.setT(T);
        modelChuanPid.setR(R);
        modelChuanPid.setModelId(modelId);
        modelChuanPidMapper.insert(modelChuanPid);
        ReturnResult returnResult = new ReturnResult();
        returnResult.setPidId(modelChuanPid.getChuanPidId());
        returnResult.setResult(result);
        return returnResult;
    }


    @Override
    @Transactional
    //无干扰pid控制实验
    public ReturnResult noDisturbPID(Double kp, Double ki, Double kd,
                                     String modelId, Double T, Double R) throws BusinessException {
        if(kp==null||ki==null||kd==null||T==null||R==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        if(modelId==null){
            throw new BusinessException(EmBusinessError.MODELID_NOT_EXIST);
        }
        Model model = modelMapper.selectByPrimaryKey(modelId);
        double k1 = model.getK1();
        double k2 = model.getK2();
        double a1 = model.getA1();
        double a2 = model.getA2();
        int rangeT = 2000;
        double[][] result = noDispid(kp, ki, kd, k1, k2, a1, a2, T, rangeT, R);
        ModelPid modelPid = new ModelPid();
        modelPid.setKp(kp);
        modelPid.setKi(ki);
        modelPid.setKd(kd);
        modelPid.setT(T);
        modelPid.setR(R);
        modelPid.setModelId(modelId);
        modelPidMapper.insert(modelPid);
        ReturnResult returnResult = new ReturnResult();
        returnResult.setPidId(modelPid.getPidId());
        returnResult.setResult(result);
        return returnResult;
    }

    @Override
    @Transactional
    //无干扰串级控制
    public ReturnResult noDisturbChuanPID(Double kp, Double ki, Double kd, String modelId,
                                          Double T, Double R, Double kp1, Double ki1) throws BusinessException {
        if(kp==null||ki==null||kd==null||kp1==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        if(modelId==null){
            throw new BusinessException(EmBusinessError.MODELID_NOT_EXIST);
        }
        Model model = modelMapper.selectByPrimaryKey(modelId);
        double k1 = model.getK1();
        double k2 = model.getK2();
        double a1 = model.getA1();
        double a2 = model.getA2();
        int rangeT = 2000;
        double[][] result = noDisChuanpid(kp, ki, kd, k1, k2, a1, a2, T, rangeT, R,kp1,ki1);
        ModelChuanPid modelChuanPid = new ModelChuanPid();
        modelChuanPid.setKp(kp);
        modelChuanPid.setKi(ki);
        modelChuanPid.setKd(kd);
        modelChuanPid.setKp1(kp1);
        modelChuanPid.setKi1(ki1);
        modelChuanPid.setT(T);
        modelChuanPid.setR(R);
        modelChuanPid.setModelId(modelId);
        modelChuanPidMapper.insert(modelChuanPid);
        ReturnResult returnResult = new ReturnResult();
        returnResult.setPidId(modelChuanPid.getChuanPidId());
        returnResult.setResult(result);
        return returnResult;
    }

    @Override
    @Transactional
    //有干扰pid控制
    public ReturnResult disturbPID(Double kp, Double ki, Double kd, String modelId, Double T, Double R, Integer signal, String disturbvalue) throws BusinessException {
        if(kp==null||ki==null||kd==null||T==null||R==null||signal==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        if(modelId==null){
            throw new BusinessException(EmBusinessError.MODELID_NOT_EXIST);
        }
        Model model = modelMapper.selectByPrimaryKey(modelId);
        double k1 = model.getK1();
        double k2 = model.getK2();
        double a1 = model.getA1();
        double a2 = model.getA2();
        int rangeT = 2000;
        double[][] result = dispid(kp, ki, kd, k1, k2, a1, a2, T, rangeT, R,signal,disturbvalue);//TODO
        ModelDisturbPid modelDisturbPid = new ModelDisturbPid();
        modelDisturbPid.setKp(kp);
        modelDisturbPid.setKi(ki);
        modelDisturbPid.setKd(kd);
        modelDisturbPid.setT(T);
        modelDisturbPid.setR(R);
        modelDisturbPid.setModelId(modelId);
        if(!(signal==1||signal==2||signal==3)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        modelDisturbPid.setSig(signal);
        modelDisturbPid.setDisturbValue(disturbvalue);
        modelDisturbPidMapper.insert(modelDisturbPid);
        ReturnResult returnResult = new ReturnResult();
        returnResult.setPidId(modelDisturbPid.getDisturbPidId());
        returnResult.setResult(result);
        return returnResult;
    }

    @Override
    @Transactional
    //有干扰串级控制
    public ReturnResult disturbChuanPID(Double kp, Double ki, Double kd, String modelId, Double T, Double R, Double kp1, Double ki1, Integer signal, String disturbvalue) throws BusinessException {
        if(kp==null||ki==null||kd==null||kp1==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        if(modelId==null){
            throw new BusinessException(EmBusinessError.MODELID_NOT_EXIST);
        }
        Model model = modelMapper.selectByPrimaryKey(modelId);
        double k1 = model.getK1();
        double k2 = model.getK2();
        double a1 = model.getA1();
        double a2 = model.getA2();
        int rangeT = 5000;
        double[][] result = disChuanpid(kp,ki,kd,k1,k2,a1,a2,T,rangeT,R,kp1,ki1,signal,disturbvalue);
        ModelChuanPid modelChuanPid = new ModelChuanPid();
        modelChuanPid.setKp(kp);
        modelChuanPid.setKi(ki);
        modelChuanPid.setKd(kd);
        modelChuanPid.setKp1(kp1);
        modelChuanPid.setKi1(ki1);
        modelChuanPid.setT(T);
        modelChuanPid.setR(R);
        modelChuanPid.setModelId(modelId);
        modelChuanPidMapper.insert(modelChuanPid);
        ReturnResult returnResult = new ReturnResult();
        returnResult.setPidId(modelChuanPid.getChuanPidId());
        returnResult.setResult(result);
        return returnResult;
    }

    @Override
    public ReturnResult disturbPreChuanPID(Double kp, Double ki, Double kd, String modelId, Double T, Double R, Double kp1, Double ki1, Integer signal, String disturbvalue,String disturbvalue1) throws BusinessException
    {
        if(kp==null||ki==null||kd==null||kp1==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        if(modelId==null){
            throw new BusinessException(EmBusinessError.MODELID_NOT_EXIST);
        }
        Model model = modelMapper.selectByPrimaryKey(modelId);
        double k1 = model.getK1();
        double k2 = model.getK2();
        double a1 = model.getA1();
        double a2 = model.getA2();
        int rangeT = 2000;
        double[][] result = disPreChuanpid(kp,ki,kd,k1,k2,a1,a2,T,rangeT,R,kp1,ki1,signal,disturbvalue,disturbvalue1);
        ModelChuanPid modelChuanPid = new ModelChuanPid();
        modelChuanPid.setKp(kp);
        modelChuanPid.setKi(ki);
        modelChuanPid.setKd(kd);
        modelChuanPid.setKp1(kp1);
        modelChuanPid.setKi1(ki1);
        modelChuanPid.setT(T);
        modelChuanPid.setR(R);
        modelChuanPid.setModelId(modelId);
        modelChuanPidMapper.insert(modelChuanPid);
        ReturnResult returnResult = new ReturnResult();
        returnResult.setPidId(modelChuanPid.getChuanPidId());
        returnResult.setResult(result);
        return returnResult;
    }

    @Override
    public ReturnResult oneLatePID(Double kp, Double ki, Double kd,Double kp0, Double ki0, Double kd0, String modelId, Double T, Double R, Double tao) throws BusinessException
    {
        if(kp==null||ki==null||kd==null||kp0==null || ki0==null ||kd0==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        if(modelId==null){
            throw new BusinessException(EmBusinessError.MODELID_NOT_EXIST);
        }
        ModelOne modelOne = modelOneMapper.selectByPrimaryKey(modelId);
        double k1 = modelOne.getK1();
        double a1 = modelOne.getA1();
        int rangeT = 5000;
        double[][] result = oneLatePid(kp,ki,kd,kp0,ki0,kd0,k1,a1,T,rangeT,R,tao);
        ModelChuanPid modelChuanPid = new ModelChuanPid();
        ModelPid modelPid = new ModelPid();
        modelPid.setKp(kp);
        modelPid.setKi(ki);
        modelPid.setKd(kd);
        modelPid.setT(T);
        modelPid.setR(R);
        modelPid.setModelId(modelId);
        modelPidMapper.insert(modelPid);
        ReturnResult returnResult = new ReturnResult();
        returnResult.setPidId(modelPid.getPidId());
        returnResult.setResult(result);
        return returnResult;
    }

    @Override
    public ReturnResult twoPrePID(Double kp1, Double ki1, Double kd1, Double kp10, Double ki10, Double kd10, Double kp0, Double ki0, Double kd0,
                                  String modelId, Double T, Double R, Double Kn,Double Tn) throws BusinessException
    {
        if(kp1==null||ki1==null||kd1==null||kp0==null || ki0==null ||kd0==null || kp10==null||ki0==null||kd0==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        if(modelId==null){
            throw new BusinessException(EmBusinessError.MODELID_NOT_EXIST);
        }
        ModelOne modelOne = modelOneMapper.selectByPrimaryKey(modelId);
        double k1 = modelOne.getK1();
        double a1 = modelOne.getA1();
        int rangeT = 5000;
        double[][] result = twoPrePid(kp1,ki1,kd1,kp10,ki10,kd10,kp0,ki0,kd0,k1,a1,T,rangeT,R,Kn,Tn);
        ModelChuanPid modelChuanPid = new ModelChuanPid();
        ModelPid modelPid = new ModelPid();
        modelPid.setKp(kp0);
        modelPid.setKi(ki0);
        modelPid.setKd(kd0);
        modelPid.setT(T);
        modelPid.setR(R);
        modelPid.setModelId(modelId);
        modelPidMapper.insert(modelPid);
        ReturnResult returnResult = new ReturnResult();
        returnResult.setPidId(modelPid.getPidId());
        returnResult.setResult(result);
        return returnResult;
    }




    private double[][] oneLatePid(Double kp, Double ki, Double kd, Double kp0, Double ki0, Double kd0, double k1, double a1, Double T, int rangeT, Double R,Double tao) throws BusinessException
    {
        double[][] result = new double[2][rangeT];//返回的结果
        double[] y = new double[rangeT];//液位1不加smith的输出
        double[] y1 = new double[rangeT];//液位2加smith的输出
        double[] e1 = new double[rangeT];//加Smith预估控制时的偏差
        double[] e3 = new double[rangeT];//不加Smith预估控制的偏差
        double[] e2 = new double[rangeT];
        double[] u = new double[rangeT];
        double[] u1 = new double[rangeT];
        double[] p = new double[rangeT];
        double[] i = new double[rangeT];
        double[] d = new double[rangeT];
        double N = tao/T;   //滞后的周期数
        double A = a1/(a1 + T);
        double B = k1*T/(a1 + T);
        double[] m = new double[rangeT];
        double[] yt = new double[rangeT];
        double y_1 = 0;
        double y1_1 = 0;
        double yt_1 = 0;
        double u_1 = 0,u_n_1 = 0,u1_1 = 0,u_n = 0,u1_n = 0,u1_n_1 = 0;
        double e2_1 = 0,e2_2 = 0,e3_1 = 0,e3_2 = 0,e1_2 = 0,e1_1 = 0,m_1 = 0;

        for (int k = 0; k < rangeT; k++)
        {
            e1[k] = R-y_1;
            e3[k] = R-y1_1;
            m[k] = A*m_1+B*u_1;
            yt[k] = A*yt_1+B*(u_1-u_n_1);
            e2[k] = e1[k]-yt[k];
            u[k] = u_1+kp*(e2[k]-e2_1) + ki*e2[k] + kd*(e2_2-2*e2_1+e2[k]);
            u1[k] = u1_1 + kp0*(e3[k]-e3_1)+ki0*e3[k] + kd0*(e3_2-2*e3_1+e3[k]);
            y[k] = A*y_1+B*u_n;
            y1[k] = A*y1_1+B*u1_n;
            e1_2 = e1_1;
            e1_1 = e1[k];
            e2_2 = e2_1;
            e2_1 = e2[k];
            e3_2 = e3_1;
            e3_1 = e3[k];
            u_1 = u[k];
            u1_1 = u1[k];
            y_1 = y[k];
            yt_1 = yt[k];
            y1_1 = y1[k];
            if (k>N+1){
                u_n_1 = u_n;
                u_n = u[(int) (k-N)];
                u1_n_1 = u1_n;
                u1_n=u1[(int) (k-N)];
            }
        }
        try
        {
            saveTwo(y);
            saveTwo(y1);
        } catch (Exception e)
        {
            throw new BusinessException(EmBusinessError.MODEL_PARAMS_EX);
        }
        result[0] = y;
        result[1] = y1;
        return result;
    }

    private double[][] twoPrePid(Double kp1, Double ki1, Double kd1, Double kp10, Double ki10, Double kd10,
                                 Double kp0, Double ki0, Double kd0, double k1, double a1, Double T, int rangeT, Double R, Double Kn, Double Tn) throws BusinessException
    {
        double[][] result = new double[3][rangeT];//返回的结果

        double[] e1 = new double[rangeT];
        double[] e3 = new double[rangeT];
        double[] e10 = new double[rangeT];
        double[] u1 = new double[rangeT];
        double[] u2 = new double[rangeT];
        double[] u10 = new double[rangeT];
        double[] un = new double[rangeT];
        double[] u = new double[rangeT];
        double[] y1 = new double[rangeT];//液位1
        double[] y10 = new double[rangeT];//液位2
        double[] y2 = new double[rangeT];//液位1
        double[] yn = new double[rangeT];//液位2
        double[] y = new double[rangeT];//液位1
        double[] y3 = new double[rangeT];//液位2

        double A1 = a1/(a1 + T),B1 = k1*T/(a1+T);
        double A10 = a1/(a1 + T),B10 = k1*T/(a1+T);
        double A2 = a1/(Tn+T),B2 = Kn*T/(Tn+T);
        double An= Tn/(T+Tn),Bn=-Kn*(T+a1)/(k1*(T+Tn)),Cn=Kn*a1/(k1*(T+Tn));
        double e1_1=0,e1_2=0,e10_1=0,e10_2=0,e3_1=0,e3_2=0;
        double n2_1=0;
        double u_1=0,u_n=0,u_n_1=0,u1_1=0,u1_n=0,u1_n_1=0;
        double u10_1=0;
        double u2_1=0,u2_n=0,u2_n_1=0;
        double un_1=0;
        double m_1=0;
        double y_1=0;
        double y1_1=0,y10_1=0,y2_1=0,yn_1=0;
        double yt_1=0,y3_1=0;
        double n2 = 10;

        for (int k = 0; k < rangeT; k++)
        {
            e1[k] = R-y_1;
            e3[k] = R-y3_1;
            e10[k] = R-y10_1;
            u1[k] = u1_1+kp1*(e1[k]-e1_1)+ki1*e1[k]+kd1*(e1_2-2*e1_1+e1[k]);
            u2[k] = u2_1+kp0*(e3[k]-e3_1)+ki0*e3[k]+kd0*(e3_2-2*e3_1+e3[k]);
            u10[k] = u10_1+kp10*(e10[k]-e10_1)+ki10*e10[k]+kd10*(e10_2-2*e10_1+e10[k]);
            un[k] = An*un_1+Bn*n2+Cn*n2_1;
            u[k] = u1[k]+un[k];
            y1[k] = A1*y1_1+B1*u[k];
            y10[k] = A10*y10_1+B10*u10[k];
            y2[k] = A1*y2_1+B1*u2[k];
            yn[k] = A2*yn_1+B2*n2;
            y[k] = y1[k]+yn[k];
            y3[k] = y2[k]+yn[k];

            e1_2=e1_1;
            e1_1=e1[k];
            e10_2=e10_1;
            e10_1=e10[k];
            e3_2=e3_1;
            e3_1=e3[k];
            u1_1=u1[k];
            u10_1=u10[k];
            u2_1=u2[k];
            un_1=un[k];
            n2_1=n2;
            y_1=y[k];
            y1_1=y1[k];
            y10_1=y10[k];
            y3_1=y3[k];
        }

        try
        {
            saveTwo(y10);
            saveTwo(y3);
            saveTwo(y);
        } catch (Exception e)
        {
            throw new BusinessException(EmBusinessError.MODEL_PARAMS_EX);
        }
        result[0] = y10;
        result[1] = y3;
        result[2] = y;
        return result;
    }


    public double[][] easypid(double kp, double ki, double kd, double k1, double a1, double T,
                               int rangeT,double R) throws BusinessException {
        double[][] result = new double[6][rangeT];//返回的结果
        double[] y0 = new double[rangeT];//液位1
        double[] y1 = new double[rangeT];//液位2
        double[] e1 = new double[rangeT];
        double[] u1 = new double[rangeT];
        double[] p = new double[rangeT];
        double[] i = new double[rangeT];
        double[] d = new double[rangeT];
        e1[0] = R;
        u1[0] = kp * (e1[0]) + ki * e1[0] + kd * (e1[0]);
        p[0] = kp * (e1[0]);
        i[0] = ki * e1[0];
        d[0] = kd * (e1[0]);
        y0[0] = (k1 * T * T * u1[0]) / (1 + a1 * T);

        e1[1] = R - y0[0];
        u1[1] = u1[0] + kp * (e1[1] - e1[0]) + ki * e1[1] + kd * (e1[1] - 2 * e1[0]);
        p[1] = kp * (e1[1] - e1[0]);
        i[1] = ki * e1[1];
        d[1] = kd * (e1[1] - 2 * e1[0]);
        y0[1] = ((2 + a1 * T) * y0[0] + k1 * T * T * (u1[1] - u1[0])) / (1 + a1 * T);

        for (int k = 2; k < rangeT; k++)
        {
            e1[k] = R - y0[k - 1];
            u1[k] = (u1[k - 1] + kp * (e1[k] - e1[k - 1]) + ki * e1[k] + kd * (e1[k] - 2 * e1[k - 1] + e1[k - 2]));
            p[k] = (kp * (e1[k] - e1[k - 1]));
            i[k] = ki * e1[k];
            d[k] = kd * (e1[k] - 2 * e1[k - 1] + e1[k - 2]);

            y0[k] = (((2 + a1 * T) * y0[k - 1] - y0[k - 2] + k1 * T * T * (u1[k] - u1[k - 1])) / (1 + a1 * T)) ;
            /*if (y0[k] >= 1000){
                y0[k] = 1000;
            }else if (y0[k] <= -1000){
                y0[k] = -1000;
            }*/
        }
        try
        {
            saveTwo(e1);
            saveTwo(u1);
            saveTwo(y0);
            saveTwo(p);
            saveTwo(i);
            saveTwo(d);
        } catch (Exception e)
        {
            throw new BusinessException(EmBusinessError.MODEL_PARAMS_EX);
        }
        result[0] = y0;
        result[1] = e1;
        result[2] = u1;

        result[3] = p;
        result[4] = i;
        result[5] = d;
        return result;
    }

    public double[][] integralSeparationPID(double kp, double ki, double kd, double k1, double a1, double T,
                              int rangeT,double R,double yu) throws BusinessException {
        double[][] result = new double[2][rangeT];//返回的结果
        double[] y0 = new double[rangeT];//液位1
        double[] y1 = new double[rangeT];//液位2
        double[] e1 = new double[rangeT];
        double[] u1 = new double[rangeT];
        e1[0] = R;
        if(Math.abs(e1[0])<=yu){
            u1[0] = kp * (e1[0]) + ki * e1[0] + kd * (e1[0]);
        }else {
            u1[0] = kp * (e1[0]) + kd * (e1[0]);
        }
        y0[0] = (k1 * T * T * u1[0]) / (1 + a1 * T);

        e1[1] = R - y0[0];
        if(Math.abs(e1[1])<=yu){
            u1[1] = u1[0] + kp * (e1[1] - e1[0]) + ki * e1[1] + kd * (e1[1] - 2 * e1[0]);
        }else {
            u1[1] = u1[0] + kp * (e1[1] - e1[0]) + kd * (e1[1] - 2 * e1[0]);
        }
        y0[1] = ((2 + a1 * T) * y0[0] + k1 * T * T * (u1[1] - u1[0])) / (1 + a1 * T);

        for (int k = 2; k < rangeT; k++)
        {
            e1[k] = R - y0[k - 1];
            if(Math.abs(e1[k])<=yu){
                u1[k] = u1[k - 1] + kp * (e1[k] - e1[k - 1]) + ki * e1[k] + kd * (e1[k] - 2 * e1[k - 1] + e1[k - 2]);
            }else {
                u1[k] = u1[k - 1] + kp * (e1[k] - e1[k - 1]) + kd * (e1[k] - 2 * e1[k - 1] + e1[k - 2]);
            }
            y0[k] = ((2 + a1 * T) * y0[k - 1] - y0[k - 2] + k1 * T * T * (u1[k] - u1[k - 1])) / (1 + a1 * T);
        }
        try
        {
            saveTwo(y1);
            saveTwo(y0);
        } catch (Exception e)
        {
            throw new BusinessException(EmBusinessError.MODEL_PARAMS_EX);
        }
        result[0] = y0;
        result[1] = y1;
        return result;
    }

    private double[][] oneCascade(Double kp, Double ki, Double kd, double k1, double a1, Double T, int rangeT,
                                  Double R,Double kp1, Double ki1, Integer signal, String disturbvalue) throws BusinessException{
        double signalValue = 0;
        double A = 0.0d;
        double W = 0.0d;
        double fai = 0.0d;
        double[][] result = new double[2][rangeT];//返回的结果
        double[] y0 = new double[rangeT];//液位1
        double[] y1 = new double[rangeT];//液位2
        double[] e1 = new double[rangeT];
        double[] e2 = new double[rangeT];
        double[] u1 = new double[rangeT];
        double[] u = new double[rangeT];
        double[] u2 = new double[rangeT];
        double[] d = new double[rangeT];//扰动
        //阶跃扰动
        if (signal == 1){
            signalValue = Double.valueOf(disturbvalue);
            e1[0]=R;
            u1[0]=kp*(e1[0])+ki*e1[0]+kd*(e1[0]);
            e2[0]=u1[0];
            u[0]=0+kp1*(e2[0]-0)+ki1*e2[0];
            u2[0]=u[0]+signalValue;
            y0[0]=(k1*T*T*u2[0])/(1+a1*T);

            e1[1]=R-y0[0];
            u1[1]=u1[0]+kp*(e1[1]-e1[0])+ki*e1[1]+kd*(e1[1]-2*e1[0]);
            e2[1]=u1[1]-u2[0];
            u[1]=u[0]+kp1*(e2[1]-e2[0])+ki1*e2[1];
            u2[1]=u[1]+signalValue;
            y0[1]=((2+a1*T)*y0[0]+k1*T*T*(u2[1]-u2[0]))/(1+a1*T);

            for (int k = 2; k < rangeT; k++) {
                e1[k]=R-y0[k-1];
                u1[k]=u1[k-1]+kp*(e1[k]-e1[k-1])+ki*e1[k]+kd*(e1[k]-2*e1[k-1]+e1[k-2]);
                e2[k]=u1[k]-u2[k-1];
                u[k]=u[k-1]+kp1*(e2[k]-e2[k-1])+ki1*e2[k];
                u2[k]=u[k]+signalValue;
                y0[k]=((2+a1*T)*y0[k-1]-y0[k-2]+k1*T*T*(u2[k]-u2[k-1]))/(1+(a1)*T);
            }
        }else if(signal == 2){
            A=Double.valueOf(disturbvalue.split("-")[0]);
            W=Double.valueOf(disturbvalue.split("-")[1]);
            fai=Double.valueOf(disturbvalue.split("-")[2]);
            double pi = Math.PI;
            for (int i = 1; i <= 100; i++) {
                for (int j = 0; j < 20; j++) {
                    d[(i-1)*20+j]=A*Math.sin(W*pi/20*j-fai*pi);
                }
            }
            e1[0]=R;
            u1[0]=kp*(e1[0])+ki*e1[0]+kd*(e1[0]);
            e2[0]=u1[0];
            u[0]=0+kp1*(e2[0]-0)+ki1*e2[0];
            u2[0]=u[0]+d[0];
            y0[0]=(k1*T*T*u2[0])/(1+a1*T);

            e1[1]=R-y0[0];
            u1[1]=u1[0]+kp*(e1[1]-e1[0])+ki*e1[1]+kd*(e1[1]-2*e1[0]);
            e2[1]=u1[1]-u2[0];
            u[1]=u[0]+kp1*(e2[1]-e2[0])+ki1*e2[1];
            u2[1]=u[1]+d[1];
            y0[1]=((2+a1*T)*y0[0]+k1*T*T*(u2[1]-u2[0]))/(1+a1*T);

            for (int k = 2; k < rangeT; k++) {
                e1[k]=R-y0[k-1];
                u1[k]=u1[k-1]+kp*(e1[k]-e1[k-1])+ki*e1[k]+kd*(e1[k]-2*e1[k-1]+e1[k-2]);
                e2[k]=u1[k]-u2[k-1];
                u[k]=u[k-1]+kp1*(e2[k]-e2[k-1])+ki1*e2[k];
                u2[k]=u[k]+d[k];
                y0[k]=((2+a1*T)*y0[k-1]-y0[k-2]+k1*T*T*(u2[k]-u2[k-1]))/(1+(a1)*T);
            }
        }else if(signal == 3){
            signalValue = Integer.valueOf(disturbvalue);
            e1[0]=R;
            u1[0]=kp*(e1[0])+ki*e1[0]+kd*(e1[0]);
            e2[0]=u1[0];
            u[0]=0+kp1*(e2[0]-0)+ki1*e2[0];
            u2[0]=u[0]+generateNoise(signalValue);
            y0[0]=(k1*T*T*u2[0])/(1+a1*T);

            e1[1]=R-y0[0];
            u1[1]=u1[0]+kp*(e1[1]-e1[0])+ki*e1[1]+kd*(e1[1]-2*e1[0]);
            e2[1]=u1[1]-u2[0];
            u[1]=u[0]+kp1*(e2[1]-e2[0])+ki1*e2[1];
            u2[1]=u[1]+generateNoise(signalValue);
            y0[1]=((2+a1*T)*y0[0]+k1*T*T*(u2[1]-u2[0]))/(1+a1*T);

            for (int k = 2; k < rangeT; k++) {
                e1[k]=R-y0[k-1];
                u1[k]=u1[k-1]+kp*(e1[k]-e1[k-1])+ki*e1[k]+kd*(e1[k]-2*e1[k-1]+e1[k-2]);
                e2[k]=u1[k]-u2[k-1];
                u[k]=u[k-1]+kp1*(e2[k]-e2[k-1])+ki1*e2[k];
                u2[k]=u[k]+generateNoise(signalValue);
                y0[k]=((2+a1*T)*y0[k-1]-y0[k-2]+k1*T*T*(u2[k]-u2[k-1]))/(1+(a1)*T);
            }
        }

        /*if (signal == 1){
            signalValue = Double.valueOf(disturbvalue);
            e1[0]=R;
            u1[0]=kp*(e1[0])+ki*e1[0]+kd*(e1[0]);
            e2[0]=u1[0];
            u[0]=0+kp1*(e2[0]-0)+ki1*e2[0];
            u2[0]=u[0];
            y0[0]=(k1*T*T*u2[0])/(1+a1*T);

            e1[1]=R-y0[0];
            u1[1]=u1[0]+kp*(e1[1]-e1[0])+ki*e1[1]+kd*(e1[1]-2*e1[0]);
            e2[1]=u1[1]-u2[0];
            u[1]=u[0]+kp1*(e2[1]-e2[0])+ki1*e2[1];
            u2[1]=u[1];
            y0[1]=((2+a1*T)*y0[0]+k1*T*T*(u2[1]-u2[0]))/(1+a1*T);

            for (int k = 2; k < 200; k++) {
                e1[k]=R-y0[k-1];
                u1[k]=u1[k-1]+kp*(e1[k]-e1[k-1])+ki*e1[k]+kd*(e1[k]-2*e1[k-1]+e1[k-2]);
                e2[k]=u1[k]-u2[k-1];
                u[k]=u[k-1]+kp1*(e2[k]-e2[k-1])+ki1*e2[k];
                u2[k]=u[k];
                y0[k]=((2+a1*T)*y0[k-1]-y0[k-2]+k1*T*T*(u2[k]-u2[k-1]))/(1+(a1)*T);
            }
            for (int k = 200; k < rangeT; k++) {
                e1[k]=R-y0[k-1];
                u1[k]=u1[k-1]+kp*(e1[k]-e1[k-1])+ki*e1[k]+kd*(e1[k]-2*e1[k-1]+e1[k-2]);
                e2[k]=u1[k]-u2[k-1];
                u[k]=u[k-1]+kp1*(e2[k]-e2[k-1])+ki1*e2[k];
                u2[k]=u[k]+signalValue;
                y0[k]=((2+a1*T)*y0[k-1]-y0[k-2]+k1*T*T*(u2[k]-u2[k-1]))/(1+(a1)*T);
            }
        }else if(signal == 2){
            A=Double.valueOf(disturbvalue.split("-")[0]);
            W=Double.valueOf(disturbvalue.split("-")[1]);
            fai=Double.valueOf(disturbvalue.split("-")[2]);
            double pi = Math.PI;
            for (int i = 1; i <= 100; i++) {
                for (int j = 0; j < 20; j++) {
                    d[(i-1)*20+j]=A*Math.sin(W*pi/20*j-fai*pi);
                }
            }
            e1[0]=R;
            u1[0]=kp*(e1[0])+ki*e1[0]+kd*(e1[0]);
            e2[0]=u1[0];
            u[0]=0+kp1*(e2[0]-0)+ki1*e2[0];
            u2[0]=u[0];
            y0[0]=(k1*T*T*u2[0])/(1+a1*T);

            e1[1]=R-y0[0];
            u1[1]=u1[0]+kp*(e1[1]-e1[0])+ki*e1[1]+kd*(e1[1]-2*e1[0]);
            e2[1]=u1[1]-u2[0];
            u[1]=u[0]+kp1*(e2[1]-e2[0])+ki1*e2[1];
            u2[1]=u[1];
            y0[1]=((2+a1*T)*y0[0]+k1*T*T*(u2[1]-u2[0]))/(1+a1*T);

            for (int k = 2; k < 200; k++) {
                e1[k]=R-y0[k-1];
                u1[k]=u1[k-1]+kp*(e1[k]-e1[k-1])+ki*e1[k]+kd*(e1[k]-2*e1[k-1]+e1[k-2]);
                e2[k]=u1[k]-u2[k-1];
                u[k]=u[k-1]+kp1*(e2[k]-e2[k-1])+ki1*e2[k];
                u2[k]=u[k];
                y0[k]=((2+a1*T)*y0[k-1]-y0[k-2]+k1*T*T*(u2[k]-u2[k-1]))/(1+(a1)*T);
            }
            for (int k = 200; k < rangeT; k++) {
                e1[k]=R-y0[k-1];
                u1[k]=u1[k-1]+kp*(e1[k]-e1[k-1])+ki*e1[k]+kd*(e1[k]-2*e1[k-1]+e1[k-2]);
                e2[k]=u1[k]-u2[k-1];
                u[k]=u[k-1]+kp1*(e2[k]-e2[k-1])+ki1*e2[k];
                u2[k]=u[k] + d[k];
                y0[k]=((2+a1*T)*y0[k-1]-y0[k-2]+k1*T*T*(u2[k]-u2[k-1]))/(1+(a1)*T);
            }
        }else if(signal == 3){
            signalValue = Integer.valueOf(disturbvalue);
            e1[0]=R;
            u1[0]=kp*(e1[0])+ki*e1[0]+kd*(e1[0]);
            e2[0]=u1[0];
            u[0]=0+kp1*(e2[0]-0)+ki1*e2[0];
            u2[0]=u[0];
            y0[0]=(k1*T*T*u2[0])/(1+a1*T);

            e1[1]=R-y0[0];
            u1[1]=u1[0]+kp*(e1[1]-e1[0])+ki*e1[1]+kd*(e1[1]-2*e1[0]);
            e2[1]=u1[1]-u2[0];
            u[1]=u[0]+kp1*(e2[1]-e2[0])+ki1*e2[1];
            u2[1]=u[1];
            y0[1]=((2+a1*T)*y0[0]+k1*T*T*(u2[1]-u2[0]))/(1+a1*T);

            for (int k = 2; k < 200; k++) {
                e1[k]=R-y0[k-1];
                u1[k]=u1[k-1]+kp*(e1[k]-e1[k-1])+ki*e1[k]+kd*(e1[k]-2*e1[k-1]+e1[k-2]);
                e2[k]=u1[k]-u2[k-1];
                u[k]=u[k-1]+kp1*(e2[k]-e2[k-1])+ki1*e2[k];
                u2[k]=u[k];
                y0[k]=((2+a1*T)*y0[k-1]-y0[k-2]+k1*T*T*(u2[k]-u2[k-1]))/(1+(a1)*T);
            }
            for (int k = 200; k < rangeT; k++) {
                e1[k]=R-y0[k-1];
                u1[k]=u1[k-1]+kp*(e1[k]-e1[k-1])+ki*e1[k]+kd*(e1[k]-2*e1[k-1]+e1[k-2]);
                e2[k]=u1[k]-u2[k-1];
                u[k]=u[k-1]+kp1*(e2[k]-e2[k-1])+ki1*e2[k];
                u2[k]=u[k]+generateNoise(signalValue);
                y0[k]=((2+a1*T)*y0[k-1]-y0[k-2]+k1*T*T*(u2[k]-u2[k-1]))/(1+(a1)*T);
            }
        }*/
        try {
            saveTwo(y0);
            saveTwo(y1);
        }catch (Exception e){
            throw new BusinessException(EmBusinessError.MODEL_PARAMS_EX);
        }
        result[0] = y0;
        result[1] = y1;
        return result;
    }


    private double[][] disChuanpid(Double kp, Double ki, Double kd, double k1, double k2, double a1, double a2,
                                   Double T, int rangeT, Double R,Double kp1, Double ki1, Integer signal, String disturbvalue) throws BusinessException{
        double signalValue = 0;
        double A = 0.0d;
        double W = 0.0d;
        double fai = 0.0d;
        double[][] result = new double[2][rangeT];//返回的结果
        double[] y0 = new double[rangeT];//液位1
        double[] y1 = new double[rangeT];//液位2
        double[] e1 = new double[rangeT];
        double[] e2 = new double[rangeT];
        double[] u1 = new double[rangeT];
        double[] u = new double[rangeT];
        double[] u2 = new double[rangeT];
        double mu = 1+(a1+a2)*T+a1*a2*T*T;
        double[] d = new double[rangeT];//扰动
        //阶跃扰动
        if (signal == 1){
            signalValue = Double.valueOf(disturbvalue);
            e1[0]=R;
            u1[0]=kp*(e1[0])+ki*e1[0]+kd*(e1[0]);
            e2[0]=u1[0];
            u[0]=0+kp1*(e2[0]-0)+ki1*e2[0];
            u2[0]=u[0]+signalValue;
            y0[0]=(k1*T*T*u2[0])/(1+a1*T);
            y1[0]=(k1*k2*T*T*T*u2[0])/mu;

            e1[1]=R-y1[0];
            u1[1]=u1[0]+kp*(e1[1]-e1[0])+ki*e1[1]+kd*(e1[1]-2*e1[0]);
            e2[1]=u1[1]-y0[0];
            u[1]=u[0]+kp1*(e2[1]-e2[0])+ki1*e2[1];
            u2[1]=u[1]+signalValue;
            y0[1]=((2+a1*T)*y0[0]+k1*T*T*(u2[1]-u2[0]))/(1+a1*T);
            y1[1]=(k1*k2*T*T*T*(u2[1]-u2[0])+(3+2*(a1+a2)*T+a1*a2*T*T)*y1[0])/mu;

            e1[2]=R-y1[1];
            u1[2]=u1[1]+kp*(e1[2]-e1[1])+ki*e1[2]+kd*(e1[2]-2*e1[1]+e1[0]);
            e2[2]=u1[2]-y0[1];
            u[2]=u[1]+kp1*(e2[2]-e2[1])+ki1*e2[2];
            u2[2]=u[2]+signalValue;
            y0[2]=((2+a1*T)*y0[1]-y0[0]+k1*T*T*(u2[2]-u2[1]))/(1+a1*T);
            y1[2]=(k1*k2*T*T*T*(u2[2]-u2[1])+(3+2*(a1+a2)*T+a1*a2*T*T)*y1[1]+(-3-(a1+a2)*T)*y1[0])/mu;

            for (int k = 3; k < rangeT; k++) {
                e1[k]=R-y1[k-1];
                u1[k]=u1[k-1]+kp*(e1[k]-e1[k-1])+ki*e1[k]+kd*(e1[k]-2*e1[k-1]+e1[k-2]);
                e2[k]=u1[k]-y0[k-1];
                u[k]=u[k-1]+kp1*(e2[k]-e2[k-1])+ki1*e2[k];
                u2[k]=u[k]+signalValue;
                y0[k]=((2+a1*T)*y0[k-1]-y0[k-2]+k1*T*T*(u2[k]-u2[k-1]))/(1+(a1)*T);
                y1[k]=(k1*k2*T*T*T*(u2[k]-u2[k-1])+(3+2*(a1+a2)*T+a1*a2*T*T)*y1[k-1]+(-3-(a1+a2)*T)*y1[k-2]+y1[k-3])/mu;
            }
        }else if(signal == 2){
            A=Double.valueOf(disturbvalue.split("-")[0]);
            W=Double.valueOf(disturbvalue.split("-")[1]);
            fai=Double.valueOf(disturbvalue.split("-")[2]);
            double pi = Math.PI;
            for (int i = 1; i <= 100; i++) {
                for (int j = 0; j < 20; j++) {
                    d[(i-1)*20+j]=A*Math.sin(W*pi/20*j-fai*pi);
                }
            }
            e1[0]=R;
            u1[0]=kp*(e1[0])+ki*e1[0]+kd*(e1[0]);
            e2[0]=u1[0];
            u[0]=0+kp1*(e2[0]-0)+ki1*e2[0];
            u2[0]=u[0]+d[0];
            y0[0]=(k1*T*T*u2[0])/(1+a1*T);
            y1[0]=(k1*k2*T*T*T*u2[0])/mu;

            e1[1]=R-y1[0];
            u1[1]=u1[0]+kp*(e1[1]-e1[0])+ki*e1[1]+kd*(e1[1]-2*e1[0]);
            e2[1]=u1[1]-y0[0];
            u[1]=u[0]+kp1*(e2[1]-e2[0])+ki1*e2[1];
            u2[1]=u[1]+d[1];
            y0[1]=((2+a1*T)*y0[0]+k1*T*T*(u2[1]-u2[0]))/(1+a1*T);
            y1[1]=(k1*k2*T*T*T*(u2[1]-u2[0])+(3+2*(a1+a2)*T+a1*a2*T*T)*y1[0])/mu;

            e1[2]=R-y1[1];
            u1[2]=u1[1]+kp*(e1[2]-e1[1])+ki*e1[2]+kd*(e1[2]-2*e1[1]+e1[0]);
            e2[2]=u1[2]-y0[1];
            u[2]=u[1]+kp1*(e2[2]-e2[1])+ki1*e2[2];
            u2[2]=u[2]+d[2];
            y0[2]=((2+a1*T)*y0[1]-y0[0]+k1*T*T*(u2[2]-u2[1]))/(1+a1*T);
            y1[2]=(k1*k2*T*T*T*(u2[2]-u2[1])+(3+2*(a1+a2)*T+a1*a2*T*T)*y1[1]+(-3-(a1+a2)*T)*y1[0])/mu;

            for (int k = 3; k < rangeT; k++) {
                e1[k]=R-y1[k-1];
                u1[k]=u1[k-1]+kp*(e1[k]-e1[k-1])+ki*e1[k]+kd*(e1[k]-2*e1[k-1]+e1[k-2]);
                e2[k]=u1[k]-y0[k-1];
                u[k]=u[k-1]+kp1*(e2[k]-e2[k-1])+ki1*e2[k];
                u2[k]=u[k]+d[k];
                y0[k]=((2+a1*T)*y0[k-1]-y0[k-2]+k1*T*T*(u2[k]-u2[k-1]))/(1+(a1)*T);
                y1[k]=(k1*k2*T*T*T*(u2[k]-u2[k-1])+(3+2*(a1+a2)*T+a1*a2*T*T)*y1[k-1]+(-3-(a1+a2)*T)*y1[k-2]+y1[k-3])/mu;
            }
        }else if(signal == 3){
            signalValue = Integer.valueOf(disturbvalue);
            e1[0]=R;
            u1[0]=kp*(e1[0])+ki*e1[0]+kd*(e1[0]);
            e2[0]=u1[0];
            u[0]=0+kp1*(e2[0]-0)+ki1*e2[0];
            u2[0]=u[0]+generateNoise(signalValue);
            y0[0]=(k1*T*T*u2[0])/(1+a1*T);
            y1[0]=(k1*k2*T*T*T*u2[0])/mu;

            e1[1]=R-y1[0];
            u1[1]=u1[0]+kp*(e1[1]-e1[0])+ki*e1[1]+kd*(e1[1]-2*e1[0]);
            e2[1]=u1[1]-y0[0];
            u[1]=u[0]+kp1*(e2[1]-e2[0])+ki1*e2[1];
            u2[1]=u[1]+generateNoise(signalValue);
            y0[1]=((2+a1*T)*y0[0]+k1*T*T*(u2[1]-u2[0]))/(1+a1*T);
            y1[1]=(k1*k2*T*T*T*(u2[1]-u2[0])+(3+2*(a1+a2)*T+a1*a2*T*T)*y1[0])/mu;

            e1[2]=R-y1[1];
            u1[2]=u1[1]+kp*(e1[2]-e1[1])+ki*e1[2]+kd*(e1[2]-2*e1[1]+e1[0]);
            e2[2]=u1[2]-y0[1];
            u[2]=u[1]+kp1*(e2[2]-e2[1])+ki1*e2[2];
            u2[2]=u[2]+generateNoise(signalValue);
            y0[2]=((2+a1*T)*y0[1]-y0[0]+k1*T*T*(u2[2]-u2[1]))/(1+a1*T);
            y1[2]=(k1*k2*T*T*T*(u2[2]-u2[1])+(3+2*(a1+a2)*T+a1*a2*T*T)*y1[1]+(-3-(a1+a2)*T)*y1[0])/mu;

            for (int k = 3; k < rangeT; k++) {
                e1[k]=R-y1[k-1];
                u1[k]=u1[k-1]+kp*(e1[k]-e1[k-1])+ki*e1[k]+kd*(e1[k]-2*e1[k-1]+e1[k-2]);
                e2[k]=u1[k]-y0[k-1];
                u[k]=u[k-1]+kp1*(e2[k]-e2[k-1])+ki1*e2[k];
                u2[k]=u[k]+generateNoise(signalValue);
                y0[k]=((2+a1*T)*y0[k-1]-y0[k-2]+k1*T*T*(u2[k]-u2[k-1]))/(1+(a1)*T);
                y1[k]=(k1*k2*T*T*T*(u2[k]-u2[k-1])+(3+2*(a1+a2)*T+a1*a2*T*T)*y1[k-1]+(-3-(a1+a2)*T)*y1[k-2]+y1[k-3])/mu;
            }
        }
        try {
            saveTwo(y1);
            saveTwo(y0);
        }catch (Exception e){
            throw new BusinessException(EmBusinessError.MODEL_PARAMS_EX);
        }
        result[0] = y0;
        result[1] = y1;
        return result;

    }

    private double[][] disPreChuanpid(Double kp, Double ki, Double kd, double k1, double k2, double a1, double a2,
                                   Double T, int rangeT, Double R,Double kp1, Double ki1, Integer signal, String disturbvalue,String disturbvalue1) throws BusinessException{
        double signalValue = 0;
        double signalValue1 = 0;
        int kd1 = 0;
        double A = 0.0d;
        double W = 0.0d;
        double fai = 0.0d;
        int kn = 1;
        int tn = 5;
        double[][] result = new double[2][rangeT];//返回的结果
        double[] y1 = new double[rangeT];//液位1
        double[] y2 = new double[rangeT];//液位2
        double[] y3 = new double[rangeT];
        double[] y = new double[rangeT];
        double[] e1 = new double[rangeT];
        double[] e2 = new double[rangeT];
        double[] u1 = new double[rangeT];//u
        double[] u2 = new double[rangeT];//
        double[] u3 = new double[rangeT];//u2
        double[] un = new double[rangeT];//
        double[] u = new double[rangeT];//
        double mu = 1+(a1+a2)*T+a1*a2*T*T;
        double[] d = new double[rangeT];//扰动
        double[] d1 = new double[rangeT];//扰动1
        //阶跃扰动
        if (signal == 1){
            signalValue = Double.valueOf(disturbvalue);//三种
            signalValue1 = Double.valueOf(disturbvalue1);//只有阶跃

            e1[0] = R;
            u1[0] = kp*(e1[0]) + ki*e1[0] + kd*e1[0];
            e2[0] = u1[0] - y1[0];
            u2[0] = kp1*e2[0] + ki1*e2[0];//kd1为0，所以没加，后期需要可以添加上
            un[0] = -((kn*a1*a2+kn*(a1+a2)*T+T*T*kn)*signalValue1-(2*kn*a1*a2+kn*(a1+a2))*0+kn*a1*a2*0-k1*k2*tn*T*0)/(k1*k2*tn*T+T*T*k1*k2);//%前馈控制器
            u[0] = u2[0] + un[0];//%增加前馈
            u3[0] = u[0] + signalValue;//%增加干扰N1 ,如果是正弦或者白噪声，修改signalValue的值就可
            y1[0] = (a1*0+k1*T*u3[0])/(a1+T);
            y2[0] = (a2*0+k2*T*y1[0])/(a2+T);
            y3[0] = (tn*0+kn*T*signalValue1)/(tn+T);
            y[0] = y2[0] + y3[0];

            e1[1] = R - y[0];
            u1[1] = u1[0]+kp*(e1[1]-e1[0])+ki*e1[1]+kd*(0-2*e1[0]+e1[1]);//%外环PID
            e2[1] = u1[1]-y3[1];
            u2[1] = u2[0]+kp1*(e2[1]-e2[0])+ki1*e2[1]+kd1*(0-2*e2[0]+e2[1]);//%内环PID
            un[1] = -((kn*a1*a2+kn*(a1+a2)*T+T*T*kn)*signalValue1-(2*kn*a1*a2+kn*(a1+a2))*signalValue1+kn*a1*a2*0-k1*k2*tn*T*un[0])/(k1*k2*tn*T+T*T*k1*k2);//%前馈控制器
            u[1] = u2[1]+un[1];//%增加前馈
            u3[1] = u[1]+signalValue;//%增加干扰N1
            y1[1] = (a1*y1[0] + k1*T*u3[1])/(a1+T);
            y2[1] = (a2*y2[0] + k2*T*y1[1])/(a2+T);
            y3[1] = (tn*y3[0] + kn*T*signalValue1)/(tn+T);
            y[1] = y2[1]+y3[1];


            for (int k = 2; k < rangeT; k++) {
                e1[k] = R - y[k - 1];
                u1[k] = u1[k - 1] + kp*(e1[k] - e1[k - 1])+ki*e1[k] + kd*(e1[k - 2]-2*e1[k-1]+e1[k]);//%外环PID
                e2[k] = u1[k] - y1[k];
                u2[k] = u2[k-1]+kp1*(e2[k] - e2[k-1]) + ki1*e2[k] + kd1*(e2[k-2] - 2*e2[k-1]+e2[k]);//%内环PID
                un[k] = -((kn*a1*a2+kn*(a1+a2)*T+T*T*kn)*signalValue1-(2*kn*a1*a2+kn*(a1+a2))*signalValue1+kn*a1*a2*signalValue1-k1*k2*tn*T*un[k-1])/(k1*k2*tn*T+T*T*k1*k2);//%前馈控制器
                u[k] = u2[k] + un[k];//%增加前馈
                u3[k] = u[k] + signalValue;//%增加干扰N1
                y1[k] = (a1*y1[k-1] + k1*T*u3[k])/(a1+T);
                y2[k] = (a2*y2[k-1] + k2*T*y1[k])/(a2+T);
                y3[k] = (tn*y3[k-1] + kn*T*signalValue1)/(tn+T);
                y[k]=y2[k]+y3[k];
            }
        }
        else if(signal == 2){
            A=Double.valueOf(disturbvalue.split("-")[0]);
            W=Double.valueOf(disturbvalue.split("-")[1]);
            fai=Double.valueOf(disturbvalue.split("-")[2]);
            double pi = Math.PI;
            for (int i = 1; i <= 100; i++) {
                for (int j = 0; j < 20; j++) {
                    d[(i-1)*20+j]=A*Math.sin(W*pi/20*j-fai*pi);
                }
            }
            signalValue1 = Double.valueOf(disturbvalue1);//只有阶跃

            e1[0] = R;
            u1[0] = kp*(e1[0]) + ki*e1[0] + kd*e1[0];
            e2[0] = u1[0] - y1[0];
            u2[0] = kp1*e2[0] + ki1*e2[0];//kd1为0，所以没加，后期需要可以添加上
            un[0] = -((kn*a1*a2+kn*(a1+a2)*T+T*T*kn)*signalValue1-(2*kn*a1*a2+kn*(a1+a2))*0+kn*a1*a2*0-k1*k2*tn*T*0)/(k1*k2*tn*T+T*T*k1*k2);//%前馈控制器
            u[0] = u2[0] + un[0];//%增加前馈
            u3[0] = u[0] + d[0];//%增加干扰N1 ,如果是正弦或者白噪声，修改signalValue的值就可
            y1[0] = (a1*0+k1*T*u3[0])/(a1+T);
            y2[0] = (a2*0+k2*T*y1[0])/(a2+T);
            y3[0] = (tn*0+kn*T*signalValue1)/(tn+T);
            y[0] = y2[0] + y3[0];

            e1[1] = R - y[0];
            u1[1] = u1[0]+kp*(e1[1]-e1[0])+ki*e1[1]+kd*(0-2*e1[0]+e1[1]);//%外环PID
            e2[1] = u1[1]-y3[1];
            u2[1] = u2[0]+kp1*(e2[1]-e2[0])+ki1*e2[1]+kd1*(0-2*e2[0]+e2[1]);//%内环PID
            un[1] = -((kn*a1*a2+kn*(a1+a2)*T+T*T*kn)*signalValue1-(2*kn*a1*a2+kn*(a1+a2))*signalValue1+kn*a1*a2*0-k1*k2*tn*T*un[0])/(k1*k2*tn*T+T*T*k1*k2);//%前馈控制器
            u[1] = u2[1]+un[1];//%增加前馈
            u3[1] = u[1]+d[1];//%增加干扰N1
            y1[1] = (a1*y1[0] + k1*T*u3[1])/(a1+T);
            y2[1] = (a2*y2[0] + k2*T*y1[1])/(a2+T);
            y3[1] = (tn*y3[0] + kn*T*signalValue1)/(tn+T);
            y[1] = y2[1]+y3[1];


            for (int k = 2; k < rangeT; k++) {
                e1[k] = R - y[k - 1];
                u1[k] = u1[k - 1] + kp*(e1[k] - e1[k - 1])+ki*e1[k] + kd*(e1[k - 2]-2*e1[k-1]+e1[k]);//%外环PID
                e2[k] = u1[k] - y1[k];
                u2[k] = u2[k-1]+kp1*(e2[k] - e2[k-1]) + ki1*e2[k] + kd1*(e2[k-2] - 2*e2[k-1]+e2[k]);//%内环PID
                un[k] = -((kn*a1*a2+kn*(a1+a2)*T+T*T*kn)*signalValue1-(2*kn*a1*a2+kn*(a1+a2))*signalValue1+kn*a1*a2*signalValue1-k1*k2*tn*T*un[k-1])/(k1*k2*tn*T+T*T*k1*k2);//%前馈控制器
                u[k] = u2[k] + un[k];//%增加前馈
                u3[k] = u[k] + d[k];//%增加干扰N1
                y1[k] = (a1*y1[k-1] + k1*T*u3[k])/(a1+T);
                y2[k] = (a2*y2[k-1] + k2*T*y1[k])/(a2+T);
                y3[k] = (tn*y3[k-1] + kn*T*signalValue1)/(tn+T);
                y[k]=y2[k]+y3[k];
            }
        }
        else if(signal == 3){
            signalValue = Integer.valueOf(disturbvalue);
            signalValue1 = Double.valueOf(disturbvalue1);//只有阶跃

            e1[0] = R;
            u1[0] = kp*(e1[0]) + ki*e1[0] + kd*e1[0];
            e2[0] = u1[0] - y1[0];
            u2[0] = kp1*e2[0] + ki1*e2[0];//kd1为0，所以没加，后期需要可以添加上
            un[0] = -((kn*a1*a2+kn*(a1+a2)*T+T*T*kn)*signalValue1-(2*kn*a1*a2+kn*(a1+a2))*0+kn*a1*a2*0-k1*k2*tn*T*0)/(k1*k2*tn*T+T*T*k1*k2);//%前馈控制器
            u[0] = u2[0] + un[0];//%增加前馈
            u3[0] = u[0] + generateNoise(signalValue);//%增加干扰N1 ,如果是正弦或者白噪声，修改signalValue的值就可
            y1[0] = (a1*0+k1*T*u3[0])/(a1+T);
            y2[0] = (a2*0+k2*T*y1[0])/(a2+T);
            y3[0] = (tn*0+kn*T*signalValue1)/(tn+T);
            y[0] = y2[0] + y3[0];

            e1[1] = R - y[0];
            u1[1] = u1[0]+kp*(e1[1]-e1[0])+ki*e1[1]+kd*(0-2*e1[0]+e1[1]);//%外环PID
            e2[1] = u1[1]-y3[1];
            u2[1] = u2[0]+kp1*(e2[1]-e2[0])+ki1*e2[1]+kd1*(0-2*e2[0]+e2[1]);//%内环PID
            un[1] = -((kn*a1*a2+kn*(a1+a2)*T+T*T*kn)*signalValue1-(2*kn*a1*a2+kn*(a1+a2))*signalValue1+kn*a1*a2*0-k1*k2*tn*T*un[0])/(k1*k2*tn*T+T*T*k1*k2);//%前馈控制器
            u[1] = u2[1]+un[1];//%增加前馈
            u3[1] = u[1]+generateNoise(signalValue);//%增加干扰N1
            y1[1] = (a1*y1[0] + k1*T*u3[1])/(a1+T);
            y2[1] = (a2*y2[0] + k2*T*y1[1])/(a2+T);
            y3[1] = (tn*y3[0] + kn*T*signalValue1)/(tn+T);
            y[1] = y2[1]+y3[1];


            for (int k = 2; k < rangeT; k++) {
                e1[k] = R - y[k - 1];
                u1[k] = u1[k - 1] + kp*(e1[k] - e1[k - 1])+ki*e1[k] + kd*(e1[k - 2]-2*e1[k-1]+e1[k]);//%外环PID
                e2[k] = u1[k] - y1[k];
                u2[k] = u2[k-1]+kp1*(e2[k] - e2[k-1]) + ki1*e2[k] + kd1*(e2[k-2] - 2*e2[k-1]+e2[k]);//%内环PID
                un[k] = -((kn*a1*a2+kn*(a1+a2)*T+T*T*kn)*signalValue1-(2*kn*a1*a2+kn*(a1+a2))*signalValue1+kn*a1*a2*signalValue1-k1*k2*tn*T*un[k-1])/(k1*k2*tn*T+T*T*k1*k2);//%前馈控制器
                u[k] = u2[k] + un[k];//%增加前馈
                u3[k] = u[k] + generateNoise(signalValue);//%增加干扰N1
                y1[k] = (a1*y1[k-1] + k1*T*u3[k])/(a1+T);
                y2[k] = (a2*y2[k-1] + k2*T*y1[k])/(a2+T);
                y3[k] = (tn*y3[k-1] + kn*T*signalValue1)/(tn+T);
                y[k]=y2[k]+y3[k];
            }
        }
        try {
            saveTwo(y1);
            saveTwo(y);
        }catch (Exception e){
            throw new BusinessException(EmBusinessError.MODEL_PARAMS_EX);
        }
        result[0] = y1;
        result[1] = y;
        return result;

    }


    private double[][] dispid(Double kp, Double ki, Double kd, double k1, double k2, double a1, double a2,
                              Double T, int rangeT, Double R, Integer signal, String disturbvalue) throws BusinessException {
        double signalValue = 0;
        double A = 0.0d;
        double W = 0.0d;
        double fai = 0.0d;
        double[][] result = new double[2][rangeT];//返回的结果
        double[] y0 = new double[rangeT];//液位1
        double[] y1 = new double[rangeT];//液位2
        double[] e1 = new double[rangeT];
        double[] e2 = new double[rangeT];
        double[] u1 = new double[rangeT];
        double[] u2 = new double[rangeT];
        double[] d = new double[rangeT];
        double mu = 1 + (a1 + a2) * T + a1 * a2 * T * T;
        if (signal == 1)
        {
            signalValue = Double.valueOf(disturbvalue);
            e1[0] = R;
            u1[0] = kp * (e1[0]) + ki * e1[0] + kd * (e1[0]);
            e2[0] = u1[0];
            u2[0] = e2[0]+signalValue;
            y0[0] = (k1 * T * T * u2[0]) / (1 + a1 * T);
            y1[0] = (k1 * k2 * T * T * T * u2[0]) / mu;

            e1[1] = R - y1[0];
            u1[1] = u1[0] + kp * (e1[1] - e1[0]) + ki * e1[1] + kd * (e1[1] - 2 * e1[0]);
            e2[1] = u1[1] - y0[0];
            u2[1] = e2[1]+signalValue;
            y0[1] = ((2 + a1 * T) * y0[0] + k1 * T * T * (u2[1] - u2[0])) / (1 + a1 * T);
            y1[1] = (k1 * k2 * T * T * T * (u2[1] - u2[0]) + (3 + 2 * (a1 + a2) * T + a1 * a2 * T * T) * y1[0]) / mu;

            e1[2] = R - y1[1];
            u1[2] = u1[1] + kp * (e1[2] - e1[1]) + ki * e1[2] + kd * (e1[2] - 2 * e1[1] + e1[0]);
            e2[2] = u1[2] - y0[1];
            u2[2] = e2[2]+signalValue;
            y0[2] = ((2 + a1 * T) * y0[1] - y0[0] + k1 * T * T * (u2[2] - u2[1])) / (1 + a1 * T);
            y1[2] = (k1 * k2 * T * T * T * (u2[2] - u2[1]) + (3 + 2 * (a1 + a2) * T + a1 * a2 * T * T) * y1[1] + (-3 - (a1 + a2) * T) * y1[0]) / mu;

            for (int k = 3; k < rangeT; k++)
            {
                e1[k] = R - y1[k - 1];
                u1[k] = u1[k - 1] + kp * (e1[k] - e1[k - 1]) + ki * e1[k] + kd * (e1[k] - 2 * e1[k - 1] + e1[k - 2]);
                e2[k] = u1[k] - y0[k - 1];
                u2[k] = e2[k]+signalValue;
                y0[k] = ((2 + a1 * T) * y0[k - 1] - y0[k - 2] + k1 * T * T * (u2[k] - u2[k - 1])) / (1 + (a1) * T);
                y1[k] = (k1 * k2 * T * T * T * (u2[k] - u2[k - 1]) + (3 + 2 * (a1 + a2) * T + a1 * a2 * T * T) * y1[k - 1] + (-3 - (a1 + a2) * T) * y1[k - 2] + y1[k - 3]) / mu;
            }
        }else if (signal == 2)
        {
            A = Double.valueOf(disturbvalue.split("-")[0]);
            W = Double.valueOf(disturbvalue.split("-")[1]);
            fai = Double.valueOf(disturbvalue.split("-")[2]);
            double pi = Math.PI;
            for (int i = 1; i <= 100; i++)
            {
                for (int j = 0; j < 20; j++)
                {
                    d[(i - 1) * 20 + j] = A * Math.sin(W * pi / 20 * j - fai * pi);
                }
            }
            e1[0] = R;
            u1[0] = kp * (e1[0]) + ki * e1[0] + kd * (e1[0]);
            e2[0] = u1[0];
            u2[0] = e2[0]+d[0];
            y0[0] = (k1 * T * T * u2[0]) / (1 + a1 * T);
            y1[0] = (k1 * k2 * T * T * T * u2[0]) / mu;

            e1[1] = R - y1[0];
            u1[1] = u1[0] + kp * (e1[1] - e1[0]) + ki * e1[1] + kd * (e1[1] - 2 * e1[0]);
            e2[1] = u1[1] - y0[0];
            u2[1] = e2[1]+d[1];
            y0[1] = ((2 + a1 * T) * y0[0] + k1 * T * T * (u2[1] - u2[0])) / (1 + a1 * T);
            y1[1] = (k1 * k2 * T * T * T * (u2[1] - u2[0]) + (3 + 2 * (a1 + a2) * T + a1 * a2 * T * T) * y1[0]) / mu;

            e1[2] = R - y1[1];
            u1[2] = u1[1] + kp * (e1[2] - e1[1]) + ki * e1[2] + kd * (e1[2] - 2 * e1[1] + e1[0]);
            e2[2] = u1[2] - y0[1];
            u2[2] = e2[2]+d[2];
            y0[2] = ((2 + a1 * T) * y0[1] - y0[0] + k1 * T * T * (u2[2] - u2[1])) / (1 + a1 * T);
            y1[2] = (k1 * k2 * T * T * T * (u2[2] - u2[1]) + (3 + 2 * (a1 + a2) * T + a1 * a2 * T * T) * y1[1] + (-3 - (a1 + a2) * T) * y1[0]) / mu;

            for (int k = 3; k < rangeT; k++)
            {
                e1[k] = R - y1[k - 1];
                u1[k] = u1[k - 1] + kp * (e1[k] - e1[k - 1]) + ki * e1[k] + kd * (e1[k] - 2 * e1[k - 1] + e1[k - 2]);
                e2[k] = u1[k] - y0[k - 1];
                u2[k] = e2[k]+d[k];
                y0[k] = ((2 + a1 * T) * y0[k - 1] - y0[k - 2] + k1 * T * T * (u2[k] - u2[k - 1])) / (1 + (a1) * T);
                y1[k] = (k1 * k2 * T * T * T * (u2[k] - u2[k - 1]) + (3 + 2 * (a1 + a2) * T + a1 * a2 * T * T) * y1[k - 1] + (-3 - (a1 + a2) * T) * y1[k - 2] + y1[k - 3]) / mu;
            }
        }

        else if (signal == 3)
        {
            signalValue = Integer.valueOf(disturbvalue);
            e1[0] = R;
            u1[0] = kp * (e1[0]) + ki * e1[0] + kd * (e1[0]);
            e2[0] = u1[0];
            u2[0] = e2[0]+generateNoise(signalValue);
            y0[0] = (k1 * T * T * u2[0]) / (1 + a1 * T);
            y1[0] = (k1 * k2 * T * T * T * u2[0]) / mu;

            e1[1] = R - y1[0];
            u1[1] = u1[0] + kp * (e1[1] - e1[0]) + ki * e1[1] + kd * (e1[1] - 2 * e1[0]);
            e2[1] = u1[1] - y0[0];
            u2[1] = e2[1]+generateNoise(signalValue);
            y0[1] = ((2 + a1 * T) * y0[0] + k1 * T * T * (u2[1] - u2[0])) / (1 + a1 * T);
            y1[1] = (k1 * k2 * T * T * T * (u2[1] - u2[0]) + (3 + 2 * (a1 + a2) * T + a1 * a2 * T * T) * y1[0]) / mu;

            e1[2] = R - y1[1];
            u1[2] = u1[1] + kp * (e1[2] - e1[1]) + ki * e1[2] + kd * (e1[2] - 2 * e1[1] + e1[0]);
            e2[2] = u1[2] - y0[1];
            u2[2] = e2[2]+generateNoise(signalValue);
            y0[2] = ((2 + a1 * T) * y0[1] - y0[0] + k1 * T * T * (u2[2] - u2[1])) / (1 + a1 * T);
            y1[2] = (k1 * k2 * T * T * T * (u2[2] - u2[1]) + (3 + 2 * (a1 + a2) * T + a1 * a2 * T * T) * y1[1] + (-3 - (a1 + a2) * T) * y1[0]) / mu;

            for (int k = 3; k < rangeT; k++)
            {
                e1[k] = R - y1[k - 1];
                u1[k] = u1[k - 1] + kp * (e1[k] - e1[k - 1]) + ki * e1[k] + kd * (e1[k] - 2 * e1[k - 1] + e1[k - 2]);
                e2[k] = u1[k] - y0[k - 1];
                u2[k] = e2[k]+generateNoise(signalValue);
                y0[k] = ((2 + a1 * T) * y0[k - 1] - y0[k - 2] + k1 * T * T * (u2[k] - u2[k - 1])) / (1 + (a1) * T);
                y1[k] = (k1 * k2 * T * T * T * (u2[k] - u2[k - 1]) + (3 + 2 * (a1 + a2) * T + a1 * a2 * T * T) * y1[k - 1] + (-3 - (a1 + a2) * T) * y1[k - 2] + y1[k - 3]) / mu;
            }
        }
        try
        {
            saveTwo(y1);
            saveTwo(y0);
        } catch (Exception e)
        {
            throw new BusinessException(EmBusinessError.MODEL_PARAMS_EX);
        }
        result[0] = y0;
        result[1] = y1;
        return result;
    }

    private double[][] noDisChuanpid(Double kp, Double ki, Double kd, double k1, double k2, double a1, double a2,
                                     Double T, int rangeT, Double R, Double kp1, Double ki1) throws BusinessException {
        double[][] result = new double[2][rangeT];//返回的结果
        double[] y0 = new double[rangeT];//液位1
        double[] y1 = new double[rangeT];//液位2
        double[] e1 = new double[rangeT];
        double[] e2 = new double[rangeT];
        double[] u1 = new double[rangeT];
        double[] u2 = new double[rangeT];
        double mu = 1+(a1+a2)*T+a1*a2*T*T;
        e1[0]=R;
        u1[0]=kp*(e1[0])+ki*e1[0]+kd*(e1[0]);
        e2[0]=u1[0];
        u2[0]=0+kp1*(e2[0]-0)+ki1*e2[0];
        y0[0]=(k1*T*T*u2[0])/(1+a1*T);
        y1[0]=(k1*k2*T*T*T*u2[0])/mu;

        e1[1]=R-y1[0];
        u1[1]=u1[0]+kp*(e1[1]-e1[0])+ki*e1[1]+kd*(e1[1]-2*e1[0]);
        e2[1]=u1[1]-y0[0];
        u2[1]=u2[0]+kp1*(e2[1]-e2[0])+ki1*e2[1];
        y0[1]=((2+a1*T)*y0[0]+k1*T*T*(u2[1]-u2[0]))/(1+a1*T);
        y1[1]=(k1*k2*T*T*T*(u2[1]-u2[0])+(3+2*(a1+a2)*T+a1*a2*T*T)*y1[0])/mu;

        e1[2]=R-y1[1];
        u1[2]=u1[1]+kp*(e1[2]-e1[1])+ki*e1[2]+kd*(e1[2]-2*e1[1]+e1[0]);
        e2[2]=u1[2]-y0[1];
        u2[2]=u2[1]+kp1*(e2[2]-e2[1])+ki1*e2[2];
        y0[2]=((2+a1*T)*y0[1]-y0[0]+k1*T*T*(u2[2]-u2[1]))/(1+a1*T);
        y1[2]=(k1*k2*T*T*T*(u2[2]-u2[1])+(3+2*(a1+a2)*T+a1*a2*T*T)*y1[1]+(-3-(a1+a2)*T)*y1[0])/mu;

        for (int k = 3; k < rangeT; k++) {
            e1[k]=R-y1[k-1];
            u1[k]=u1[k-1]+kp*(e1[k]-e1[k-1])+ki*e1[k]+kd*(e1[k]-2*e1[k-1]+e1[k-2]);
            e2[k]=u1[k]-y0[k-1];
            u2[k]=u2[k-1]+kp1*(e2[k]-e2[k-1])+ki1*e2[k];
            y0[k]=((2+a1*T)*y0[k-1]-y0[k-2]+k1*T*T*(u2[k]-u2[k-1]))/(1+(a1)*T);
            y1[k]=(k1*k2*T*T*T*(u2[k]-u2[k-1])+(3+2*(a1+a2)*T+a1*a2*T*T)*y1[k-1]+(-3-(a1+a2)*T)*y1[k-2]+y1[k-3])/mu;
        }
        try {
            saveTwo(y1);
            saveTwo(y0);
        }catch (Exception e){
            throw new BusinessException(EmBusinessError.MODEL_PARAMS_EX);
        }
        result[0] = y0;
        result[1] = y1;
        return result;

    }

    /**
     *          无扰动阶跃信号仿真
     * @param kp
     * @param ki
     * @param kd
     * @param k1
     * @param k2
     * @param a1
     * @param a2
     * @param T
     * @param rangeT    仿真时间（现在实现的都是在后台代码中给定）
     * @param singnal   阶跃信号
     * @return          仿真结果
     */
    public double[][] noDispid(double kp, double ki, double kd, double k1,
                             double k2, double a1, double a2, double T,
                             int rangeT,double R) throws BusinessException {
        double[][] result = new double[2][rangeT];//返回的结果
        double[] y0 = new double[rangeT];//液位1
        double[] y1 = new double[rangeT];//液位2
        double[] e1 = new double[rangeT];
        double[] e2 = new double[rangeT];
        double[] u1 = new double[rangeT];
        double[] u2 = new double[rangeT];
        double mu = 1 + (a1 + a2) * T + a1 * a2 * T * T;
        e1[0] = R;
        u1[0] = kp * (e1[0]) + ki * e1[0] + kd * (e1[0]);
        e2[0] = u1[0];
        u2[0] = e2[0];
        y0[0] = (k1 * T * T * u2[0]) / (1 + a1 * T);
        y1[0] = (k1 * k2 * T * T * T * u2[0]) / mu;

        e1[1] = R - y1[0];
        u1[1] = u1[0] + kp * (e1[1] - e1[0]) + ki * e1[1] + kd * (e1[1] - 2 * e1[0]);
        e2[1] = u1[1] - y0[0];
        u2[1] = e2[1];
        y0[1] = ((2 + a1 * T) * y0[0] + k1 * T * T * (u2[1] - u2[0])) / (1 + a1 * T);
        y1[1] = (k1 * k2 * T * T * T * (u2[1] - u2[0]) + (3 + 2 * (a1 + a2) * T + a1 * a2 * T * T) * y1[0]) / mu;

        e1[2] = R - y1[1];
        u1[2] = u1[1] + kp * (e1[2] - e1[1]) + ki * e1[2] + kd * (e1[2] - 2 * e1[1] + e1[0]);
        e2[2] = u1[2] - y0[1];
        u2[2] = e2[2];
        y0[2] = ((2 + a1 * T) * y0[1] - y0[0] + k1 * T * T * (u2[2] - u2[1])) / (1 + a1 * T);
        y1[2] = (k1 * k2 * T * T * T * (u2[2] - u2[1]) + (3 + 2 * (a1 + a2) * T + a1 * a2 * T * T) * y1[1] + (-3 - (a1 + a2) * T) * y1[0]) / mu;

        for (int k = 3; k < rangeT; k++)
        {
            e1[k] = R - y1[k - 1];
            u1[k] = u1[k - 1] + kp * (e1[k] - e1[k - 1]) + ki * e1[k] + kd * (e1[k] - 2 * e1[k - 1] + e1[k - 2]);
            e2[k] = u1[k] - y0[k - 1];
            u2[k] = e2[k];
            y0[k] = ((2 + a1 * T) * y0[k - 1] - y0[k - 2] + k1 * T * T * (u2[k] - u2[k - 1])) / (1 + (a1) * T);
            y1[k] = (k1 * k2 * T * T * T * (u2[k] - u2[k - 1]) + (3 + 2 * (a1 + a2) * T + a1 * a2 * T * T) * y1[k - 1] + (-3 - (a1 + a2) * T) * y1[k - 2] + y1[k - 3]) / mu;
        }
        try
        {
            saveTwo(y1);
            saveTwo(y0);
        } catch (Exception e)
        {
            throw new BusinessException(EmBusinessError.MODEL_PARAMS_EX);
        }
        result[0] = y0;
        result[1] = y1;
        return result;
    }

    /**
     * 参数：
     newScale - 要返回的 BigDecimal 值的标度。
     roundingMode - 要应用的舍入模式。
     返回：
     一个 BigDecimal，其标度为指定值，其非标度值可以通过此 BigDecimal 的非标度值乘以或除以十的适当次幂来确定。
     */
    public void saveTwo(double y[]) {
        for (int i = 0; i < y.length; i++) {
            BigDecimal bg = new BigDecimal(y[i]);
            y[i] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
    }

    public double generateNoise(double a){
        java.util.Random r = new java.util.Random();
        double noise = r.nextGaussian()*a;
        return noise;
    }
}

package com.threetanks.service;

import com.threetanks.controller.viewobject.ReturnResult;
import com.threetanks.error.BusinessException;

public interface ModelService {
    String createModel(Double k1, Double k2, Double a1, Double a2) throws BusinessException;

    String createModel(Double k1, Double a1) throws BusinessException;

    ReturnResult easyPID(Double kp, Double ki, Double kd, String modelId, Double T, Double R) throws BusinessException;

    ReturnResult integralSeparationPID(Double kp, Double ki, Double kd, String modelId, Double T, Double R,Double yu) throws BusinessException;

    ReturnResult oneCascade(Double kp, Double ki, Double kd, String modelId, Double T, Double R, Double kp1, Double ki1, Integer signal, String disturbvalue) throws BusinessException;

    ReturnResult noDisturbPID(Double kp, Double ki, Double kd, String modelId, Double T, Double R) throws BusinessException;

    ReturnResult noDisturbChuanPID(Double kp, Double ki, Double kd, String modelId, Double t, Double r, Double kp1, Double ki1) throws BusinessException;

    ReturnResult disturbPID(Double kp, Double ki, Double kd, String modelId, Double t, Double r, Integer signal, String disturbvalue) throws BusinessException;

    ReturnResult disturbChuanPID(Double kp, Double ki, Double kd, String modelId, Double T, Double R, Double kp1, Double ki1, Integer signal, String disturbvalue) throws BusinessException;

    ReturnResult disturbPreChuanPID(Double kp, Double ki, Double kd, String modelId, Double T, Double R, Double kp1, Double ki1,
                                    Integer signal, String disturbvalue, String disturbvalue1) throws BusinessException;

    ReturnResult oneLatePID(Double kp, Double ki, Double kd,Double kp0, Double ki0, Double kd0, String modelId, Double T, Double R, Double tao) throws BusinessException;

    ReturnResult twoPrePID(Double kp1,Double ki1,Double kd1,Double kp10,Double ki10,Double kd10,Double kp0,Double ki0,
                           Double kd0,String modelId,Double T, Double R,Double Kn,Double Tn) throws BusinessException;
    //ReturnResult onePrePID(Double kp, Double ki, Double kd, String modelId, Double T, Double R, Double kp1, Double ki1, Integer signal, String disturbvalue) throws BusinessException;

}

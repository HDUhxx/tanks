package com.threetanks.controller;

import com.threetanks.controller.viewobject.ReturnResult;
import com.threetanks.error.BusinessException;
import com.threetanks.response.CommonReturnType;
import com.threetanks.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("model")
@RequestMapping("/model")

public class ModelController extends BaseController{

    @Autowired
    private ModelService modelService;

    //把模型存入后台，返回模型
    @RequestMapping(path = "/create",method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType createModel(
            @RequestParam("k1") Double k1,@RequestParam("k2") Double k2,
            @RequestParam("a1") Double a1,@RequestParam("a2") Double a2) throws BusinessException {

        String modelId = modelService.createModel(k1,k2,a1,a2);
        return CommonReturnType.create(modelId);
    }

    //把模型存入后台，返回模型
    @RequestMapping(path = "/createOne",method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType createModel(@RequestParam("k1") Double k1,
                                        @RequestParam("a1") Double a1) throws BusinessException {

        String modelId = modelService.createModel(k1,a1);
        return CommonReturnType.create(modelId);
    }

    //简单pid控制策略
    @RequestMapping(path = "/easyPID",method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType easyPID(
            @RequestParam("kp") Double kp,@RequestParam("ki") Double ki,
            @RequestParam("kd") Double kd,@RequestParam("modelId") String modelId,
            @RequestParam("T") Double T,@RequestParam("R") Double R) throws BusinessException {

        ReturnResult returnResult = modelService.easyPID(kp,ki,kd,modelId,T,R);
        return CommonReturnType.create(returnResult);
    }

    //积分分离pid控制策略
    @RequestMapping(path = "/integralSeparationPID",method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType integralSeparationPID(
            @RequestParam("kp") Double kp,@RequestParam("ki") Double ki,
            @RequestParam("kd") Double kd,@RequestParam("modelId") String modelId,
            @RequestParam("T") Double T,@RequestParam("R") Double R,@RequestParam("yu") Double yu) throws BusinessException {

        ReturnResult returnResult = modelService.integralSeparationPID(kp,ki,kd,modelId,T,R,yu);
        return CommonReturnType.create(returnResult);
    }

    //一阶串级控制策略
    @RequestMapping(path = "/oneCascade",method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType oneCascade(
            @RequestParam("kp") Double kp,@RequestParam("ki") Double ki,
            @RequestParam("kd") Double kd,@RequestParam("modelId") String modelId,
            @RequestParam("T") Double T,@RequestParam("R") Double R,
            @RequestParam("kp1") Double kp1,@RequestParam(value = "ki1",required = false) Double ki1,
            @RequestParam("signal")Integer signal,
            @RequestParam("disturbValue")String disturbvalue) throws BusinessException {

        ReturnResult returnResult = modelService.oneCascade(kp,ki,kd,modelId,T,R,kp1,ki1,signal,disturbvalue);
        return CommonReturnType.create(returnResult);
    }

    //无扰动pid控制策略
    @RequestMapping(path = "/noDisturbPID",method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType noDisturbPID(
            @RequestParam("kp") Double kp,@RequestParam("ki") Double ki,
            @RequestParam("kd") Double kd,@RequestParam("modelId") String modelId,
            @RequestParam("T") Double T,@RequestParam("R") Double R) throws BusinessException {

        ReturnResult returnResult = modelService.noDisturbPID(kp,ki,kd,modelId,T,R);
        return CommonReturnType.create(returnResult);
    }

    //无扰动串级pid控制策略
    @RequestMapping(path = "/noDisturbChuanPID",method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType noDisturbchuanPID(
            @RequestParam("kp") Double kp,@RequestParam("ki") Double ki,
            @RequestParam("kd") Double kd,@RequestParam("modelId") String modelId,
            @RequestParam("T") Double T,@RequestParam("R") Double R,
            @RequestParam("kp1") Double kp1,@RequestParam(value = "ki1",required = false) Double ki1) throws BusinessException {

        ReturnResult returnResult = modelService.noDisturbChuanPID(kp,ki,kd,modelId,T,R,kp1,ki1);
        return CommonReturnType.create(returnResult);
    }

    //扰动pid控制策略
    @RequestMapping(path = "/disturbPID",method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType disturbPID(
            @RequestParam("kp") Double kp,@RequestParam("ki") Double ki,
            @RequestParam("kd") Double kd,@RequestParam("modelId") String modelId,
            @RequestParam("T") Double T,@RequestParam("R") Double R,
            @RequestParam("signal")Integer signal,
            @RequestParam("disturbValue")String disturbvalue) throws BusinessException {

        ReturnResult returnResult = modelService.disturbPID(kp,ki,kd,modelId,T,R,signal,disturbvalue);
        return CommonReturnType.create(returnResult);
    }

    //扰动串级pid控制策略
    @RequestMapping(path = "/disturbChuanPID",method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType disturbchuanPID(
            @RequestParam("kp") Double kp,@RequestParam("ki") Double ki,
            @RequestParam("kd") Double kd,@RequestParam("modelId") String modelId,
            @RequestParam("T") Double T,@RequestParam("R") Double R,
            @RequestParam("kp1") Double kp1,@RequestParam(value = "ki1",required = false) Double ki1,
            @RequestParam("signal")Integer signal,
            @RequestParam("disturbValue")String disturbvalue) throws BusinessException {

        ReturnResult returnResult = modelService.disturbChuanPID(kp,ki,kd,modelId,T,R,kp1,ki1,signal,disturbvalue);
        return CommonReturnType.create(returnResult);
    }

    //扰动串级pid控制策略
    @RequestMapping(path = "/disturbPreChuanPID",method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType disturbPrechuanPID(
            @RequestParam("kp") Double kp,@RequestParam("ki") Double ki,
            @RequestParam("kd") Double kd,@RequestParam("modelId") String modelId,
            @RequestParam("T") Double T,@RequestParam("R") Double R,
            @RequestParam("kp1") Double kp1,@RequestParam(value = "ki1",required = false) Double ki1,
            @RequestParam("signal")Integer signal,
            @RequestParam("disturbValue")String disturbvalue,
            @RequestParam("disturbValue1")String disturbvalue1) throws BusinessException {

        ReturnResult returnResult = modelService.disturbPreChuanPID(kp,ki,kd,modelId,T,R,kp1,ki1,signal,disturbvalue,disturbvalue1);
        return CommonReturnType.create(returnResult);
    }

    //纯滞后控制
    @RequestMapping(path = "/oneLatePID",method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType oneLatePID(
            @RequestParam("kp") Double kp,@RequestParam("ki") Double ki,
            @RequestParam("kd") Double kd,@RequestParam("modelId") String modelId,
            @RequestParam("T") Double T,@RequestParam("R") Double R,
            @RequestParam("kp0") Double kp0,@RequestParam("ki0") Double ki0,
            @RequestParam("kd0")Double kd0, @RequestParam("tao")Double tao
            ) throws BusinessException {
        ReturnResult returnResult = modelService.oneLatePID(kp,ki,kd,kp0,ki0,kd0,modelId,T,R,tao);
        return CommonReturnType.create(returnResult);
    }

    //前馈控制
    @RequestMapping(path = "/twoPrePID",method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType twoPrePID(
            @RequestParam("kp1") Double kp1,@RequestParam("ki1") Double ki1, @RequestParam("kd1") Double kd1,
            @RequestParam("kp10") Double kp10,@RequestParam("ki10") Double ki10, @RequestParam("kd10") Double kd10,
            @RequestParam("kp0") Double kp0,@RequestParam("ki0") Double ki0, @RequestParam("kd0") Double kd0,
            @RequestParam("modelId") String modelId, @RequestParam("T") Double T,@RequestParam("R") Double R,
            @RequestParam("Kn")Double Kn,@RequestParam("Tn")Double Tn
    ) throws BusinessException {
        ReturnResult returnResult = modelService.twoPrePID(kp1,ki1,kd1,kp10,ki10,kd10,kp0,ki0,kd0,modelId,T,R,Kn,Tn);
        return CommonReturnType.create(returnResult);
    }
}

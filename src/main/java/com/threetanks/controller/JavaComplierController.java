package com.threetanks.controller;

import com.threetanks.controller.viewobject.ReturnResult;
import com.threetanks.error.BusinessException;
import com.threetanks.error.EmBusinessError;
import com.threetanks.response.CommonReturnType;
import com.threetanks.service.JavaComplieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * author: haiyangp
 * date:  2017/9/22
 * desc: JAVA编译器controller
 */
@CrossOrigin
@Controller
public class JavaComplierController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private JavaComplieService javaComplieService;

    /**
     * 检验编译
     *
     * @param javaSource JAVA代码
     * @return 是否能编译
     */
    @ResponseBody
    @RequestMapping("/model/testCompile")
    public CommonReturnType testComplie(@RequestParam("javasource")String javaSource) throws BusinessException {
        try {
            if (StringUtils.isEmpty(javaSource)) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
            }
            javaSource = javaSource.replace("&lt;","<");
            javaSource = javaSource.replace("&gt;",">");
            javaSource = javaSource.replace("&nbsp;"," ");
            Class clazz = javaComplieService.complie(javaSource);
            return CommonReturnType.create("success");
        } catch (Exception e) {

            return CommonReturnType.create("fail");
        }
    }


    /**
     * 执行编译
     *
     * @param javaSource JAVA代码
     * @return 编译结果
     */
    @ResponseBody
    @RequestMapping("/model/compile")
    public CommonReturnType complie(@RequestParam("javasource")String javaSource) throws BusinessException {
        try {
            if (StringUtils.isEmpty(javaSource)) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
            }
            javaSource = javaSource.replace("&lt;","<");
            javaSource = javaSource.replace("&gt;",">");
            javaSource = javaSource.replace("&nbsp;"," ");
            Class clazz = javaComplieService.complie(javaSource);
            System.out.println(111111);

            ReturnResult returnResult = javaComplieService.excuteMainMethod(clazz);
            return CommonReturnType.create(returnResult);

        } catch (Exception e) {
/*
            return CommonReturnType.create("fail");
*/
            e.printStackTrace();
            throw new BusinessException(EmBusinessError.COMPILE_ERROR);
        }
    }


    /**
     * 获取运行时程序需要的参数
     *
     * @param excuteArgsStr 参数字符串
     */
    private String[] getInputArgs(String excuteArgsStr) {
        if (StringUtils.isEmpty(excuteArgsStr)) {
            return null;
        } else {
            return excuteArgsStr.split(" ");
        }
    }

    @GetMapping(value = {"", "index"})
    public String index() {

        return "index";
    }


}

package org.lu.zhaodazi.user.controller;

import org.lu.zhaodazi.common.domain.vo.res.ApiResult;
import org.lu.zhaodazi.match.domain.dto.MatchingConditionDTO;
import org.lu.zhaodazi.match.enums.ProvinceEnum;
import org.lu.zhaodazi.match.enums.SexEnum;
import org.lu.zhaodazi.user.domain.entity.User;
import org.lu.zhaodazi.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/updateCondition")
    public ApiResult updateCondition(@RequestBody MatchingConditionDTO matchingConditionDTO){
        //TODO 没有验证呢
        User user = new User();
        user.setId(matchingConditionDTO.getUid());
        user.setSex(SexEnum.of(matchingConditionDTO.getSex()).getCode());
        user.setProvince(ProvinceEnum.of(matchingConditionDTO.getProvince()).getCode());
        user.setWantProvince(ProvinceEnum.of(matchingConditionDTO.getWantProvince()).getCode());
        user.setWantSex(SexEnum.of(matchingConditionDTO.getWantSex()).getCode());
        userService.update(user);
        return ApiResult.success("更新成功");
    }

}

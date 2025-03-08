package com.esmiao.cloudpicture.interfaces.controller.space;

import com.esmiao.cloudpicture.application.user.service.UserService;
import com.esmiao.cloudpicture.domain.user.constant.UserConstant;
import com.esmiao.cloudpicture.domain.user.entity.User;
import com.esmiao.cloudpicture.infrastructure.annotation.RoleValidation;
import com.esmiao.cloudpicture.infrastructure.common.CommonResponse;
import com.esmiao.cloudpicture.infrastructure.utils.ResponseUtil;
import com.esmiao.cloudpicture.interfaces.dto.space.analyze.*;
import com.esmiao.cloudpicture.domain.space.entity.Space;
import com.esmiao.cloudpicture.interfaces.vo.space.SpaceVo;
import com.esmiao.cloudpicture.interfaces.vo.space.analyze.*;
import com.esmiao.cloudpicture.application.space.service.SpaceAnalyzeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Space analyze endpoints
 * @author Steven Chen
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/space/analyze")
public class SpaceAnalyzeController {

    private final SpaceAnalyzeService spaceAnalyzeService;

    private final UserService userService;

    /**
     * Load space usage
     */
    @PostMapping("/usage")
    public CommonResponse<SpaceUsageAnalyzeResponse> getSpaceUsageAnalyze(
        @RequestBody SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest,
        HttpServletRequest request) {

        return ResponseUtil.success(spaceAnalyzeService.analyzeSpaceUsage(spaceUsageAnalyzeRequest, request));
    }

    @PostMapping("/category")
    public CommonResponse<List<SpaceCategoryAnalyzeResponse>> getSpaceCategoryAnalyze(
        @RequestBody SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest,
        HttpServletRequest request) {

        List<SpaceCategoryAnalyzeResponse> resultList = spaceAnalyzeService.analyzeSpaceCategory(spaceCategoryAnalyzeRequest, request);

        return ResponseUtil.success(resultList);
    }

    @PostMapping("/tag")
    public CommonResponse<List<SpaceTagAnalyzeResponse>> getSpaceTagAnalyze(
        @RequestBody SpaceTagAnalyzeRequest spaceTagAnalyzeRequest,
        HttpServletRequest request) {

        User loginUser = userService.getLoginUser(request);
        List<SpaceTagAnalyzeResponse> resultList = spaceAnalyzeService.analyzeSpaceTag(spaceTagAnalyzeRequest, loginUser);

        return ResponseUtil.success(resultList);
    }

    @PostMapping("/size")
    public CommonResponse<List<SpaceSizeAnalyzeResponse>> getSpaceSizeAnalyze(
        @RequestBody SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest,
        HttpServletRequest request) {

        User loginUser = userService.getLoginUser(request);
        List<SpaceSizeAnalyzeResponse> resultList = spaceAnalyzeService.analyzeSpaceSize(spaceSizeAnalyzeRequest, loginUser);

        return ResponseUtil.success(resultList);
    }

    @PostMapping("/user")
    public CommonResponse<List<SpaceUserAnalyzeResponse>> getSpaceUserAnalyze(
        @RequestBody SpaceUserAnalyzeRequest spaceUserAnalyzeRequest,
        HttpServletRequest request) {

        User loginUser = userService.getLoginUser(request);
        List<SpaceUserAnalyzeResponse> resultList = spaceAnalyzeService.analyzeSpaceUser(spaceUserAnalyzeRequest, loginUser);

        return ResponseUtil.success(resultList);
    }

    @RoleValidation(roles = UserConstant.ROLE_ADMIN)
    @PostMapping("/rank")
    public CommonResponse<List<SpaceVo>> getSpaceRankAnalyze(
        @RequestBody SpaceRankAnalyzeRequest spaceRankAnalyzeRequest,
        HttpServletRequest request) {

        User loginUser = userService.getLoginUser(request);
        List<Space> resultList = spaceAnalyzeService.analyzeSpaceRank(spaceRankAnalyzeRequest, loginUser);

        return ResponseUtil.success(resultList.stream().map(SpaceVo::of).toList());
    }


}


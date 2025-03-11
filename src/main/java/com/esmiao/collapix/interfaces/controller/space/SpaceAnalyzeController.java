package com.esmiao.collapix.interfaces.controller.space;

import com.esmiao.collapix.application.user.service.UserService;
import com.esmiao.collapix.domain.user.constant.UserConstant;
import com.esmiao.collapix.domain.user.entity.User;
import com.esmiao.collapix.infrastructure.annotation.RoleValidation;
import com.esmiao.collapix.infrastructure.common.CommonResponse;
import com.esmiao.collapix.infrastructure.utils.ResponseUtil;
import com.esmiao.collapix.interfaces.dto.space.analyze.*;
import com.esmiao.collapix.domain.space.entity.Space;
import com.esmiao.collapix.interfaces.vo.space.SpaceVo;
import com.esmiao.collapix.interfaces.vo.space.analyze.*;
import com.esmiao.collapix.application.space.service.SpaceAnalyzeService;
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


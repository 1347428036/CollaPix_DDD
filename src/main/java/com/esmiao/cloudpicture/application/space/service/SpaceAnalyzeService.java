package com.esmiao.cloudpicture.application.space.service;


import com.esmiao.cloudpicture.domain.space.entity.Space;
import com.esmiao.cloudpicture.domain.user.entity.User;
import com.esmiao.cloudpicture.interfaces.dto.space.analyze.*;
import com.esmiao.cloudpicture.interfaces.vo.space.analyze.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * Maintain all space analysis operations
 * @author Steven Chen
 */
public interface SpaceAnalyzeService {

    /**
     * Get space usage analysis
     * */
    SpaceUsageAnalyzeResponse analyzeSpaceUsage(SpaceUsageAnalyzeRequest analyzeRequest, HttpServletRequest request);

    List<SpaceCategoryAnalyzeResponse> analyzeSpaceCategory(SpaceCategoryAnalyzeRequest analyzeRequest, HttpServletRequest request);

    List<SpaceTagAnalyzeResponse> analyzeSpaceTag(SpaceTagAnalyzeRequest request, User loginUser);

    List<SpaceSizeAnalyzeResponse> analyzeSpaceSize(SpaceSizeAnalyzeRequest request, User loginUser);

    List<SpaceUserAnalyzeResponse> analyzeSpaceUser(SpaceUserAnalyzeRequest request, User loginUser);

    List<Space> analyzeSpaceRank(SpaceRankAnalyzeRequest request, User loginUser);
}

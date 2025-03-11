package com.esmiao.collapix.application.space.service;


import com.esmiao.collapix.domain.space.entity.Space;
import com.esmiao.collapix.domain.user.entity.User;
import com.esmiao.collapix.interfaces.dto.space.analyze.*;
import com.esmiao.collapix.interfaces.vo.space.analyze.*;
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

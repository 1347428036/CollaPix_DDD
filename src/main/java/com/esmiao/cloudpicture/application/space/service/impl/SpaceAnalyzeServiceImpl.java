package com.esmiao.cloudpicture.application.space.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.esmiao.cloudpicture.application.picture.service.PictureService;
import com.esmiao.cloudpicture.application.space.service.SpaceService;
import com.esmiao.cloudpicture.application.user.service.UserService;
import com.esmiao.cloudpicture.domain.picture.entity.Picture;
import com.esmiao.cloudpicture.domain.space.constant.SpaceConstant;
import com.esmiao.cloudpicture.domain.space.entity.Space;
import com.esmiao.cloudpicture.application.space.service.SpaceAnalyzeService;
import com.esmiao.cloudpicture.domain.user.entity.User;
import com.esmiao.cloudpicture.infrastructure.exception.BusinessException;
import com.esmiao.cloudpicture.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.cloudpicture.infrastructure.exception.ThrowErrorUtil;
import com.esmiao.cloudpicture.infrastructure.utils.NumUtil;
import com.esmiao.cloudpicture.interfaces.dto.space.analyze.*;
import com.esmiao.cloudpicture.interfaces.vo.space.analyze.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Space analyze service implementation
 * @author Steven Chen
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SpaceAnalyzeServiceImpl implements SpaceAnalyzeService {

    private final SpaceService spaceService;

    private final PictureService pictureService;

    private final UserService userService;

    @Override
    public SpaceUsageAnalyzeResponse analyzeSpaceUsage(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, HttpServletRequest request) {
        ThrowErrorUtil.throwIf(spaceUsageAnalyzeRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        if (spaceUsageAnalyzeRequest.isQueryAll() || spaceUsageAnalyzeRequest.isQueryPublic()) {
            // Query all or public gallery logic
            // Only admin can access
            ThrowErrorUtil.throwIf(!loginUser.isAdmin(), ErrorCodeEnum.NO_PERMISSION_ERROR);
            QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("picSize");
            if (!spaceUsageAnalyzeRequest.isQueryPublic()) {
                queryWrapper.eq("spaceId", SpaceConstant.PUBLIC_SPACE_ID);
            }

            return generateSpaceUsageResponse(pictureService.getPictureSizeList(queryWrapper));
        }

        Long spaceId = NumUtil.parseLong(spaceUsageAnalyzeRequest.getSpaceId());
        Space space = spaceService.getSpaceById(spaceId);
        ThrowErrorUtil.throwIf(space == null, ErrorCodeEnum.NOT_FOUND_ERROR, "Space not found");
        spaceService.validateSpacePermission(loginUser, space);

        return generatePrivateSpaceUsageAnalyzeResponse(space);
    }

    @Override
    public List<SpaceCategoryAnalyzeResponse> analyzeSpaceCategory(
        SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, HttpServletRequest request) {

        ThrowErrorUtil.throwIf(spaceCategoryAnalyzeRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        checkSpaceAnalyzePermission(spaceCategoryAnalyzeRequest, loginUser);

        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyzeQueryWrapper(spaceCategoryAnalyzeRequest, queryWrapper);
        queryWrapper.select("category AS category",
                "COUNT(*) AS count",
                "SUM(picSize) AS totalSize")
            .groupBy("category");

        return pictureService.getPictureInfoMaps(queryWrapper)
            .stream()
            .map(result -> {
                String category = result.get("category") != null ? result.get("category").toString() : "default";
                Long count = ((Number) result.get("count")).longValue();
                Long totalSize = ((Number) result.get("totalSize")).longValue();

                return new SpaceCategoryAnalyzeResponse(category, count, totalSize);
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<SpaceTagAnalyzeResponse> analyzeSpaceTag(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser) {
        ThrowErrorUtil.throwIf(spaceTagAnalyzeRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        checkSpaceAnalyzePermission(spaceTagAnalyzeRequest, loginUser);

        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyzeQueryWrapper(spaceTagAnalyzeRequest, queryWrapper);
        queryWrapper.select("tags");

        // 合并所有标签并统计使用次数
        Map<String, Long> tagCountMap = pictureService.getPictureJsonTagList(queryWrapper)
            .stream()
            .filter(ObjUtil::isNotNull)
            .flatMap(tagsJson -> JSONUtil.toList(tagsJson, String.class).stream())
            .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()));

        return tagCountMap.entrySet().stream()
            // Descending order
            .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
            .map(entry -> new SpaceTagAnalyzeResponse(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    @Override
    public List<SpaceSizeAnalyzeResponse> analyzeSpaceSize(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser) {
        ThrowErrorUtil.throwIf(spaceSizeAnalyzeRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        checkSpaceAnalyzePermission(spaceSizeAnalyzeRequest, loginUser);

        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyzeQueryWrapper(spaceSizeAnalyzeRequest, queryWrapper);
        queryWrapper.select("picSize");
        List<Long> picSizes = pictureService.getPictureSizeList(queryWrapper);
        Map<String, Long> sizeRanges = new LinkedHashMap<>();
        for (Long picSize : picSizes) {
            if (picSize < 100 * 1024) {
                sizeRanges.merge("<100KB", 1L, Long::sum);
            }
            if (picSize >= 100 * 1024 && picSize < 500 * 1024) {
                sizeRanges.merge("100KB-500KB", 1L, Long::sum);
            }
            if (picSize >= 500 * 1024 && picSize < 1024 * 1024) {
                sizeRanges.merge("500KB-1MB", 1L, Long::sum);
            }
            if (picSize >= 1024 * 1024) {
                sizeRanges.merge(">1MB", 1L, Long::sum);
            }
        }

        return sizeRanges.entrySet().stream()
            .map(entry -> new SpaceSizeAnalyzeResponse(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    @Override
    public List<SpaceUserAnalyzeResponse> analyzeSpaceUser(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, User loginUser) {
        ThrowErrorUtil.throwIf(spaceUserAnalyzeRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        checkSpaceAnalyzePermission(spaceUserAnalyzeRequest, loginUser);

        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        Long userId = NumUtil.parseLong(spaceUserAnalyzeRequest.getUserId());
        queryWrapper.eq(ObjUtil.isNotNull(userId), "userId", userId);
        fillAnalyzeQueryWrapper(spaceUserAnalyzeRequest, queryWrapper);
        switch (spaceUserAnalyzeRequest.getTimeDimension()) {
            case "day" -> queryWrapper.select("DATE_FORMAT(createTime, '%Y-%m-%d') AS period", "COUNT(*) AS count");
            case "week" -> queryWrapper.select("YEARWEEK(createTime) AS period", "COUNT(*) AS count");
            case "month" -> queryWrapper.select("DATE_FORMAT(createTime, '%Y-%m') AS period", "COUNT(*) AS count");
            default -> throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "Unsupported time dimension");
        }
        queryWrapper.groupBy("period").orderByAsc("period");
        List<Map<String, Object>> queryResult = pictureService.getPictureTimeDimensionList(queryWrapper);

        return queryResult.stream()
            .map(result -> {
                String period = result.get("period").toString();
                Long count = ((Number) result.get("count")).longValue();

                return new SpaceUserAnalyzeResponse(period, count);
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<Space> analyzeSpaceRank(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser) {
        ThrowErrorUtil.throwIf(spaceRankAnalyzeRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        ThrowErrorUtil.throwIf(!loginUser.isAdmin(), ErrorCodeEnum.NO_PERMISSION_ERROR, "No permission to check space ranking");

        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "spaceName", "userId", "totalSize")
            .orderByDesc("totalSize")
            .last("LIMIT " + spaceRankAnalyzeRequest.getTopN());

        return spaceService.listSpaces(queryWrapper);
    }

    private SpaceUsageAnalyzeResponse generateSpaceUsageResponse(List<Long> pictureObjList) {
        long usedSize = pictureObjList.stream()
            .mapToLong(result -> result != null ? result : 0)
            .sum();
        long usedCount = pictureObjList.size();

        SpaceUsageAnalyzeResponse spaceUsageAnalyzeResponse = new SpaceUsageAnalyzeResponse();
        spaceUsageAnalyzeResponse.setUsedSize(usedSize);
        spaceUsageAnalyzeResponse.setUsedCount(usedCount);
        // Public space has no limit
        spaceUsageAnalyzeResponse.setMaxSize(null);
        spaceUsageAnalyzeResponse.setSizeUsageRatio(null);
        spaceUsageAnalyzeResponse.setMaxCount(null);
        spaceUsageAnalyzeResponse.setCountUsageRatio(null);

        return spaceUsageAnalyzeResponse;
    }

    private SpaceUsageAnalyzeResponse generatePrivateSpaceUsageAnalyzeResponse(Space space) {
        SpaceUsageAnalyzeResponse response = new SpaceUsageAnalyzeResponse();
        response.setUsedSize(space.getTotalSize());
        response.setMaxSize(space.getMaxSize());
        double sizeUsageRatio = NumberUtil.round(space.getTotalSize() * 100.0 / space.getMaxSize(), 2).doubleValue();
        response.setSizeUsageRatio(sizeUsageRatio);
        response.setUsedCount(space.getTotalCount());
        response.setMaxCount(space.getMaxCount());
        double countUsageRatio = NumberUtil.round(space.getTotalCount() * 100.0 / space.getMaxCount(), 2).doubleValue();
        response.setCountUsageRatio(countUsageRatio);

        return response;
    }

    private void checkSpaceAnalyzePermission(SpaceAnalyzeRequest spaceAnalyzeRequest, User loginUser) {
        // Check permission
        if (spaceAnalyzeRequest.isQueryAll() || spaceAnalyzeRequest.isQueryPublic()) {
            // Full space analysis or public gallery permission check: only admin can access
            ThrowErrorUtil.throwIf(!loginUser.isAdmin(), ErrorCodeEnum.NO_PERMISSION_ERROR);
        } else {
            // Private space permission check
            Long spaceId = NumUtil.parseLong(spaceAnalyzeRequest.getSpaceId());
            Space space = spaceService.getSpaceById(spaceId);
            ThrowErrorUtil.throwIf(space == null, ErrorCodeEnum.NOT_FOUND_ERROR, "Space not found");
            spaceService.validateSpacePermission(loginUser, space);
        }
    }

    private static void fillAnalyzeQueryWrapper(SpaceAnalyzeRequest spaceAnalyzeRequest, QueryWrapper<Picture> queryWrapper) {
        if (spaceAnalyzeRequest.isQueryAll()) {
            return;
        }
        if (spaceAnalyzeRequest.isQueryPublic()) {
            queryWrapper.eq("spaceId", SpaceConstant.PUBLIC_SPACE_ID);
            return;
        }
        Long spaceId = NumUtil.parseLong(spaceAnalyzeRequest.getSpaceId());
        if (spaceId != null) {
            queryWrapper.eq("spaceId", spaceId);
            return;
        }
        throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "No specified query range");
    }

}

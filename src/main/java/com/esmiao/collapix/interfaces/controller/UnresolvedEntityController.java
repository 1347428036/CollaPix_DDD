package com.esmiao.collapix.interfaces.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.esmiao.collapix.infrastructure.api.imagesearch.model.ImageSearchResult;
import com.esmiao.collapix.interfaces.dto.space.SpaceLevel;
import com.esmiao.collapix.interfaces.vo.space.analyze.SpaceCategoryAnalyzeResponse;
import com.esmiao.collapix.interfaces.vo.space.analyze.SpaceSizeAnalyzeResponse;
import com.esmiao.collapix.interfaces.vo.space.analyze.SpaceTagAnalyzeResponse;
import com.esmiao.collapix.interfaces.vo.space.analyze.SpaceUserAnalyzeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller is used to resolve the entity class.
 * Some entities cannot be resolved by swagger when they are used in jdk generic type like {@code List<T>}.
 * @author Steven Chen
 */
@RestController
@RequestMapping("/entity/unresolved")
public class UnresolvedEntityController {

    @GetMapping("/search/search-result-entity")
    public ImageSearchResult getSearchResultEntity() {
        return null;
    }

    @GetMapping("/page/order-item")
    public OrderItem getOrderItemEntity() {
        return null;
    }

    @GetMapping("/space/space-level")
    public SpaceLevel getSpaceLevelEntity() {
        return null;
    }

    @GetMapping("/space/analyze/space-user-analyze-response")
    public SpaceUserAnalyzeResponse getSpaceUserAnalyzeResponse() {
        return null;
    }

    @GetMapping("/space/analyze/space-tag-analyze-response")
    public SpaceTagAnalyzeResponse getSpaceTagAnalyzeResponse() {
        return null;
    }

    @GetMapping("/space/analyze/space-category-analyze-response")
    public SpaceCategoryAnalyzeResponse getSpaceCategoryAnalyzeResponse() {
        return null;
    }

    @GetMapping("/space/analyze/space-size-analyze-response")
    public SpaceSizeAnalyzeResponse getSpaceSizeAnalyzeResponse() {
        return null;
    }
}

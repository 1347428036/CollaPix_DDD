package com.esmiao.collapix.domain.picture.valueObject;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * Picture review status enumeration
 * @author Steven Chen
 */
@Getter
public enum PictureReviewStatusEnum {
    REVIEWING("Pending Review", 0),
    PASS("Approved", 1),
    REJECT("Rejected", 2);

    private final String text;
    private final int value;

    PictureReviewStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * Get enum by value
     */
    public static PictureReviewStatusEnum getEnumByValue(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (PictureReviewStatusEnum pictureReviewStatusEnum : PictureReviewStatusEnum.values()) {
            if (pictureReviewStatusEnum.value == value) {
                return pictureReviewStatusEnum;
            }
        }
        return null;
    }
}
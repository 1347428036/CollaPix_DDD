package com.esmiao.cloudpicture.domain.space.valueObject;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * Space level enums
 * @author Steven Chen
 */
@Getter
public enum SpaceLevelEnum {

    COMMON("Common Edition", 0, 100, 100L * 1024 * 1024),
    PROFESSIONAL("Professional Edition", 1, 1000, 1000L * 1024 * 1024),
    FLAGSHIP("Flagship Edition", 2, 10000, 10000L * 1024 * 1024);

    private final String text;

    private final int value;

    private final long maxCount;

    private final long maxSize;

    /**
     * Constructor for SpaceLevelEnum
     * @param text description of the space level
     * @param value integer value representing the space level
     * @param maxCount maximum number of images allowed
     * @param maxSize maximum total size of images allowed in bytes
     */
    SpaceLevelEnum(String text, int value, long maxCount, long maxSize) {
        this.text = text;
        this.value = value;
        this.maxCount = maxCount;
        this.maxSize = maxSize;
    }

    /**
     * Get enum by its value
     * @param value integer value of the space level
     * @return SpaceLevelEnum corresponding to the value, or null if not found
     */
    public static SpaceLevelEnum getEnumByValue(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (SpaceLevelEnum spaceLevelEnum : SpaceLevelEnum.values()) {
            if (spaceLevelEnum.value == value) {
                return spaceLevelEnum;
            }
        }
        return null;
    }
}
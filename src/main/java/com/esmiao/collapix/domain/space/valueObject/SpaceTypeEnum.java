package com.esmiao.collapix.domain.space.valueObject;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * Space type enumeration
 * @author Steven Chen
 */
@Getter
public enum SpaceTypeEnum {

    PRIVATE("Private Space", 0),
    TEAM("Team Space", 1);

    private final String text;

    private final int value;

    SpaceTypeEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * Get the enumeration based on the value
     */
    public static SpaceTypeEnum getEnumByValue(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (SpaceTypeEnum spaceTypeEnum : SpaceTypeEnum.values()) {
            if (spaceTypeEnum.value == value) {
                return spaceTypeEnum;
            }
        }
        return null;
    }
}

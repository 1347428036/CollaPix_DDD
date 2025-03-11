package com.esmiao.collapix.shared.sharding;

import com.esmiao.collapix.domain.space.constant.SpaceConstant;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

/**
 * Picture table customized sharding algorithm
 *
 * @author Steven Chen
 * @createDate 2025-02-24
 */
public class PictureShardingAlgorithm implements StandardShardingAlgorithm<Long> {

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> preciseShardingValue) {
        Long spaceId = preciseShardingValue.getValue();
        String logicTableName = preciseShardingValue.getLogicTableName();
        // spaceId is null indicate query all pictures
        if (spaceId == null || SpaceConstant.PUBLIC_SPACE_ID == spaceId) {
            return logicTableName;
        }
        // Generate dynamic table name by spaceId
        String realTableName = "picture_" + spaceId;
        if (availableTargetNames.contains(realTableName)) {
            return realTableName;
        } else {
            return logicTableName;
        }
    }

    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Long> rangeShardingValue) {
        return new ArrayList<>();
    }

    @Override
    public void init(Properties properties) {

    }
}

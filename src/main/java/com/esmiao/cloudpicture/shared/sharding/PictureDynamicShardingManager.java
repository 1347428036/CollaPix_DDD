package com.esmiao.cloudpicture.shared.sharding;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.esmiao.cloudpicture.domain.space.entity.Space;
import com.esmiao.cloudpicture.domain.space.valueObject.SpaceLevelEnum;
import com.esmiao.cloudpicture.domain.space.valueObject.SpaceTypeEnum;
import com.esmiao.cloudpicture.infrastructure.exception.BusinessException;
import com.esmiao.cloudpicture.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.cloudpicture.infrastructure.mapper.SpaceMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.driver.jdbc.core.connection.ShardingSphereConnection;
import org.apache.shardingsphere.infra.metadata.database.ShardingSphereDatabase;
import org.apache.shardingsphere.infra.metadata.version.MetaDataVersion;
import org.apache.shardingsphere.mode.manager.ContextManager;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.rule.ShardingRule;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Picture table dynamic sharding manager
 *
 * @author Steven Chen
 * @createDate 2025-02-25
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class PictureDynamicShardingManager {

    private final DataSource dataSource;

    private final SpaceMapper spaceMapper;

    private static final String LOGIC_TABLE_NAME = "picture";

    /**
     * The database name in the .properties file
     */
    private static final String DATABASE_NAME = "cloud_picture";

    @PostConstruct
    public void initialize() {
        log.info("Initialize dynamic sharding configuration...");
        updateShardingTableNodes();
    }

    /**
     * Dynamic create table for team space
     * Only create sharding table for [FALGSHIP] team space.
     */
    public void createSpacePictureTable(Space space) {
        if (SpaceTypeEnum.TEAM.getValue() == space.getSpaceType() &&
            SpaceLevelEnum.FLAGSHIP.getValue() == space.getSpaceLevel()) {

            String tableName = "picture_" + space.getId();
            // Create new table
            String createTableSql = "CREATE TABLE " + tableName + " LIKE picture";
            try {
                SqlRunner.db().update(createTableSql);
                // Update sharding table nodes
                updateShardingTableNodes();
            } catch (Exception e) {
                log.error("Failed to create a table sharding in picture space，space id = {}", space.getId());
            }
        }
    }

    /**
     * Get all dynamic table names, including the initial table picture and sharded tables picture_{spaceId}
     */
    private Set<String> fetchAllPictureTableNames() {
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id");
        queryWrapper.eq("spaceType", SpaceTypeEnum.TEAM.getValue());
        queryWrapper.eq("spaceLevel", SpaceLevelEnum.FLAGSHIP.getValue());
        List<Long> spaceIds = spaceMapper.selectObjs(queryWrapper);

        Set<String> tableNames = spaceIds.stream()
            .map(spaceId -> LOGIC_TABLE_NAME + "_" + spaceId)
            .collect(Collectors.toSet());
        // Add the default table name
        tableNames.add(LOGIC_TABLE_NAME);

        return tableNames;
    }

    /**
     * Update ShardingSphere's actual-data-nodes dynamic table name configuration
     */
    private void updateShardingTableNodes() {
        Set<String> tableNames = fetchAllPictureTableNames();
        String newActualDataNodes = tableNames.stream()
            // Ensure the prefix is legal
            .map(tableName -> DATABASE_NAME + "." + tableName)
            .collect(Collectors.joining(","));
        log.info("Loaded dynamic sharding [actual-data-nodes] config: {}", newActualDataNodes);

        ContextManager contextManager = getContextManager();
        ShardingSphereDatabase pictureDatabase = contextManager.getDatabase(DATABASE_NAME);
        Optional<ShardingRule> shardingRule = pictureDatabase.getRuleMetaData().findSingleRule(ShardingRule.class);
        if (shardingRule.isPresent()) {
            ShardingRule originalRule = shardingRule.get();
            ShardingRuleConfiguration oldShardingRuleConfig = originalRule.getConfiguration();
            List<ShardingTableRuleConfiguration> newTableRuleConfigs = createNewTableRuleConfigs(oldShardingRuleConfig.getTables(), newActualDataNodes);
            ShardingRuleConfiguration newShardingRuleConfig = createNewShardingRuleConfig(oldShardingRuleConfig, newTableRuleConfigs);

            // Save new rules to the persist repo
            Collection<MetaDataVersion> metaDataVersions = contextManager.getPersistServiceFacade()
                .getMetaDataPersistService()
                .getDatabaseRulePersistService()
                .persist(DATABASE_NAME, Collections.singleton(newShardingRuleConfig));
            // Switch active version, save new version to the persist repo
            contextManager.getPersistServiceFacade()
                .getMetaDataPersistService()
                .getMetaDataVersionPersistService()
                .switchActiveVersion(metaDataVersions);
            // Refresh meta data
            contextManager.getMetaDataContextManager().forceRefreshDatabaseMetaData(pictureDatabase);
            log.info("Update dynamic sharding rules success!");
        } else {
            log.error("Cannot find sharding rule config for ShardingSphere, update dynamic sharding failed.");
        }
    }

    private static ShardingRuleConfiguration createNewShardingRuleConfig(
        ShardingRuleConfiguration configuration,
        List<ShardingTableRuleConfiguration> newRuleConfig) {

        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.setTables(newRuleConfig);
        shardingRuleConfig.setAutoTables(configuration.getAutoTables());
        shardingRuleConfig.setBindingTableGroups(configuration.getBindingTableGroups());
        shardingRuleConfig.setDefaultDatabaseShardingStrategy(configuration.getDefaultDatabaseShardingStrategy());
        shardingRuleConfig.setDefaultTableShardingStrategy(configuration.getDefaultTableShardingStrategy());
        shardingRuleConfig.setDefaultKeyGenerateStrategy(configuration.getDefaultKeyGenerateStrategy());
        shardingRuleConfig.setDefaultAuditStrategy(configuration.getDefaultAuditStrategy());
        shardingRuleConfig.setDefaultShardingColumn(configuration.getDefaultShardingColumn());
        shardingRuleConfig.setShardingAlgorithms(configuration.getShardingAlgorithms());
        shardingRuleConfig.setKeyGenerators(configuration.getKeyGenerators());
        shardingRuleConfig.setAuditors(configuration.getAuditors());
        shardingRuleConfig.setShardingCache(configuration.getShardingCache());

        return shardingRuleConfig;
    }

    private List<ShardingTableRuleConfiguration> createNewTableRuleConfigs(
        Collection<ShardingTableRuleConfiguration> originalTables,
        String newNodes) {
        /*
         * Keep original strategies
         * */
        // Find the original table rule of picture table
        ShardingTableRuleConfiguration originalTableRule = originalTables.stream()
            .filter(t -> LOGIC_TABLE_NAME.equals(t.getLogicTable()))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "Cannot find sharding rule config for table[" + LOGIC_TABLE_NAME + "]"));
        ShardingTableRuleConfiguration newTableRule = new ShardingTableRuleConfiguration(LOGIC_TABLE_NAME, newNodes);
        newTableRule.setDatabaseShardingStrategy(originalTableRule.getDatabaseShardingStrategy());
        newTableRule.setTableShardingStrategy(originalTableRule.getTableShardingStrategy());
        newTableRule.setKeyGenerateStrategy(originalTableRule.getKeyGenerateStrategy());
        newTableRule.setAuditStrategy(originalTableRule.getAuditStrategy());

        List<ShardingTableRuleConfiguration> newTableRules = new ArrayList<>(originalTables);
        newTableRules.removeIf(table -> LOGIC_TABLE_NAME.equals(table.getLogicTable()));
        newTableRules.add(newTableRule);

        return newTableRules;
    }

    /**
     * Get [ShardingSphere ContextManager]
     */
    private ContextManager getContextManager() {
        try (ShardingSphereConnection connection = dataSource.getConnection().unwrap(ShardingSphereConnection.class)) {
            return connection.getContextManager();
        } catch (SQLException e) {
            throw new RuntimeException("Load [ShardingSphere ContextManager] failed", e);
        }
    }
}

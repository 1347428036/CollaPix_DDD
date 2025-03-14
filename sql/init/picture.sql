-- 图片表
create table if not exists picture
(
    id           bigint auto_increment comment 'id' primary key,
    url          varchar(512)                       not null comment '图片 url',
    name         varchar(128)                       not null comment '图片名称',
    introduction varchar(512)                       null comment '简介',
    category     varchar(64)                        null comment '分类',
    tags         varchar(512)                      null comment '标签（JSON 数组）',
    picSize      bigint                             null comment '图片体积',
    picWidth     int                                null comment '图片宽度',
    picHeight    int                                null comment '图片高度',
    picScale     double                             null comment '图片宽高比例',
    picFormat    varchar(32)                        null comment '图片格式',
    picColor varchar(16) null comment '图片主色调',
    thumbnailUrl varchar(512) NULL COMMENT '缩略图 url',
    originalUrl varchar(512) NULL COMMENT '原始图片 url',
    userId       bigint                             not null comment '创建用户 id',
    spaceId  bigint  null comment '空间 id（为空表示公共空间）',
    reviewStatus INT DEFAULT 0 NOT NULL COMMENT '审核状态：0-待审核; 1-通过; 2-拒绝',
    reviewMessage VARCHAR(512) NULL COMMENT '审核信息',
    reviewerId BIGINT NULL COMMENT '审核人 ID',
    reviewTime DATETIME NULL COMMENT '审核时间',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    editTime     datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    INDEX idx_name (name),                 -- 提升基于图片名称的查询性能
    INDEX idx_introduction (introduction), -- 用于模糊搜索图片简介
    INDEX idx_category (category),         -- 提升基于分类的查询性能
    INDEX idx_tags (tags),                 -- 提升基于标签的查询性能
    INDEX idx_userId (userId),              -- 提升基于用户 ID 的查询性能
    INDEX idx_spaceId (spaceId),
    INDEX idx_reviewStatus (reviewStatus)
) comment '图片' ENGINE=InnoDB CHARSET=utf8mb4 collate = utf8mb4_unicode_ci;

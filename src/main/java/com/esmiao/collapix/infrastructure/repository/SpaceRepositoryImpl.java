package com.esmiao.collapix.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esmiao.collapix.domain.space.entity.Space;
import com.esmiao.collapix.domain.space.repository.SpaceRepository;
import com.esmiao.collapix.infrastructure.mapper.SpaceMapper;
import org.springframework.stereotype.Component;


@Component
public class SpaceRepositoryImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceRepository {
}

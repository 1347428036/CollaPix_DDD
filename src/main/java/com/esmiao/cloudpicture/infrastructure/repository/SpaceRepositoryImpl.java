package com.esmiao.cloudpicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esmiao.cloudpicture.domain.space.entity.Space;
import com.esmiao.cloudpicture.domain.space.repository.SpaceRepository;
import com.esmiao.cloudpicture.infrastructure.mapper.SpaceMapper;
import org.springframework.stereotype.Component;


@Component
public class SpaceRepositoryImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceRepository {
}

package com.esmiao.collapix.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esmiao.collapix.domain.space.entity.SpaceUser;
import com.esmiao.collapix.domain.space.repository.SpaceUserRepository;
import com.esmiao.collapix.infrastructure.mapper.SpaceUserMapper;
import org.springframework.stereotype.Component;


@Component
public class SpaceUserRepositoryImpl extends ServiceImpl<SpaceUserMapper, SpaceUser> implements SpaceUserRepository {
}

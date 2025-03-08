package com.esmiao.cloudpicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esmiao.cloudpicture.domain.space.entity.SpaceUser;
import com.esmiao.cloudpicture.domain.space.repository.SpaceUserRepository;
import com.esmiao.cloudpicture.infrastructure.mapper.SpaceUserMapper;
import org.springframework.stereotype.Component;


@Component
public class SpaceUserRepositoryImpl extends ServiceImpl<SpaceUserMapper, SpaceUser> implements SpaceUserRepository {
}

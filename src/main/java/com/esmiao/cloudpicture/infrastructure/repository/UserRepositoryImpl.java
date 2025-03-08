package com.esmiao.cloudpicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esmiao.cloudpicture.domain.user.entity.User;
import com.esmiao.cloudpicture.domain.user.reposiitory.UserRepository;
import com.esmiao.cloudpicture.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class UserRepositoryImpl extends ServiceImpl<UserMapper, User> implements UserRepository {
}

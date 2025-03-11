package com.esmiao.collapix.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esmiao.collapix.domain.user.entity.User;
import com.esmiao.collapix.domain.user.reposiitory.UserRepository;
import com.esmiao.collapix.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class UserRepositoryImpl extends ServiceImpl<UserMapper, User> implements UserRepository {
}

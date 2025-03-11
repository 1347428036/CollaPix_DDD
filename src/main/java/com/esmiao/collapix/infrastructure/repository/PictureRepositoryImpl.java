package com.esmiao.collapix.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esmiao.collapix.domain.picture.entity.Picture;
import com.esmiao.collapix.domain.picture.repository.PictureRepository;
import com.esmiao.collapix.infrastructure.mapper.PictureMapper;
import org.springframework.stereotype.Component;

@Component
public class PictureRepositoryImpl extends ServiceImpl<PictureMapper, Picture> implements PictureRepository {
}

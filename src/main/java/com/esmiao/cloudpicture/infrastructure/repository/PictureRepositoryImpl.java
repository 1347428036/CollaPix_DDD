package com.esmiao.cloudpicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esmiao.cloudpicture.domain.picture.entity.Picture;
import com.esmiao.cloudpicture.domain.picture.repository.PictureRepository;
import com.esmiao.cloudpicture.infrastructure.mapper.PictureMapper;
import org.springframework.stereotype.Component;

@Component
public class PictureRepositoryImpl extends ServiceImpl<PictureMapper, Picture> implements PictureRepository {
}

package com.esmiao.cloudpicture.infrastructure.api.ai.aliyun;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.esmiao.cloudpicture.infrastructure.api.ai.aliyun.model.CreateOutPaintingTaskRequest;
import com.esmiao.cloudpicture.infrastructure.api.ai.aliyun.model.CreateOutPaintingTaskResponse;
import com.esmiao.cloudpicture.infrastructure.api.ai.aliyun.model.OutPaintingTaskStatusResponse;
import com.esmiao.cloudpicture.infrastructure.exception.BusinessException;
import com.esmiao.cloudpicture.infrastructure.exception.ErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Ali Yun AI API implementation
 * @author Steven Chen
 */
@Slf4j
@Component
public class AliYunAiApi {

    // Read configuration file
    @Value("${aliYunAi.apiKey}")
    private String apiKey;

    // Create task URL
    public static final String CREATE_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/image2image/out-painting";

    // Query task status
    public static final String GET_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/tasks/%s";

    /**
     * Create task
     */
    public CreateOutPaintingTaskResponse createOutPaintingTask(CreateOutPaintingTaskRequest createOutPaintingTaskRequest) {
        if (createOutPaintingTaskRequest == null) {
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "Out-painting parameters are empty");
        }
        // Send request
        HttpRequest httpRequest = HttpRequest.post(CREATE_OUT_PAINTING_TASK_URL)
            .header(Header.AUTHORIZATION, "Bearer " + apiKey)
            // Must enable asynchronous processing.
            .header("X-DashScope-Async", "enable")
            .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
            .body(JSONUtil.toJsonStr(createOutPaintingTaskRequest));
        try (HttpResponse httpResponse = httpRequest.execute()) {
            if (!httpResponse.isOk()) {
                log.error("Ali AI request exception: {}", httpResponse.body());
                throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "AI out-painting failed");
            }
            CreateOutPaintingTaskResponse response = JSONUtil.toBean(httpResponse.body(), CreateOutPaintingTaskResponse.class);
            String errorCode = response.getCode();
            if (StrUtil.isNotBlank(errorCode)) {
                log.error("AI out-painting failed, errorCode:{}, errorMessage:{}", errorCode, response.getMessage());
                throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "AI out-painting interface response exception");
            }
            return response;
        }
    }

    /**
     * Query created task
     */
    public OutPaintingTaskStatusResponse getOutPaintingTask(String taskId) {
        if (StrUtil.isBlank(taskId)) {
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "Task id cannot be empty");
        }

        HttpRequest httpRequest = HttpRequest.get(String.format(GET_OUT_PAINTING_TASK_URL, taskId))
            .header(Header.AUTHORIZATION, "Bearer " + apiKey);
        try (HttpResponse httpResponse = httpRequest.execute()) {
            if (!httpResponse.isOk()) {
                throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "Failed to get task status");
            }

            return JSONUtil.toBean(httpResponse.body(), OutPaintingTaskStatusResponse.class);
        }
    }
}
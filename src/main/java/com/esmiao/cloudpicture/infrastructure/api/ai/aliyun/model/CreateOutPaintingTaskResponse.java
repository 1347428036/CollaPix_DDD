package com.esmiao.cloudpicture.infrastructure.api.ai.aliyun.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ali Yun create out painting task response body
 * @author Steven Chen
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOutPaintingTaskResponse {

    private Output output;

    /**
     * Indicates the output information of the task
     */
    @Data
    public static class Output {

        /**
         * Task ID
         */
        private String taskId;

        /**
         * Task status
         * <ul>
         *     <li>PENDING：Queuing</li>
         *     <li>RUNNING：Processing</li>
         *     <li>SUSPENDED：Suspended</li>
         *     <li>SUCCEEDED：Succeeded</li>
         *     <li>FAILED：Failed</li>
         *     <li>UNKNOWN：Task does not exist or status is unknown</li>
         * </ul>
         */
        private String taskStatus;
    }

    /**
     * Interface error code.
     * <p>This parameter will not be returned if the request is successful.</p>
     */
    private String code;

    /**
     * Interface error message.
     * <p>This parameter will not be returned if the request is successful.</p>
     */
    private String message;

    /**
     * Unique request identifier.
     * <p>Can be used for request detail tracing and problem troubleshooting.</p>
     */
    private String requestId;

}
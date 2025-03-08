package com.esmiao.cloudpicture.infrastructure.api.ai.aliyun.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ali Yun out painting task status response body.
 * @author Steven Chen
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutPaintingTaskStatusResponse {

    /**
     * Request unique identifier
     */
    private String requestId;

    /**
     * Output information
     */
    private Output output;

    /**
     * Represents the output information of the task
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
         *     <li>PENDING：Queued</li>
         *     <li>RUNNING：Processing</li>
         *     <li>SUSPENDED：Suspended</li>
         *     <li>SUCCEEDED：Executed successfully</li>
         *     <li>FAILED：Execution failed</li>
         *     <li>UNKNOWN：Task does not exist or status is unknown</li>
         * </ul>
         */
        private String taskStatus;

        /**
         * Submission time
         * Format：YYYY-MM-DD HH:mm:ss.SSS
         */
        private String submitTime;

        /**
         * Scheduling time
         * Format：YYYY-MM-DD HH:mm:ss.SSS
         */
        private String scheduledTime;

        /**
         * End time
         * Format：YYYY-MM-DD HH:mm:ss.SSS
         */
        private String endTime;

        /**
         * URL of the output image
         */
        private String outputImageUrl;

        /**
         * Interface error code
         * <p>This parameter will not be returned for successful requests</p>
         */
        private String code;

        /**
         * Interface error message
         * <p>This parameter will not be returned for successful requests</p>
         */
        private String message;

        /**
         * Task metrics information
         */
        private TaskMetrics taskMetrics;
    }

    /**
     * Represents the metrics information of the task
     */
    @Data
    public static class TaskMetrics {

        /**
         * Total number of tasks
         */
        private Integer total;

        /**
         * Number of successful tasks
         */
        private Integer succeeded;

        /**
         * Number of failed tasks
         */
        private Integer failed;
    }
}
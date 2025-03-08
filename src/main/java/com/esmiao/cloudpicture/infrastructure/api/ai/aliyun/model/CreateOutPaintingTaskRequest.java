package com.esmiao.cloudpicture.infrastructure.api.ai.aliyun.model;

import cn.hutool.core.annotation.Alias;
import com.esmiao.cloudpicture.interfaces.dto.picture.PictureOutPaintingRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Ali Yun image out painting request body
 * @author Steven Chen
 */
@Data
public class CreateOutPaintingTaskRequest implements Serializable {

    /**
     * Model, e.g., "image-out-painting"
     */
    private String model = "image-out-painting";

    /**
     * Input image information
     */
    private Input input;

    /**
     * Image processing parameters
     */
    private Parameters parameters;

    public static CreateOutPaintingTaskRequest of(Input input, PictureOutPaintingRequest request) {
        CreateOutPaintingTaskRequest taskRequest = new CreateOutPaintingTaskRequest();
        taskRequest.setInput(input);
        taskRequest.setParameters(request.getParameters());

        return taskRequest;
    }

    @Data
    public static class Input {
        /**
         * Required, image URL
         */
        @Alias("image_url")
        private String imageUrl;
    }

    @Data
    public static class Parameters implements Serializable {
        /**
         * Optional, counterclockwise rotation angle, default value 0, range [0, 359]
         */
        private Integer angle;

        /**
         * Optional, output image aspect ratio, default empty string, no aspect ratio set
         * Possible values: ["", "1:1", "3:4", "4:3", "9:16", "16:9"]
         */
        @Alias("output_ratio")
        private String outputRatio;

        /**
         * Optional, horizontal scale factor when centering the image, default value 1.0, range [1.0, 3.0]
         */
        @Alias("x_scale")
        @JsonProperty("xScale")
        private Float xScale;

        /**
         * Optional, vertical scale factor when centering the image, default value 1.0, range [1.0, 3.0]
         */
        @Alias("y_scale")
        @JsonProperty("yScale")
        private Float yScale;

        /**
         * Optional, number of pixels to add above the image, default value 0
         */
        @Alias("top_offset")
        private Integer topOffset;

        /**
         * Optional, number of pixels to add below the image, default value 0
         */
        @Alias("bottom_offset")
        private Integer bottomOffset;

        /**
         * Optional, number of pixels to add to the left of the image, default value 0
         */
        @Alias("left_offset")
        private Integer leftOffset;

        /**
         * Optional, number of pixels to add to the right of the image, default value 0
         */
        @Alias("right_offset")
        private Integer rightOffset;

        /**
         * Optional, enable best quality mode, default value false
         * If true, processing time will increase significantly
         */
        @Alias("best_quality")
        private Boolean bestQuality;

        /**
         * Optional, limit the size of the generated image file, default value true
         * - Side length <= 10000: output image file size limit 5MB
         * - Side length > 10000: output image file size limit 10MB
         */
        @Alias("limit_image_size")
        private Boolean limitImageSize;

        /**
         * Optional, add "Generated by AI" watermark, default value false
         */
        @Alias("add_watermark")
        private Boolean addWatermark = false;
    }
}
package com.esmiao.collapix.infrastructure.api.imagesearch.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Search image result
 * @author Steven Chen
 */
@Data
public class ImageSearchResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * Thumbnail url
     */
    @Schema(description = "缩略图url")
    private String thumbUrl;

    /**
     * The url where the image is located
     */
    @Schema(description = "trest")
    private String fromUrl;
}

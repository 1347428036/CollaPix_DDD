package com.esmiao.cloudpicture.shared.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Websocket message of editing picture
 *
 * @author Steven Chen
 * @createDate 2025-02-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureEditRequestMessage {

    /**
     * Message type，like "ENTER_EDIT", "EXIT_EDIT", "EDIT_ACTION"
     */
    private String type;

    /**
     * Editing operation
     */
    private String editAction;
}

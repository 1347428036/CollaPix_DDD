package com.esmiao.cloudpicture.shared.websocket.model;

import com.esmiao.cloudpicture.interfaces.vo.user.UserVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Server side message of editing picture
 *
 * @author Steven Chen
 * @createDate 2025-02-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureEditResponseMessage {

    /**
     * Message type，like "INFO", "ERROR", "ENTER_EDIT", "EXIT_EDIT", "EDIT_ACTION"
     */
    private String type;

    /**
     * Message content
     */
    private String message;

    /**
     * Editing operation
     */
    private String editAction;

    /**
     * User info who is editing the picture
     */
    private UserVo user;
}

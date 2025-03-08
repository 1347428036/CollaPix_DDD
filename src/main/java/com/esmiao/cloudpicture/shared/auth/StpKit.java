package com.esmiao.cloudpicture.shared.auth;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;

/**
 * StpLogic facade class, managing all StpLogic account systems in the project.
 * The purpose of adding the @Component annotation is to ensure that static properties DEFAULT and SPACE are initialized.
 *
 * @author Steven Chen
 * @createDate 2025-02-22
 */
@Component
public class StpKit {

    public static final String SPACE_TYPE = "space";

    /**
     * Default native session object, currently not used in the project.
     */
    public static final StpLogic DEFAULT = StpUtil.stpLogic;

    /**
     * Space session object, managing login and permission authentication for all accounts in the Space table.
     */
    public static final StpLogic SPACE = new StpLogic(SPACE_TYPE);
}

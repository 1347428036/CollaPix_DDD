package com.esmiao.collapix.infrastructure.common;

import lombok.Getter;

/**
 * A common base request for page request
 * @author Steven Chen
 */
@Getter
public class PageRequest {

    /**
     * Current page number
     * */
    int current;
    /**
     * The page size
     * */
    int pageSize;
    /**
     * The field for sorting
     * */
    String sortField;
    /**
     * The order of sorting.Default is "desc".
     * */
    String sortOrder = "desc";
}

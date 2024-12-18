package com.bizzan.bitrade.util;

import com.bizzan.bitrade.pagenation.EntityPage;
import org.springframework.data.domain.Page;

public class PageUtil {

    public static EntityPage page(Page page, int pageNo, int pageSize) {
        EntityPage entityPage = new EntityPage();
        entityPage.setCount(page.getTotalElements());
        entityPage.setTotalPage(page.getTotalPages());
        entityPage.setList(page.getContent());
        entityPage.setCurrentPage(pageNo);
        entityPage.setPageSize(pageSize);
        return entityPage;
    }
}

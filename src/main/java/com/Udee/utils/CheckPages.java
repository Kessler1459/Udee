package com.Udee.utils;

import com.Udee.exception.PageException;

public class CheckPages {
    /**
     * Check if the selected page is valid
     */
    public static boolean checkPages(Integer totalPages, Integer selectedPage) {
        if (selectedPage != 0 && totalPages - 1 < selectedPage) {
            throw new PageException(selectedPage, totalPages - 1);
        } else return true;
    }
}

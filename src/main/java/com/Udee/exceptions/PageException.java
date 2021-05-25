package com.Udee.exceptions;

public class PageException extends RuntimeException {
    public PageException(Integer selectedPage, Integer maxPage) {
        super("Page " + selectedPage + " does not exist, the last page is " + (maxPage < 0 ? 0 : maxPage));
    }
}

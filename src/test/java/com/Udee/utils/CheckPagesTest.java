package com.Udee.utils;

import com.Udee.exception.PageException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CheckPagesTest {

    @Test
    void testCheckPagesTotal0GreaterSelected() {
        Assertions.assertDoesNotThrow(() ->CheckPages.checkPages(0, 1));
    }

    @Test
    void testCheckPagesTotal0LessSelected() {
        Assertions.assertDoesNotThrow(() ->CheckPages.checkPages(0, -1));
    }

    @Test
    void testCheckPagesTotal0() {
        Assertions.assertDoesNotThrow(() ->CheckPages.checkPages(0, 0));
    }

    @Test
    void testCheckPagesTotalNot0NotThrows() {
        Assertions.assertThrows(PageException.class,() ->CheckPages.checkPages(1, 1));
    }

    @Test
    void testCheckPagesLessThan0Throws() {
        Assertions.assertThrows(PageException.class,() ->CheckPages.checkPages(1, -1));
    }

    @Test
    void testCheckPagesGreaterSelectedThrows() {
        Assertions.assertThrows(PageException.class,() ->CheckPages.checkPages(1, 2));
    }
}


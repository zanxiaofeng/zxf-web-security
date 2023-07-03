package com.zxf.example.document.checker;

import java.io.BufferedInputStream;

public interface DocumentChecker {
    boolean isSafe(BufferedInputStream inputStream, String fileName) throws Exception;
}

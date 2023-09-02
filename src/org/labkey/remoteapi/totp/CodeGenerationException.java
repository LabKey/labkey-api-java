/*
 * Copyright (c) 2019 Sam Stevens
 *
 * Licensed under the MIT License
 *
 * https://github.com/samdjstevens v1.7.1
 */
package org.labkey.remoteapi.totp;

public class CodeGenerationException extends Exception {
    public CodeGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}

/*
 * Copyright (c) 2019 Sam Stevens
 *
 * Licensed under the MIT License
 *
 * https://github.com/samdjstevens v1.7.1
 */
package org.labkey.remoteapi.totp;

import java.time.Instant;

public class TimeProvider {
    public long getTime() throws RuntimeException {
        return Instant.now().getEpochSecond();
    }
}

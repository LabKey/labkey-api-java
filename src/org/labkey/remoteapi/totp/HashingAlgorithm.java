/*
 * Copyright (c) 2019 Sam Stevens
 *
 * Licensed under the MIT License
 *
 * https://github.com/samdjstevens v1.7.1
 */

package org.labkey.remoteapi.totp;

public enum HashingAlgorithm {

    SHA1("HmacSHA1", "SHA1"), //default
    SHA256("HmacSHA256", "SHA256"),
    SHA512("HmacSHA512", "SHA512");

    private final String hmacAlgorithm;
    private final String friendlyName;

    HashingAlgorithm(String hmacAlgorithm, String friendlyName) {
        this.hmacAlgorithm = hmacAlgorithm;
        this.friendlyName = friendlyName;
    }

    public String getHmacAlgorithm() {
        return hmacAlgorithm;
    }

    public String getFriendlyName() {
        return friendlyName;
    }
}

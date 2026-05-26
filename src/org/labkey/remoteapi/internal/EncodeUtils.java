/*
 * Copyright (c) 2023-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.labkey.remoteapi.internal;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncodeUtils
{
    // Text that is first urlencoded to flatten UNICODE to ASCII then base64 encoded to avoid WAF rejection
    public static final String WAF_PREFIX = "/*{{base64/x-www-form-urlencoded/wafText}}*/";

    /**
     * Obfuscates content that's often intercepted by web application firewalls that are scanning for likely SQL or
     * script injection. We have a handful of endpoints that intentionally accept SQL or script, so we encode the text
     * to avoid tripping alarms. It's a simple BASE64 encoding that obscures the content, and lets the WAF scan for and
     * reject malicious content on all other parameters. See issue 48509 and PageFlowUtil.wafEncode()/wafDecode().
     */
    public static String wafEncode(String plain)
    {
        if (null == plain || plain.isBlank())
        {
            return null;
        }
        var step1 = encodeURIComponent(plain);
        var step2 = step1.getBytes(StandardCharsets.US_ASCII);
        return WAF_PREFIX + Base64.getEncoder().encodeToString(step2);
    }

    /**
     * URL Encode string.
     * NOTE! this should be used on parts of a url, not an entire url
     * Like JavaScript encodeURIComponent()
     */
    public static String encodeURIComponent(String s)
    {
        if (null == s)
            return "";
        String enc = URLEncoder.encode(s, StandardCharsets.UTF_8);
        return enc.replace("+", "%20");
    }
}

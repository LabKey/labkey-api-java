/*
 * Copyright (c) 2009-2016 LabKey Corporation
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
package org.labkey.remoteapi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetrcFileParser
{
    private final static Log LOG = LogFactory.getLog(NetrcFileParser.class);

    public NetrcEntry getEntry(String host) throws IOException
    {
        NetrcEntry entry = getEntry(".netrc", host);

        if (null != entry)
            return entry;

        return getEntry("_netrc", host);
    }

    private static final Pattern netrcPattern = Pattern.compile("machine\\s+(.+?)\\s+login\\s+(.+?)\\s+password\\s+(.+?)\\s+", Pattern.MULTILINE);

    private NetrcEntry getEntry(String netrcName, String host) throws IOException
    {
        File netrcFile = new File(System.getProperty("user.home") + "/" + netrcName);
        return getEntry(netrcFile, host);
    }

    // Note: Also called by test PasswordUtil
    public NetrcEntry getEntry(File netrcFile, String host) throws IOException
    {
        NetrcEntry entry = null;
        LOG.info("Attempting to access " + netrcFile);

        if (netrcFile.exists())
        {
            LOG.info(netrcFile + " was found");
            LOG.info("Attempting to find an entry in " + netrcFile + " for host \"" + host + "\"");

            try (BufferedReader input = new BufferedReader(new FileReader(netrcFile, StandardCharsets.UTF_8)))
            {
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = input.readLine()) != null)
                {
                    sb.append(line).append('\n');
                }

                Matcher m = netrcPattern.matcher(sb);

                int index = 0;

                while (m.find(index))
                {
                    if (3 == m.groupCount() && m.group(1).equals(host))
                    {
                        entry = new NetrcEntry(m.group(1), m.group(2), m.group(3));
                        break;
                    }

                    index = m.end();
                }
            }

            LOG.info("Entry corresponding to host \"" + host + "\" was " +  (null == entry ? "not " : "") + "found in " +
                netrcFile + (null == entry ? "" : "; the login and password fields of this entry will be used to attempt authentication."));
        }
        else
        {
            LOG.info(netrcFile + " was not found");
        }

        return entry;
    }

    public static class NetrcEntry
    {
        private final String _machine;
        private final String _login;
        private final String _password;

        private NetrcEntry(String machine, String login, String password)
        {
            _machine = machine;
            _login = login;
            _password = password;
        }

        public String getMachine()
        {
            return _machine;
        }

        public String getLogin()
        {
            return _login;
        }

        public String getPassword()
        {
            return _password;
        }
    }
}

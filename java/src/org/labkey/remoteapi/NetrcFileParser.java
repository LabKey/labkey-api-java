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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: adam
 * Date: Jan 10, 2009
 * Time: 6:30:14 PM
 */
public class NetrcFileParser
{
    public NetrcEntry getEntry(String host) throws IOException
    {
        NetrcEntry entry = getEntry(".netrc", host);

        if (null != entry)
            return entry;

        return getEntry("_netrc", host);
    }

    private static Pattern netrcPattern = Pattern.compile("machine\\s+(.+?)\\s+login\\s+(.+?)\\s+password\\s+(.+?)\\s+", Pattern.MULTILINE);

    private NetrcEntry getEntry(String netrcName, String host) throws IOException
    {
        File netrcFile = new File(System.getProperty("user.home") + "/" + netrcName);
        return getEntry(netrcFile, host);
    }

    public  NetrcEntry getEntry(File netrcFile, String host) throws IOException
    {
        if (!netrcFile.exists())
            return null;

        BufferedReader input = null;

        try
        {
            StringBuilder sb = new StringBuilder();
            input = new BufferedReader(new FileReader(netrcFile));
            String line;

            while ((line = input.readLine()) != null)
            {
                sb.append(line).append('\n');
            }

            Matcher m = netrcPattern.matcher(sb);

            int index = 0;

            while(m.find(index))
            {
                if (3 == m.groupCount() && m.group(1).equals(host))
                    return new NetrcEntry(m.group(1), m.group(2), m.group(3));

                index = m.end();
            }
        }
        finally
        {
            try
            {
                if (input != null)
                {
                    input.close();
                }
            }
            catch (IOException ioe)
            {
                // ignore
            }
        }

        return null;
    }

    public class NetrcEntry
    {
        private String _machine;
        private String _login;
        private String _password;

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

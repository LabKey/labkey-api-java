/*
 * Copyright (c) 2011-2017 LabKey Corporation
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
package org.labkey.remoteapi.test;

import org.labkey.remoteapi.Connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to parse a .properties file and produce reasonable exceptions if required properties aren't present.
 */
public class ClientConfig
{
    private Properties _properties;
    public static final String CONFIG_ARG_PREFIX = "-config=";

    /**
     * Defaults to looking in the working directory for a config.properties file. Can be pointed to another file
     * if one of the arguments is -config=&lt;PATH TO CONFIG FILE&gt;
     *
     * Looks for a "debug" property - if set to true, enables verbose debug logging for the HTTP connection 
     *
     * @param args command-line arguments to the program
     * @throws IOException if there is an IO problem reading the config.properties file
     */
    public ClientConfig(String args[]) throws IOException
    {
        String propertiesPath = "config.properties";
        if(args.length > 0 && args[0].startsWith(CONFIG_ARG_PREFIX))
            propertiesPath = args[0].substring(CONFIG_ARG_PREFIX.length());
        File propertiesFile = new File(propertiesPath);
        if(!propertiesFile.exists())
            throw new IllegalArgumentException((new StringBuilder()).append("Could not find config file ").append(propertiesPath).toString());

        _properties = new Properties();

        InputStream in = new FileInputStream(propertiesFile);
        try
        {
            _properties.load(in);
        }
        finally
        {
            in.close();
        }

        if("true".equalsIgnoreCase(getProperty("debug", "false")))
        {
            System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
            System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
            System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "debug");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");
        }
    }

    public String getProperty(String name, String defaultValue)
    {
        return _properties.getProperty(name, defaultValue);
    }

    /**
     * Gets a property of the given name. 
     * @param name the name of the property
     * @return the property value, or null if the property is not found
     */
    public String getProperty(String name)
    {
        String value = _properties.getProperty(name);
        if(value == null)
            throw new IllegalArgumentException((new StringBuilder()).append("No '").append(name).append("' value specified in config file").toString());
        else
            return value;
    }

    /**
     * Creates a connection object based on the baseServerURL, username, and password config properties.
     * All are required.
     * @return a connection object
     */
    public Connection createConnection()
    {
        String baseServerURL = getProperty("baseServerURL");
        String username = getProperty("username");
        String password = getProperty("password");
        return new Connection(baseServerURL, username, password);
    }
}

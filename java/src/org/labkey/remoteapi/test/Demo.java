/*
 * Copyright (c) 2008-2013 LabKey Corporation
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

import org.labkey.remoteapi.query.*;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;

import java.util.Map;

public class Demo
{
    public void doDemo() throws Exception
    {
        simpleSelectDemo();
        sortFilterDemo();
        executeSqlDemo();
        getWebPartDemo();
    }

    public void simpleSelectDemo() throws Exception
    {
        System.out.println("-----------------------------------------------");
        System.out.println("Simple Select Demo");
        System.out.println("-----------------------------------------------");

        //create a new connection, specifying base URL,
        //user email, and password
        Connection cn = new Connection("http://localhost:8080/labkey", _email, _password);

        //create a SelectRowsCommand to call the selectRows.api
        SelectRowsCommand cmd = new SelectRowsCommand("lists", "People");

        //execute the command against the connection
        //within the Api Test project folder
        SelectRowsResponse resp = cmd.execute(cn, "Api Test");

        System.out.println(resp.getRowCount() + " rows were returned.");

        //loop over the returned rows
        for(Map<String,Object> row : resp.getRows())
        {
            System.out.println(row.get("FirstName") + " is " + row.get("Age"));
        }
    }

    public void sortFilterDemo() throws Exception
    {
        System.out.println("-----------------------------------------------");
        System.out.println("Sort/Filter Demo");
        System.out.println("-----------------------------------------------");

        Connection cn = getConnection();

        SelectRowsCommand cmd = new SelectRowsCommand("lists", "People");

        //sort by age descending
        cmd.addSort("Age", Sort.Direction.DESCENDING);

        //filter for ages between 30 and 50
        cmd.addFilter("Age", 30, Filter.Operator.GTE);
        cmd.addFilter("Age", 50, Filter.Operator.LTE);
        
        SelectRowsResponse resp = cmd.execute(cn, "Api Test");

        for(Map<String,Object> row : resp.getRows())
        {
            System.out.println(row.get("FirstName") + " is " + row.get("Age"));
        }
    }

    public void executeSqlDemo() throws Exception
    {
        System.out.println("-----------------------------------------------");
        System.out.println("Execute SQL Demo");
        System.out.println("-----------------------------------------------");

        Connection cn = getConnection();

        ExecuteSqlCommand cmd = new ExecuteSqlCommand("lists");
        cmd.setSql("select avg(People.Age) as AvgAge from People");
        cmd.setTimeout(0);

        SelectRowsResponse resp = cmd.execute(cn, "Api Test");

        System.out.println("Average Age is " + resp.getRows().get(0).get("AvgAge"));
    }

    public void getWebPartDemo() throws Exception
    {
        System.out.println("-----------------------------------------------");
        System.out.println("Get Web Part Demo");
        System.out.println("-----------------------------------------------");

        Connection cn = getConnection();
        Command cmd = new Command("project", "getWebPart");
        cmd.getParameters().put("webpart.name", "Wiki");
        cmd.getParameters().put("name", "home");

        CommandResponse resp = cmd.execute(cn, "Api Test");
        System.out.println(resp.getText());
    }






    

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args) throws Exception
    {
        Demo d = new Demo(args);
        d.doDemo();
    }

    public Demo(String[] args) throws Exception
    {
        processArgs(args);
        if(null == _email || null == _password)
            throw new Exception("Usage: java demo.class <user> <password>");
    }

    private void processArgs(String[] args)
    {
        if(args.length >= 2)
        {
            _email = args[0];
            _password = args[1];
        }
    }

    private Connection getConnection()
    {
        if(null == _connection)
            _connection = new Connection("http://localhost:8080/labkey", _email, _password);
        return _connection;
    }

    private String _email = null;
    private String _password = null;
    private Connection _connection = null;
}
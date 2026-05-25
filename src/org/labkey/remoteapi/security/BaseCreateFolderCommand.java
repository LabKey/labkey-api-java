/*
 * Copyright (c) 2022-2026 LabKey Corporation
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
package org.labkey.remoteapi.security;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.PostCommand;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseCreateFolderCommand<COMMAND extends BaseCreateFolderCommand<?>> extends PostCommand<CommandResponse>
{
    private String _name;
    private String _folderType;
    private String _templateSourceId;
    private boolean _templateIncludeSubfolders = false;
    private List<String> _templateWriterTypes = List.of();

    protected BaseCreateFolderCommand(String actionName)
    {
        super("admin", actionName);
    }

    abstract COMMAND getThis();

    public COMMAND setName(String name)
    {
        _name = name;
        return getThis();
    }

    public COMMAND setFolderType(String folderType)
    {
        _folderType = folderType;
        return getThis();
    }

    public COMMAND setTemplateSourceId(String templateSourceId)
    {
        _templateSourceId = templateSourceId;
        return getThis();
    }

    public COMMAND setTemplateIncludeSubfolders(boolean templateIncludeSubfolders)
    {
        _templateIncludeSubfolders = templateIncludeSubfolders;
        return getThis();
    }

    public COMMAND setTemplateWriterTypes(String... templateWriterTypes)
    {
        _templateWriterTypes = List.of(templateWriterTypes);
        return getThis();
    }

    protected List<BasicNameValuePair> getPostData()
    {
        List<BasicNameValuePair> postData = new ArrayList<>();
        postData.add(new BasicNameValuePair("name", _name));
        postData.add(new BasicNameValuePair("folderType", _folderType));

        if ("Template".equals(_folderType))
        {
            postData.add(new BasicNameValuePair("templateSourceId", _templateSourceId));
            postData.add(new BasicNameValuePair("templateIncludeSubfolders", Boolean.toString(_templateIncludeSubfolders)));
            _templateWriterTypes.forEach(type -> postData.add(new BasicNameValuePair("templateWriterTypes", type)));
        }

        return postData;
    }

    @Override
    protected HttpPost createRequest(URI uri)
    {
        // CreateFolderAction and CreateProjectAction are not real API actions, so we POST form data instead of JSON
        HttpPost request = new HttpPost(uri);
        request.setEntity(new UrlEncodedFormEntity(getPostData()));
        return request;
    }
}

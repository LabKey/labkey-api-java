/*
 * Copyright (c) 2009-2010 LabKey Corporation
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

import org.labkey.remoteapi.PostCommand;
import org.json.simple.JSONObject;

/*
* User: dave
* Date: Sep 28, 2009
* Time: 2:40:43 PM
*/

/**
 * Create a new user account on the server.
 */
public class CreateUserCommand extends PostCommand<CreateUserResponse>
{
    private String _email;
    private boolean _sendEmail;

    public CreateUserCommand(String email)
    {
        super("security", "createNewUser");
        _email = email;
    }

    public CreateUserCommand(CreateUserCommand source)
    {
        super(source);
        _email = source.getEmail();
        _sendEmail = source.isSendEmail();
    }

    public String getEmail()
    {
        return _email;
    }

    /**
     * Set the email address for the new user.
     * @param email The user's email address
     */
    public void setEmail(String email)
    {
        _email = email;
    }

    public boolean isSendEmail()
    {
        return _sendEmail;
    }

    /**
     * Set to true to send email to the user with login instructions, false to skip email.
     * @param sendEmail True to send email, false to skip.
     */
    public void setSendEmail(boolean sendEmail)
    {
        _sendEmail = sendEmail;
    }

    @Override
    protected CreateUserResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new CreateUserResponse(text, status, contentType, json, this);
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject obj = new JSONObject();
        obj.put("email", getEmail());
        obj.put("sendEmail", isSendEmail());
        return obj;
    }

    @Override
    public PostCommand copy()
    {
        return new CreateUserCommand(this);
    }
}

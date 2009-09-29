/*
 * Copyright (c) 2009 LabKey Corporation
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

/*
* User: dave
* Date: Sep 28, 2009
* Time: 3:19:23 PM
*/

/**
 * Adds a set of principals to a security group.
 * This command has no meaningful response--if it executed without exception, the changes were applied.
 */
public class AddGroupMembersCommand extends GroupMembersCommand
{
    public AddGroupMembersCommand(int groupId)
    {
        super("addGroupMember", groupId);
    }
}

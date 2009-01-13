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
package org.labkey.remoteapi.sas;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * User: adam
 * Date: Jan 10, 2009
 * Time: 11:40:48 PM
 */

/*
    This classes stashes objects of the specified class and allows retrieval at a later time.  A random key is issued
    when the object is stashed; the key must be provided to retrieve the object later.  This keeps stashed data private
    in a multi-threaded environment.  All entries that are older than the specified expiration time are removed on every
    call to put() and get().  This ensures that we're only keeping active data.

    We need this because SAS cannot maintain a reference to a Java object across two DATA STEP commands.  Our macros
    use two DATA STEP commands: the first to issue the query & handle the meta data and the second to handle the data
    itself.  The Stash allows us to use the same query & response across these two steps.
 */
class Stash<V>
{
    private final LinkedList<Entry<V>> _list = new LinkedList<Entry<V>>();
    private final long _expires;

    // timeLimit: Maximum time an entry is allowed to live, in milliseconds 
    Stash(long expires)
    {
        _expires = expires;
    }

    String put(V value)
    {
        synchronized(_list)
        {
            String key;
            boolean keyExists;

            do
            {
                keyExists = false;

                // Create a stash key.  The stash may get shared by multiple SAS sessions, all with different permissions,
                // so the key must be random and difficult to guess; if not, a user could attempt to retrieve stashed data
                // they are not authorized to see.  An eight-character key results in over 200 trillion possibilities,
                // which seems sufficient.  Paranoid: when creating the key, loop until we generate an unused key.
                key = generateRandomKey(8);

                ListIterator<Entry<V>> li = _list.listIterator();

                // Loop to clear out all the expired entries and ensure the key isn't already used.
                while (li.hasNext())
                {
                    Entry<V> e = li.next();

                    if (e.isExpired())
                    {
                        System.out.println("Removing " + e);
                        li.remove();
                    }
                    else if (e.getKey().equals(key))
                    {
                        keyExists = true;
                    }
                }
            }
            while (keyExists);

            _list.add(new Entry<V>(key, value));

            return key;
        }
    }

    V get(String id)
    {
        synchronized(_list)
        {
            V value = null;

            ListIterator<Entry<V>> li = _list.listIterator();

            // Loop to find the requested entry and clear out all the expired entries
            while (li.hasNext())
            {
                Entry<V> e = li.next();

                if (e.getKey().equals(id))
                {
                    value = e.getValue();
                    li.remove();
                }
                else if (e.isExpired())
                {
                    System.out.println("Removing " + e);
                    li.remove();
                }
            }

            return value;
        }
    }

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static String generateRandomKey(int length)
    {
        StringBuilder key = new StringBuilder(length);

        for (int i = 0; i < length; i++)
            key.append(CHARS.charAt((int) Math.floor((Math.random() * CHARS.length()))));

        return key.toString();
    }

    private class Entry<V>
    {
        private String _key;
        private V _value;
        private long _putTime;

        private Entry(String key, V value)
        {
            _key = key;
            _value = value;
            _putTime = System.currentTimeMillis();
        }

        private String getKey()
        {
            return _key;
        }

        private V getValue()
        {
            return _value;
        }

        private boolean isExpired()
        {
            return System.currentTimeMillis() - _putTime > _expires;
        }
    }
}

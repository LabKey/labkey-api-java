/*
 * Copyright (c) 2009-2013 LabKey Corporation
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
package org.labkey.remoteapi.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CaseInsensitiveHashMap<V> extends HashMap<String, V>
{
    private Map<String, String> caseMap = new HashMap<String, String>();

    public CaseInsensitiveHashMap()
    {
    }

    public CaseInsensitiveHashMap(Map<String, V> map)
    {
        for (Map.Entry<String, V> entry : map.entrySet())
            put(entry.getKey(), entry.getValue());
    }

    public CaseInsensitiveHashMap(int count)
    {
        super(count);
    }

    @Override
    public void clear()
    {
        super.clear();
        //Not really necessary as having
        caseMap.clear();
    }

    @Override
    public V get(Object o)
    {
        String key = (String) o;
        String correctCaseKey = caseMap.get(key);
        if (null == correctCaseKey && null != key)
        {
            correctCaseKey = caseMap.get(key.toLowerCase());
            if (null != correctCaseKey)
                caseMap.put(key, correctCaseKey);
        }
        return super.get(correctCaseKey);
    }

    @Override
    public boolean containsKey(Object key)
    {
        return super.containsKey(key) || (key != null && super.containsKey(caseMap.get(((String) key).toLowerCase())));
    }

    @Override
    public V remove(Object o)
    {
        String key = (String) o;
        String correctCaseKey = caseMap.get(key);
        if (null == correctCaseKey)
            correctCaseKey = caseMap.get(key == null ? null : key.toLowerCase());

        V val = null == correctCaseKey ? null : super.get(correctCaseKey);

        super.remove(correctCaseKey);
        //Now remove all the cached casings.
        //Need to do this in two loops to avoid modification while iterating
        ArrayList<String> casings = new ArrayList<String>();
        for (String s : caseMap.keySet())
        {
            if (s.equalsIgnoreCase(key))
                casings.add(s);
        }

        for (String s : casings)
            caseMap.remove(s);

        return val;
    }

    @Override
    public V put(String str, V o1)
    {
        String lcase = str == null ? null : str.toLowerCase();
        String correctCase = caseMap.get(lcase);
        if (null == correctCase)
        {
            correctCase = str;
            caseMap.put(lcase, correctCase);
        }
        caseMap.put(str, correctCase);
        return super.put(correctCase, o1);
    }

    @Override
    public V getOrDefault(Object key, V defaultValue)
    {
        if (containsKey(key))
            return get(key);

        return defaultValue;
    }

    @Override
    public V putIfAbsent(String key, V value)
    {
        if (containsKey(key))
            return get(key);

        put(key, value);
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m)
    {
        for (Entry<? extends String, ? extends V> e : m.entrySet())
        {
            put(e.getKey(), e.getValue());
        }
    }

    //
    // UNDONE
    // These methods are to help avoid intellij warnings
    //

    public boolean containsKey(String key)
    {
        return containsKey((Object)key);
    }

    public V get(String key)
    {
        return get((Object)key);
    }

    public V remove(String key)
    {
        return remove((Object)key);
    }

}

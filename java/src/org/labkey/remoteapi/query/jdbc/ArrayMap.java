package org.labkey.remoteapi.query.jdbc;

/*
 * Copyright (c) 2003-2016 Fred Hutchinson Cancer Research Center
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
import java.io.Serializable;
import java.util.*;

/**
 * modified from the slightly more general ArrayList map in server code base

 * @param <K>
 * @param <V>
 */
public class ArrayMap<K, V> extends AbstractMap<K, V> implements Serializable
{
    boolean _readonly = true;

    private static final Object DOES_NOT_CONTAINKEY = new Object()
    {
        @Override
        public String toString()
        {
            return "ArrayListMap.DOES_NOT_CONTAINKEY";
        }
    };

    private V convertToV(Object o)
    {
        return o == DOES_NOT_CONTAINKEY ? null : (V)o;
    }

    public static class FindMap<K> implements Map<K,Integer>
    {
        final Map<K,Integer> _map;
        int _max = -1;

        public FindMap(Map<K,Integer> wrap)
        {
            _map = wrap;
        }

        @Override
        public int size()
        {
            return _map.size();
        }

        @Override
        public boolean isEmpty()
        {
            return _map.isEmpty();
        }

        @Override
        public boolean containsKey(Object o)
        {
            return _map.containsKey(o);
        }

        @Override
        public boolean containsValue(Object o)
        {
            return _map.containsValue(o);
        }

        @Override
        public Integer get(Object o)
        {
            return _map.get(o);
        }

        @Override
        public Integer put(K k, Integer integer)
        {
            assert null != integer;
            assert !containsValue(integer);
            if (integer > _max)
                _max = integer;
            return _map.put(k,integer);
        }

        @Override
        public Integer remove(Object o)
        {
            return _map.remove(o);
        }

        @Override
        public void putAll(Map<? extends K, ? extends Integer> map)
        {
            _map.putAll(map);
        }

        @Override
        public void clear()
        {
            _map.clear();
        }

        @Override
        public Set<K> keySet()
        {
            return _map.keySet();
        }

        @Override
        public Collection<Integer> values()
        {
            return _map.values();
        }

        @Override
        public Set<Entry<K, Integer>> entrySet()
        {
            return _map.entrySet();
        }
    }


    private final FindMap<K> _findMap;
    private final Object _row[];

    public ArrayMap(int columnCount)
    {
        this(new FindMap<>(new HashMap<K, Integer>(columnCount * 2)), new Object[columnCount]);
    }


    public ArrayMap(ArrayMap<K, V> m, Object row[])
    {
        this(m.getFindMap(), row);
    }


    public ArrayMap(FindMap<K> findMap)
    {
        this(findMap, new Object[findMap.size()]);
    }


    public ArrayMap(FindMap<K> findMap, Object[] row)
    {
        _findMap = findMap;
        _row = row;
    }


    public V get(Object key)
    {
        Integer I = _findMap.get(key);
        if (I == null)
            return null;
        int i = I.intValue();
        Object v = i>=_row.length ? null : _row[i];
        return convertToV(v);
    }


    public V put(K key, V value)
    {
        throw new UnsupportedOperationException();
        /*
        if (_readonly)
            throw new IllegalStateException();
        Integer I = _findMap.get(key);
        int i;

        if (null == I)
        {
            i = _findMap._max+1;
            _findMap.put(key, i);
        }
        else
        {
            i = I.intValue();
        }

        V prevValue = convertToV(_row[i]);
        _row[i] = value;
        return prevValue;
        */
    }


    public void clear()
    {
        if (_readonly)
            throw new IllegalStateException();
        for (int i=0 ; i<_row.length ; i++)
            _row[i] = DOES_NOT_CONTAINKEY;
    }


    public boolean containsKey(Object key)
    {
        Integer I = _findMap.get(key);
        if (I == null)
            return false;
        int i = I.intValue();
        Object v = i>=_row.length ? null : _row[i];
        return v != DOES_NOT_CONTAINKEY;
    }


    public boolean containsValue(Object value)
    {
        return Arrays.asList(_row).contains(value);
    }


    public Set<Entry<K, V>> entrySet()
    {
        Set<Entry<K, V>> r = new HashSet<>(_row.length * 2);
        for (Entry<K, Integer> e : _findMap.entrySet())
        {
            int i = e.getValue();
            if (i < _row.length)
            {
                if (_row[i] != DOES_NOT_CONTAINKEY)
                    r.add(new AbstractMap.SimpleEntry<>(e.getKey(), (V)_row[i]));
            }
        }
        return r;
    }


    public Set<K> keySet()
    {
        Set<K> ret = _findMap.keySet();
        assert null != (ret = Collections.unmodifiableSet(ret));
        return ret;

    }


    /** use getFindMap().remove(key) */
    public V remove(Object key)
    {
        throw new UnsupportedOperationException();
    }


    public int size()
    {
        return _findMap.size();
    }


    public Collection<V> values()
    {
        ArrayList<V> a = new ArrayList<>(size());
        for (Object o : _row)
        {
            if (o != DOES_NOT_CONTAINKEY)
                a.add((V)o);
        }
        Collection<V> ret = a;
        assert null != (ret = Collections.unmodifiableCollection(ret));
        return ret;
    }


    /* ArrayListMap extensions (not part of Map) */

    public V get(int i)
    {
        return convertToV(i<_row.length ? _row[i] : null);
    }


    /*
     * NOTE: we're not validating that you haven't removed the key!
    public V set(int i, V value)
    {
        if (i >= _row.length)
            throw new IllegalArgumentException();

        V prev = convertToV(_row[i]);
        _row[i] = value;
        return prev;
    }
     */


    // need to override toString() since we don't implement entrySet()
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("{");

        Iterator i = _findMap.entrySet().iterator();
        boolean hasNext = i.hasNext();
        while (hasNext)
        {
            Map.Entry e = (Map.Entry) i.next();
            Object key = e.getKey();
            Object value = get(e.getValue());
            if (key == this)
                buf.append("(this Map)");
            else
                buf.append(key);
            buf.append("=");
            if (value == this)
                buf.append("(this Map)");
            else
                buf.append(value);
            hasNext = i.hasNext();
            if (hasNext)
                buf.append(", ");
        }

        buf.append("}");
        return buf.toString();
    }


    public FindMap<K> getFindMap()
    {
        return _findMap;
    }
}

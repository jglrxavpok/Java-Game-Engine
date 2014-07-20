package org.jge.util;

import java.util.LinkedList;
import java.util.List;

public final class Lists
{

    public static <T>List<T> asList(T... elems)
    {
        LinkedList<T> list = new LinkedList<T>();
        for(T elem : elems)
            list.add(elem); 
        return list;
    }
    
}
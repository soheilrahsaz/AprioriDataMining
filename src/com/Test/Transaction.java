package com.Test;

import java.util.HashSet;
import java.util.stream.Stream;

public class Transaction {

    private final HashSet<String> items;

    public Transaction(String... items)
    {
        this.items = new HashSet();
        for(String item : items)
        {
            if(item != null && !item.isBlank())
            {
                this.items.add(item);
            }
        }
    }

    public boolean hasItems(String... items)
    {
        return hasItems(false, items);
    }

    public boolean hasItems(boolean ignoreFirst, String... items)
    {
        if(items.length > this.items.size())
            return false;

        for (int i = ignoreFirst ? 1 : 0; i < items.length; i++)
        {
            if(!this.items.contains(items[i]))
                return false;
        }

        return true;
    }

    public HashSet<String> getItems() {
        return items;
    }
}

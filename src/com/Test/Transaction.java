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
        if(items.length > this.items.size())
            return false;

        for(String item : items)
        {
            if(!this.items.contains(item))
                return false;
        }

        return true;
    }

    public HashSet<String> getItems() {
        return items;
    }
}

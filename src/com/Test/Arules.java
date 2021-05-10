package com.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Arules {

    private final double minSupport;
    private final double minConfidence;
    private final DataSource dataSource;
    private ArrayList<List<ItemSet>> frequentPatterns = new ArrayList<>();

    public Arules(double minSupport, double minConfidence, DataSource dataSource) {
        this.minSupport = minSupport;
        this.minConfidence = minConfidence;
        this.dataSource = dataSource;
    }

    public void getFrequentItemSets()
    {
        List<ItemSet> allItems = dataSource.generateItemSet();

        List<ItemSet> itemSets = allItems;
        while (true)
        {
            ArrayList<ItemSet> filtered = scanAndFilter(itemSets);
            if(itemSets != allItems)
            {
                frequentPatterns.add(itemSets);
            }
            itemSets = generateCandidate(filtered);

            if(itemSets.size() == 0)
                break;
        }

    }

    private ArrayList<ItemSet> scanAndFilter(List<ItemSet> itemSets)
    {
        return itemSets.parallelStream().filter(itemSet -> {
           double support = dataSource.getSupport(itemSet.getItems());
           if(support < minSupport)
               return false;
           itemSet.setSupport(support);
           return true;
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<ItemSet> generateCandidate(ArrayList<ItemSet> itemSets)
    {
        int length = itemSets.get(0).getItems().length;

        ArrayList<ItemSet> candidates = new ArrayList<>();

        if(length == 1)
        {
            for (int i = 0; i < itemSets.size(); i++)
            {
                for (int j = i + 1; j < itemSets.size(); j++)
                {
                    candidates.add(itemSets.get(i).combine(itemSets.get(j)));
                }
            }
        }else
        {
            for (int i = 0; i < itemSets.size(); i++)
            {
                for (int j = i + 1; j < itemSets.size(); j++)
                {
                    if(itemSets.get(i).candidateForCombine(itemSets.get(j)))
                    {
                        ItemSet candidate = itemSets.get(i).combine(itemSets.get(j));
                        if(areChildrenAvailableInItemSetsList(candidate, itemSets))
                        {
                            candidates.add(candidate);
                        }
                    }
                }
            }
        }

        return candidates;
    }

    private boolean areChildrenAvailableInItemSetsList(ItemSet candidate, List<ItemSet> itemSets)
    {
        String[] candidateItems = candidate.getItems();
        for (int k = 0; k < candidateItems.length; k++)
        {
            boolean foundEqual = false;
            for (int m = 0; m < itemSets.size(); m++)
            {
                if((foundEqual = areItemsEqual(candidateItems, k, itemSets.get(m).getItems())))
                    break;
            }
            if(!foundEqual)
            {
                return false;
            }
        }

        return true;
    }

    private boolean areItemsEqual(String[] candidate, int ignoreIndex, String[] items)
    {
        for (int i = 0, j = 0; i < items.length; i++, j++)
        {
            if(j == ignoreIndex)
                j++;
            if(!items[i].equals(candidate[j]))
            {
                return false;
            }
        }

        return true;
    }

    //another implementation for validating candidates, (performance is equal to the other approach!)
   /* private boolean areChildrenAvailableInItemSetsList(ItemSet candidate, List<ItemSet> itemSets)
    {
        String[] candidateItems = candidate.getItems();
        HashSet<String> items = new HashSet<>();
        for (int i = 0; i < itemSets.size(); i++)
        {
            String item = getListException(candidateItems, itemSets.get(i).getItems());
            if(item != null)
            {
                if(items.add(item))
                {
                    if(items.size() == candidateItems.length)
                        return true;
                }
            }
        }

        return false;
    }

    private String getListException(String[] candidate, String[] itemSet)
    {
        String exception = null;
        for (int i = 0, j = 0; i < itemSet.length; i++, j++)
        {
            if(!itemSet[i].equals(candidate[j]))
            {
                if(exception != null)
                    return null;

                exception = candidate[j];
                i--;
            }
        }

        if(exception == null)
            return candidate[candidate.length - 1];
        return exception;
    }*/

    public void getArules()
    {

    }

}

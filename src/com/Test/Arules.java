package com.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Arules {

    private final double minSupport;
    private final double minConfidence;
    private final DataSource dataSource;
    private ArrayList<List<ItemSet>> frequentPatterns = new ArrayList<>();
    private List<Rule> finalRules;

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
            ArrayList<ItemSet> filtered = scanAndFilterItemSetAndSetSupport(itemSets);
            if(itemSets != allItems)
            {
                frequentPatterns.add(filtered);
            }else
            {
                dataSource.setCacheInited(true);
            }
            itemSets = generateCandidate(filtered);

            if(itemSets.size() == 0)
                break;
        }
    }

    public void getArules()
    {
        finalRules = generateRules().stream().sorted(Comparator.comparingDouble(Rule::getLift).reversed()).collect(Collectors.toList());
    }

    public void printResult()
    {
        finalRules.stream().map(Rule::toString).forEach(System.out::println);
        System.out.println("Frequent Patterns: " + this.frequentPatterns.stream().flatMap(List::stream).count());
        System.out.println("Rules: " + this.finalRules.stream().count());
    }

    private List<Rule> generateRules()
    {
        return this.frequentPatterns.parallelStream().flatMap(List::stream).map(this::generateRules).flatMap(List::stream)
                .filter(this::scanAndFilterRuleAndSetConfidence).collect(Collectors.toList());
    }

    /**
     * generating all possible rules from a item set, thank you https://www.geeksforgeeks.org/finding-all-subsets-of-a-given-set-in-java/
     * @param itemSet
     * @return
     */
    private List<Rule> generateRules(ItemSet itemSet)
    {
        ArrayList<Rule> rules = new ArrayList<>();
        String[] items = itemSet.getItems();
        int length = items.length;

        for (int i = 1; i < (1<<length) - 1; i++)
        {
            ArrayList<String> priorItems = new ArrayList<>();
            ArrayList<String> resultItems = new ArrayList<>();
            for (int j = 0; j < length; j++)
            {
                if ((i & (1 << j)) > 0)
                {
                    priorItems.add(items[j]);
                }else
                {
                    resultItems.add(items[j]);
                }
            }

            rules.add(new Rule(new ItemSet(priorItems.toArray(String[]::new)), new ItemSet(resultItems.toArray(String[]::new)))
                .setUnionSupport(itemSet.getSupport()));
        }

        return rules;
    }

    private boolean scanAndFilterRuleAndSetConfidence(Rule rule)
    {
        double priorSupport = dataSource.getSupport(rule.getPrior().getItems());
        double confidence = rule.getUnionSupport() / priorSupport;
        if(confidence < minConfidence)
        {
            return false;
        }
        rule.getPrior().setSupport(priorSupport);
        rule.getResult().setSupport(dataSource.getSupport(rule.getResult().getItems()));
        rule.setConfidence(confidence);
        getAndSetLift(rule);
        return true;
    }

    /**
     * lift A->b = support(A U B) / support(A) * support(B)
     * @param rule
     */
    private void getAndSetLift(Rule rule)
    {
        double lift = rule.getConfidence() / rule.getResult().getSupport();
        rule.setLift(lift);
    }

    private ArrayList<ItemSet> scanAndFilterItemSetAndSetSupport(List<ItemSet> itemSets)
    {
        int length = itemSets.get(0).getItems().length;
        Stream<ItemSet> stream = itemSets.stream();
        if(!Main.CACHE_ITEM_DATA_SOURCE || length > 1)
        {
            stream.parallel();
        }

        return stream.filter(itemSet -> {
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
//            int _k = k;
//            if(!itemSets.stream().anyMatch(itemSet -> areItemsEqual(candidateItems, _k, itemSet.getItems())))
//            {
//                return false;
//            }
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



}

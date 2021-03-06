package com.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DataSource {

    private final Path filePath;
    private ArrayList<Transaction> transactions;
    private long transactionCount;
    private HashMap<String, List<Transaction>> itemIndexCache = new HashMap<>();
    private boolean isCacheInited = false;

    public DataSource(Path filePath) throws IOException {
        this.filePath = filePath;
        init();
    }

    private void init() throws IOException {
        this.transactions = Files.lines(filePath).parallel().map(line -> new Transaction(line.split(",")))
                .collect(Collectors.toCollection(() -> new ArrayList<>(9850)));
        this.transactionCount = this.transactions.size();
    }

    public double getSupport(String... items)
    {
        if(!Main.CACHE_ITEM_DATA_SOURCE)
        {
            return transactions.parallelStream()
                    .filter(transaction -> transaction.hasItems(items))
                    .count()/(double)transactionCount;
        }

        if(!isCacheInited && items.length == 1)
        {
            List<Transaction> containingItem =  transactions.parallelStream()
                    .filter(transaction -> transaction.hasItems(items))
                    .collect(Collectors.toList());
            this.itemIndexCache.put(items[0], containingItem);
            return containingItem.size()/(double)transactionCount;
        }else
        {
            return this.itemIndexCache.get(items[0]).parallelStream()
                    .filter(transaction -> transaction.hasItems(true, items))
                    .count()/(double)transactionCount;
        }
    }

    public long getTransactionCount() {
        return transactionCount;
    }

    public List<ItemSet> generateItemSet()
    {
        return this.transactions.stream().flatMap(t -> t.getItems().stream()).distinct()
                .map(item -> new ItemSet(item)).collect(Collectors.toList());
    }

    public void setCacheInited(boolean cacheInited) {
        isCacheInited = cacheInited;
    }
}

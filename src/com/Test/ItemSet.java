package com.Test;


public class ItemSet {

    private final String[] items;
    private double support;

    public ItemSet(String... items)
    {
        this.items = items;
    }

    public double getSupport() {
        return support;
    }

    public void setSupport(double support) {
        this.support = support;
    }

    public String[] getItems() {
        return items;
    }

    public ItemSet combine(ItemSet itemSet)
    {
        int currentLength = this.items.length;
        String[] newItems = new String[currentLength+1];
        System.arraycopy(this.items, 0, newItems, 0, currentLength);
        newItems[currentLength] = itemSet.getItems()[currentLength-1];

        return new ItemSet(newItems);
    }

    /**
     * checking if the left items are equal
     * @param that
     * @return
     */
    public boolean candidateForCombine(ItemSet that)
    {
        for (int i = 0; i < this.items.length - 1; i++)
        {
            if(!this.items[i].equals(that.items[i]))
            {
                return false;
            }
        }

        return true;
    }
}

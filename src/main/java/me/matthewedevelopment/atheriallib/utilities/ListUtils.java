package me.matthewedevelopment.atheriallib.utilities;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {
    public static <E extends Object> List<E> getPageItems(List<E> allItems, int amountPerPage, int currentPage) {

        List<E> collect = new ArrayList<>();
        collect.addAll(allItems);

        List<E> returnItems = new ArrayList<>();
        if (collect.size() < amountPerPage) {
            returnItems.addAll(collect);
        } else {
            int endIndex = currentPage * amountPerPage;
            int startIndex = endIndex - amountPerPage;
            if (endIndex > collect.size()) {
                endIndex = collect.size();
            }
            returnItems.addAll(collect.subList(startIndex, endIndex));
        }
        return returnItems;
    }

    public static <E> int getMaxPage(List<E> stringList, int amountPerPage) {
        int maxPage = 0;
        for (int i = 0; i < stringList.size(); i += amountPerPage) {
            maxPage++;
        }
        if (maxPage == 0) {
            maxPage = 1;
        }
        return maxPage;
    }
}

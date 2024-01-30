package me.matthewedevelopment.atheriallib.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    public static String getMessage(String[] args, int start) {
        String[] args1 = ListUtils.getArgs(args, start);
        String msg = "";
        for (int i = 0; i < args1.length; i++) {
            msg+= args1[i];

            if (i < args[1].length()-1){
                msg+=" ";
            }
        }
        return msg;
    }
    public static <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null; // Or throw an exception, depending on your requirement
        }
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
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
    public static List<String> filterStartsWith(List<String> completions, String partialInput) {
        if (partialInput==null||partialInput.isEmpty()){
            return completions;
        }
        List<String> filteredList = new ArrayList<>();
        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(partialInput.toLowerCase())) {
                filteredList.add(completion);
            }
        }
        return filteredList;
    }
    public static String[] getArgs(String[] input, int start) {
        List<String> newArgs = new ArrayList<>();
        if (input.length - 1 == start) {
            newArgs.add(input[1]);
        } else {
            for(int i = start; i < input.length; ++i) {
                if (input[i] != null) {
                    newArgs.add(input[i]);
                }
            }
        }

        return (String[])newArgs.toArray(new String[0]);
    }
}

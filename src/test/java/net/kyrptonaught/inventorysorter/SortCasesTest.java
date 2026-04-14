package net.kyrptonaught.inventorysorter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortCasesTest {

    @Test
    public void testChineseSorting() {
        String stack1 = "白色混凝土";
        String stack2 = "白色陶瓦";
        String stack3 = "白色羊毛";
        String stack4 = "光滑的石头";
        String stack5 = "黑色地毯";
        String stack6 = "红色地毯";
        String stack7 = "透明冰";
        String stack8 = "透明玻璃";
        String stack9 = "砖楼梯";
        String stack10 = "砖墙";

        Comparator<String> comparator = SortCases.getNameComparator("zh_cn");

        List<String> input = new ArrayList<>(List.of(stack1, stack3, stack2, stack6, stack5, stack9, stack10, stack4, stack7, stack8));
        List<String> expected = List.of(stack1, stack2, stack3, stack4, stack5, stack6, stack7, stack8, stack9, stack10);

        input.sort(comparator);

        for (int i = 0; i < input.size(); i++) {
            System.out.println("Input: " + input.get(i));
            Assertions.assertEquals(expected.get(i), input.get(i), "Sorting failed at index " + i);
        }
    }
}

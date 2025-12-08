package org.unina.robulaplus;

import java.util.ArrayList;
import java.util.List;

public class RobulaPlus {
    public final List<String> attributePriorities = new ArrayList<>(){};
    public final List<String> attributeBlacklist = new ArrayList<>(){};

    public RobulaPlus()
    {
        attributePriorities.add("name");
        attributePriorities.add("class");
        attributePriorities.add("title");
        attributePriorities.add("alt");
        attributePriorities.add("value");

        attributeBlacklist.add("href");
        attributeBlacklist.add("src");
        attributeBlacklist.add("onclick");
        attributeBlacklist.add("onload");
        attributeBlacklist.add("tabindex");
        attributeBlacklist.add("width");
        attributeBlacklist.add("height");
        attributeBlacklist.add("style");
        attributeBlacklist.add("size");
        attributeBlacklist.add("maxlength");
        attributeBlacklist.add("data-*");
    }

    public String getRobusXPath() {
        String element = ""; // eval?

        List<XPath> xpaths = new ArrayList<>(){};
        xpaths.add(new XPath("//*"));

        while (true) {
            XPath xpath = xpaths.remove(0);
            List<XPath> tempList = new ArrayList<>();
            tempList.add(xpath);
            tempList.add(xpath);
            tempList.add(xpath);
            tempList.add(xpath);
            tempList.add(xpath);
            tempList.add(xpath);
            tempList.add(xpath);
            for (XPath x : xpaths) {

            }
            return "";
        }
    }
}


package org.unina.robulaplus;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    /**
     * Evaluates an XPath expression against the provided DOM Document to retrieve the corresponding Element.
     * <p>
     * This method corresponds to the {@code eval(abs, d)} function described in the Robula+ algorithm (Line 3).
     * It is primarily used to resolve the initial absolute XPath into the target DOM Element before generating robust locators.
     * </p>
     *
     * @param xpath The XPath expression to evaluate (typically an absolute XPath).
     * @param doc   The Jsoup Document context.
     * @return The first {@link Element} matching the XPath, or {@code null} if no match is found or the XPath is invalid.
     */
    public static Element eval(String xpath, Document doc) {
        try {
            Elements found = doc.selectXpath(xpath);
            if (found.isEmpty()) {
                return null;
            }
            return found.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Checks if the target element is uniquely located by the provided xpath in the document.
     * @param xpath the xpath of the element to find
     * @param target the element to find via xpath
     * @param doc the document containing the target element
     * @return {@code true} if the element is uniquely located, {@code false} otherwise
     */
    public static boolean uniquelyLocate(String xpath, Element target, Document doc) {
        try {
            Elements found = doc.selectXpath(xpath);
            return found.size() == 1 && found.get(0).equals(target);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Checks if the provided XPath step already contains any kind of predicate.
     * <p>
     * This helper is used to verify preconditions for transformations like
     * {@code transfAddID} or {@code transfAddAttribute}, which typically require
     * the step to be free of existing predicates (e.g., it converts "div" to
     * "div[@id='foo']", but should skip "div[@class='bar']").
     * </p>
     *
     * @param step The string representation of the XPath step (e.g., "div" or "span[@class='row']").
     * @return {@code true} if the step contains an open bracket '[', indicating the presence of a predicate; {@code false} otherwise.
     */
    public static boolean hasPredicates(String step) {
        return step.contains("[");
    }

    /**
     * Checks if the provided XPath step specifically contains a positional predicate (e.g., [1], [2]).
     * <p>
     * This helper is used to verify preconditions for transformations like {@code transfAddPosition}.
     * It distinguishes between general predicates (like attributes) and numeric indices.
     * </p>
     *
     * @param step The string representation of the XPath step to check.
     * @return {@code true} if the step matches a pattern containing a numeric index inside brackets (e.g., "[1]"); {@code false} otherwise.
     */
    public static boolean hasPositionPredicate(String step) {
        return step.matches(".*\\[\\d+].*");
    }

    /**
     * Generates the power set (the set of all possible subsets) for the given list of attributes using an iterative approach.
     * <p>
     * This method is used by the {@code transfAddAttributeSet} transformation to create candidate locators
     * based on combinations of multiple attributes (e.g., {@code [@name='foo' and @class='bar']}).
     * </p>
     * <p>
     * <b>Performance Warning:</b> The size of the output grows exponentially (2<sup>n</sup>).
     * For example, an input of 5 attributes results in 32 subsets, but 20 attributes would result in over 1 million.
     * Callers should ensure the input list is limited in size before invoking this method.
     * </p>
     *
     * @param originalList The source list of {@link Attribute} objects to combine.
     * @return A list of lists, where each inner list represents a unique subset of the original attributes (including the empty set).
     */
    public static List<List<Attribute>> generatePowerSet(List<Attribute> originalList) {
        List<List<Attribute>> sets = new ArrayList<>();
        sets.add(new ArrayList<>());

        for (Attribute attr : originalList) {
            int currentSize = sets.size();
            for (int i = 0; i < currentSize; i++) {
                List<Attribute> newSubset = new ArrayList<>(sets.get(i));
                newSubset.add(attr);
                sets.add(newSubset);
            }
        }
        return sets;
    }
}

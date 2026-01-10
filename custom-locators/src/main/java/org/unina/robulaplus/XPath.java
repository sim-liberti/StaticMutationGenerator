package org.unina.robulaplus;

import java.util.LinkedList;

    /**
     * Helper class to represent and manipulate an XPath expression as a mutable sequence of steps.
     * <p>
     * This abstraction simplifies the Robula+ transformations by treating the XPath
     * not as a monolithic string, but as a list of tokens. For example, the XPath
     * {@code //*\/div[@id='foo']} is represented internally as a list: {@code ["*", "div[@id='foo']"]}.
    * </p>
    * <p>
     * This structure allows for easy modification of the "head" (the current element being specialized)
    * or prepending new levels (deepening the search tree) without complex string parsing.
    * </p>
    */
    public class XPath {
        LinkedList<String> steps;

        /**
         * Initializes a new XPath with a single starting step.
         * <p>
         * In the Robula+ algorithm, the search typically starts with the wildcard {@code "*"},
         * representing the query {@code //*}.
         * </p>
         *
         * @param initialStep The first token of the XPath (e.g., "*").
         */
        public XPath(String initialStep) {
            this.steps = new LinkedList<>();
            this.steps.add(initialStep);
        }

        /**
         * Copy constructor.
         * <p>
         * Creates a deep copy of the provided XPath object. This is crucial for the BFS algorithm,
         * as each transformation must generate a <i>new</i> candidate without modifying the original
         * parent XPath in the queue.
         * </p>
         *
         * @param original The XPath object to duplicate.
         */
        public XPath(XPath original) {
            this.steps = new LinkedList<>(original.steps);
        }

        /**
         * Converts the internal list of steps into a valid XPath string.
         * <p>
         * The method automatically prefixes the path with {@code //} and joins the steps
         * with {@code /}.
         * </p>
         *
         * @return The full XPath string (e.g., {@code "//div/span"}).
         */
        public String toString() {
            return "//" + String.join("/", steps);
        }

        /**
         * Returns the number of steps (levels) in the current XPath.
         * <p>
         * This corresponds to the value <b>N</b> described in the Robula+ paper.
         * It is used to determine which ancestor in the DOM tree corresponds to the current head of the XPath.
         * </p>
         *
         * @return The number of nodes in the path.
         */
        public int getLength() {
            return steps.size();
        }

        /**
         * Retrieves the first step (the "head") of the XPath.
         * <p>
         * This step represents the specific part of the locator currently being refined or specialized
         * by transformations like {@code transfConvertStar} or {@code transfAddID}.
         * </p>
         *
         * @return The string representation of the first step (e.g., {@code "*"} or {@code "div"}).
         */
        public String getHead() {
            return steps.getFirst();
        }

        /**
         * Replaces the first step of the XPath with a new value.
         * <p>
         * This is the primary method used by transformations to "specialize" a locator.
         * For example, changing {@code "div"} to {@code "div[@id='foo']"}.
         * </p>
         *
         * @param newHead The new string value for the first step.
         */
        public void replaceHead(String newHead) {
            steps.removeFirst();
            steps.addFirst(newHead);
        }

        /**
         * Adds a new step to the beginning of the XPath list.
         * <p>
         * This is used exclusively by the {@code transfAddLevel} transformation to
         * increase the depth of the locator (e.g., changing {@code //div} to {@code //*\/div}).
    * </p>
    *
    * @param step The new step to prepend (typically {@code "*"}).
    */
    public void prepend(String step) {
        steps.addFirst(step);
    }
}

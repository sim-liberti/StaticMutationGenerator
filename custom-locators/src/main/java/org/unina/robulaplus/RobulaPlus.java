package org.unina.robulaplus;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;

import static org.unina.robulaplus.Transformations.*;
import static org.unina.robulaplus.Utils.eval;

public class RobulaPlus {

    public String getRobustXPath(String abs, Document doc) {
        Element target = eval(abs, doc);

        // 1. Build the ancestor list L (Target: index 0)
        List<Element> ancestors = new ArrayList<>();
        Element current = target;
        while (current != null && !current.equals(doc)) {
            ancestors.add(current);
            current = current.parent();
        }

        // Build the xpList
        LinkedList<XPath> xpList = new LinkedList<>();
        xpList.add(new XPath("*"));

        Set<String> visited = new HashSet<>();

        while (!xpList.isEmpty()) {
            XPath xp = xpList.removeFirst();
            String xpStr = xp.toString();

            if (visited.contains(xpStr)) continue;
            visited.add(xpStr);

            // Build the temp list
            List<XPath> temp = new ArrayList<>();

            // If xp è //td (len=1), we are looking at ancestors.get(0).
            // If xp è //*/td (len=2), the head is *, which is equals to ancestors.get(1).
            int N = xp.getLength();
            if (N > ancestors.size()) continue; // Out of bounds safety
            Element contextElement = ancestors.get(N - 1);

            temp.add(transfConvertStar(xp, contextElement));
            temp.add(transfAddID(xp, contextElement));
            temp.add(transfAddText(xp, contextElement));
            temp.addAll(transfAddAttribute(xp, contextElement));
            temp.addAll(transfAddAttributeSet(xp, contextElement));
            temp.add(transfAddPosition(xp, contextElement));
            temp.add(transfAddLevel(xp, ancestors));

            for (XPath candidate : temp) {
                if (candidate == null) continue;

                if (Utils.uniquelyLocate(candidate.toString(), target, doc)) {
                    return candidate.toString(); // Success
                }

                // If it's not uniquely located we add it back to the queue to refine it
                xpList.add(candidate);
            }
        }

        return null; // Failure
    }

    // Esempio di utilizzo
    public static void main(String[] args) {
        String html = "<html><body><table><tr><td>Target</td><td>Altro</td></tr></table></body></html>";
        String abs = "//*";
        Document doc = Jsoup.parse(html);

        RobulaPlus robula = new RobulaPlus();
        String xpath = robula.getRobustXPath(abs, doc);
        System.out.println("Generated XPath: " + xpath);
    }
}
package org.unina.robulaplus;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
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

    public static void main(String[] args) {
        record element(String name, String xpath, String source){}
        List<element> elements = new ArrayList<>();
        elements.add(new element("NavSearch", "/html/body/angular-spotify-root/as-layout/as-nav-bar/ul/li[2]/a", "home.html"));
        elements.add(new element("SearchInput", "/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-search/div/div[1]/as-input/div/input", "search.html"));
        elements.add(new element("ArtistCard", "/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-search/div/div[3]/div/as-card[1]/a", "search.html"));
        elements.add(new element("ArtistName", "/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-artist/div/as-media-summary/div/h2", "artist.html"));

        RobulaPlus robula = new RobulaPlus();
        for (element e: elements) {
            try {
                InputStream inputStream = RobulaPlus.class.getClassLoader().getResourceAsStream(e.source);

                if (inputStream == null) {
                    throw new IllegalArgumentException("File not found");
                }
                Document doc = Jsoup.parse(inputStream, "UTF-8", "");
                String abs = e.xpath;

                String xpath = robula.getRobustXPath(abs, doc);
                System.out.println("Generated XPath for " + e.name + ": " + xpath);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        // Results:
        //
        // NavSearch     //*[@ng-reflect-router-link='/search']
        // NavHome       //*[@ng-reflect-router-link='']
        // SearchInput   //input
        // PlayButton    //as-media-order[@ng-reflect-index='1']/div/as-play-button/button/svg-icon/svg
        // NowPlaying    //*[@class='text-white hover:underline']
    }
}
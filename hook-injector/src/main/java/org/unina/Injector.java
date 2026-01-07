package org.unina;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.unina.data.Component;
import org.unina.util.ComponentIndexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.regex.Pattern;

public class Injector {

    final static Pattern componentTagPattern = Pattern.compile("<([a-z][a-z0-9]*(?:-[a-z0-9]+)+)");

    public static void injectHooks() throws IOException {
        int max_hook_id = 0;

        for (Component component : ComponentIndexer.getInstance().getAllComponents()) {
            Document document = Jsoup.parse(component.path, "UTF-8", "", Parser.xmlParser());
            for (Element element : document.getAllElements()) {
                Attribute hook = getHook(element);
                if (hook == null) continue;
                int hook_id = extractHookId(hook);
                if (hook_id > max_hook_id) max_hook_id = hook_id;
            }
        }
        for (Component component : ComponentIndexer.getInstance().getAllComponents()) {
            boolean hasHtmlMainTags = false;
            String regex = "(?i).*<\\s*(html|head|body|!DOCTYPE).*";
            if (java.util.regex.Pattern.compile(regex).matcher(Files.readString(component.path)).matches()) {
                hasHtmlMainTags = true;
            }

            Parser parser = Parser.htmlParser();
            parser.settings(new ParseSettings(true, true));
            Document document = Jsoup.parse(component.path, "UTF-8", "", parser);

            for (Element element : document.getAllElements()) {
                Attribute hook = getHook(element);
                if (hook != null) continue;
                max_hook_id += 1;
                if (componentTagPattern.matcher(element.tagName()).matches()) {
                    element.attr("x-test-tpl-" + max_hook_id, "");
                } else {
                    element.attr("x-test-hook-" + max_hook_id, "");
                }
            }
            String content = hasHtmlMainTags ? document.html() : document.body().html();
            Files.write(component.path, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    private static Attribute getHook(Element element) {
        return element.attributes()
                .asList()
                .stream()
                .filter(attr -> attr.getKey().startsWith("x-test-"))
                .findFirst()
                .orElse(null);
    }

    private static int extractHookId(Attribute hook) {
        Pattern hookIdPattern = Pattern.compile("(\\d+)");
        return Integer.parseInt(hookIdPattern.matcher(hook.getKey()).group(1));
    }
}

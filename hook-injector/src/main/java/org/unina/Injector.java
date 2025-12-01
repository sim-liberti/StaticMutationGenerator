package org.unina;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.regex.Pattern;

public class Injector {

    final static Pattern componentTagPattern = Pattern.compile("<([a-z][a-z0-9]*(?:-[a-z0-9]+)+)");

    public static void injectHooks(Set<Path> sourceFiles) throws IOException {
        int max_hook_id = 0;

        for (Path file : sourceFiles) {
            Document document = Jsoup.parse(file);
            for (Element element : document.getAllElements()) {
                Attribute hook = getHook(element);
                if (hook == null) continue;
                int hook_id = extractHookId(hook);
                if (hook_id > max_hook_id) max_hook_id = hook_id;
            }
        }
        for (Path file : sourceFiles) {
            Document document = Jsoup.parse(file);
            for (Element element : document.getAllElements()) {
                Attribute hook = getHook(element);
                if (hook != null) continue;
                max_hook_id += 1;
                if (componentTagPattern.matcher(element.tagName()).matches()) {
                    element.attr("x-test-tpl-" + max_hook_id);
                } else {
                    element.attr("x-test-hook-" + max_hook_id);
                }
            }
            String content = document.html();
            Files.write(file, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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

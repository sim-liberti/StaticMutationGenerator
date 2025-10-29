package org.unina.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FileBrowser {
    public static Path findComponentBySelector(String selector, Path currentDir) throws IOException {
        Path srcDir = getSrcPathDirectory(currentDir);
        if (srcDir == null) return null;

        try (Stream<Path> stream = Files.walk(srcDir)) {
            return stream
                    .filter(file -> file.toString().endsWith(".component.ts"))
                    .filter(file -> {
                        try {
                            String content = Files.readString(file);
                            return content.contains("selector: '" + selector + "'") ||
                                    content.contains("selector: \"" + selector + "\"");
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .map(file -> {
                        String tsFileName = file.getFileName().toString();
                        String htmlFileName = tsFileName.replace(".component.ts", ".component.html");
                        return file.resolveSibling(htmlFileName);
                    })
                    .filter(Files::exists)
                    .findFirst()
                    .orElse(null);
        }
    }

    public static Path getSrcPathDirectory(Path startDir) {
        Path dir = startDir;
        while (dir != null && !dir.getFileName().toString().equals("app") &&
                !dir.getParent().getFileName().toString().equals("src")) {
            dir = dir.getParent();
        }
        return dir;
    }

}

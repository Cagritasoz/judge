package com.cagritasoz.service;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class ZipExtractor {

    public void extractSafely(Path zipFilePath, Path safeExtractionDir) throws IOException {

        log.info("Safe extraction in progress for zip file: {}", zipFilePath);

        try(InputStream inputStream = Files.newInputStream(zipFilePath);
            ZipInputStream zip = new ZipInputStream(inputStream)) {

            ZipEntry entry;
            while((entry = zip.getNextEntry()) != null) {

                log.info("Processing zip entry: {}", entry.getName());

                Path targetPath = safeExtractionDir.resolve(entry.getName()).normalize(); //Normalize is very important here.

                if(!targetPath.startsWith(safeExtractionDir)) {
                    log.warn("Skipping malicious zip entry: {}", entry);
                    continue;
                }

                if(entry.isDirectory()) {
                    Files.createDirectories(targetPath);
                }
                else {
                    Files.createDirectories(targetPath.getParent()); // Ensure parent exists.
                    Files.copy(zip, targetPath); // Copy the bytes from the ZIP stream, throws FileAlreadyExists if file exists.
                }
            }
            log.info("Extraction complete for zip file: {}", zipFilePath);
        }
    }
}

/*
Zip file example, there is no tree structure.
Entry 1: "src/"                       (directory, indicated by "/" at the end)
Entry 2: "src/main/"                  (directory)
Entry 3: "src/main/Main.java"         (file, content: bytes of Main.java)
Entry 4: "src/main/App.java"          (file, content: bytes of App.java)
Entry 5: "src/utils/"                 (directory)
Entry 6: "src/utils/Helper.java"      (file, content: bytes of Helper.java)
Entry 7: "tests/"                     (directory)
Entry 8: "tests/MainTest.java"        (file, content: bytes of MainTest.java)
Entry 9: "README.md"                  (file, content: bytes of README.md)
Entry 10: ".gitignore"                (file, content: bytes of .gitignore)
 */

package org.codice.imaging.nitf.nitfpeek;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.codice.imaging.nitf.core.common.NitfFormatException;
import org.codice.imaging.nitf.core.header.NitfHeader;
import org.codice.imaging.nitf.core.image.ImageSegment;
import org.codice.imaging.nitf.fluent.NitfParserInputFlow;

public class NitfPeek {
    private static boolean dumpImageHeader = false;

    public static void main(String[] args)
            throws ParseException, IOException, java.text.ParseException, NitfFormatException {
        Options options = new Options();

        Option fileOption = Option.builder("f")
                .hasArg()
                .longOpt("file")
                .desc("Nitf file to peek into.")
                .build();

        Option recursiveOption = Option.builder("d")
                .hasArg()
                .longOpt("directory")
                .desc("Search for NITFs recursively from the given directory.")
                .build();

        Option imageHeaderOption = Option.builder("i")
                .longOpt("imageheader")
                .desc("dump out image header information.")
                .build();

        options.addOption(fileOption);
        options.addOption(recursiveOption);
        options.addOption(imageHeaderOption);

        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine commandLine = commandLineParser.parse(options, args);

        if (!(commandLine.hasOption("f") || commandLine.hasOption("d"))) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "NitfPeek <options>", options );
        }

        Path path = null;

        if (commandLine.hasOption("i")) {
            dumpImageHeader = true;
        }

        if (commandLine.hasOption("f")) {
            processSingleFile(FileSystems.getDefault()
                    .getPath(commandLine.getOptionValue("f")).toFile() );
        }

        if (commandLine.hasOption("d")) {
            processRecursively(FileSystems.getDefault()
                    .getPath(commandLine.getOptionValue("d")) );
        }
    }

    private static void processSingleFile(File file)
            throws FileNotFoundException, java.text.ParseException, NitfFormatException {
        new NitfParserInputFlow()
                .file(file)
                .allData()
                .fileHeader(header -> dumpFileHeader(header))
                .forEachImageSegment(segment -> dumpImageHeader(segment))
                ;
    }

    private static void dumpImageHeader(ImageSegment segment) {
        if (dumpImageHeader) {
            for (ImageAttribute imageAttribute : ImageAttribute.values()) {
                String shortName = imageAttribute.getShortName();
                String longName = imageAttribute.getLongName();
                String value = imageAttribute.getAccessorFunction().apply(segment).toString();

                if (StringUtils.isNotBlank(value)) {
                    System.out.println(String.format("%s (%s) : %s", longName, shortName, value));
                }
            }

            System.out.println();

            dumpTres(segment.getTREsFlat());
        }
    }

    private static void processRecursively(Path startPath) throws IOException {
        int maxDepth = 5;
        try (Stream<Path> stream = Files.find(startPath, maxDepth, (path, attr) ->
                String.valueOf(path).matches(".*\\.(nsf|ntf)"))) {
            stream.forEach(path -> {
                try {
                    processSingleFile(path.toFile());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                } catch (NitfFormatException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static void dumpTres(Map<String, String> tres) {
        for (String key : tres.keySet()) {
            String value = tres.get(key);

            if (StringUtils.isNotBlank(value)) {
                System.out.println(String.format("%s : %s", key, value));
            }
        }

        System.out.println();
    }

    private static void dumpFileHeader(NitfHeader header) {
        System.out.println();
        System.out.println(header.getFileTitle());
        System.out.println("-------------------------------");

        for (NitfHeaderAttribute attribute : NitfHeaderAttribute.values()) {
            String shortName = attribute.getShortName();
            String longName = attribute.getLongName();
            String value = attribute.getAccessorFunction().apply(header).toString();

            if (StringUtils.isNotBlank(value)) {
                System.out.println(String.format("%s (%s) : %s", longName, shortName, value));
            }
        }

        System.out.println();
        Map<String, String> tres = header.getTREsFlat();

        if (tres != null) {
            dumpTres(tres);
        }
    }
}

package eu.janinko.andaria.uotools.importer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Importer {
    public enum Type {
        ART,
        GUMP
    }
    public static void main(String[] args) throws IOException {
        if(args.length < 4){
            error("You must enter all arguments");
        }
        Type type;
        try {
            type = Type.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException ex) {
            error("TYPE must be one of: " + Type.values());
            return;
        }
        Path origDir = Paths.get(args[1]);
        if(!Files.isDirectory(origDir)){
            error("SOURCE must be directory.");
            return;
        }
        Path imageDir = Paths.get(args[2]);
        if(!Files.isDirectory(imageDir)){
            error("IMAGES must be directory.");
            return;
        }
        Path outDir = Paths.get(args[3]);
        if(!Files.isDirectory(imageDir)){
            error("OUTPUT must be directory.");
            return;
        }
        if(origDir.equals(outDir)){
            error("OUTPUT must be different then SOURCE.");
            return;
        }

        ImageImport imageImport = new ImageImport(origDir);
        switch (type){
            case ART: imageImport.importArts(imageDir, outDir); break;
            case GUMP: imageImport.importGumps(imageDir, outDir); break;
            default: error("Unknown type " + type); return;
        }
    }

    private static void error(String x) {
        printHelp();
        System.out.flush();
        System.err.println(x);
        System.exit(1);
    }


    private static void printHelp(){
        System.out.println("importer TYPE SOURCE IMAGES OUTPUT");
        System.out.println("  TYPE is file type: gump, art.");
        System.out.println("  SOURCE is directory with the source mul files.");
        System.out.println("  IMAGES is directory with the images to import. Images must be named ID.png, where ID is the numeral id of the image to replace (e.g 1020.png or 0x3fc.png).");
        System.out.println("  OUTPUT is directory where the updated files should be stored. Must be different then SOURCE directory.");
    }
}

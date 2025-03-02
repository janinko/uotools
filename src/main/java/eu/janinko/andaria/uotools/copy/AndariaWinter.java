package eu.janinko.andaria.uotools.copy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author janinko
 */
public class AndariaWinter {
    public static void main(String[] args) throws IOException {
        
        
        if(args.length < 4){
            System.out.println("Pouziti AndariaWinter:");
            System.out.println("AndariaWinter CESTA_LONI CESTA_LETOS CESTA_ZIMA VYSTUPNI_CESTA");
            System.out.println("CESTA_LONI: cesta ke slozce, ve ktere je lonska mapa (ze ktere se delala zima loni)");
            System.out.println("CESTA_LETOS: cesta ke slozce, ve ktere je letosni mapa (ze ktere se ma udelat zima)");
            System.out.println("CESTA_ZIMA: cesta ke slozce, ve ktere je lonska zimni mapa");
            System.out.println("VYSTUPNI_CESTA: cesta ke slozce, do ktere se ulozi letosni zima");
            System.exit(1);
        }
        
        
        Path lonskaMapa = Paths.get(args[0]);
        Path letosniMapa = Paths.get(args[1]);
        Path zimniMapa = Paths.get(args[2]);
        Path vystup = Paths.get(args[3]);
        
        testDirectory(lonskaMapa);
        testDirectory(letosniMapa);
        testDirectory(zimniMapa);
        testDirectory(vystup);
        testDiferentPaths(lonskaMapa, letosniMapa, zimniMapa, vystup);

        MapCopier copier = new MapCopier(lonskaMapa, letosniMapa, zimniMapa, vystup);
        System.out.println("Kopiruju mapu...");
        copier.copyChangesInMapToDifferentMap();
        System.out.println("Zmeny v mape:");
        copier.printChangesInMapBlocks();
    }

    private static void testDirectory(Path lonskaMapa) {
        if(!Files.isDirectory(lonskaMapa)){
            System.err.println( lonskaMapa + " neni slozka.");
            System.exit(1);
        }
    }
    
    private static void testDiferentPaths(Path... paths){
        HashSet<Path> setPaths = new HashSet<>(Arrays.asList(paths));
        if(setPaths.size() != paths.length){
            System.err.println("Každá cesta musí být jiná!");
            System.exit(1);
        }
    }
}

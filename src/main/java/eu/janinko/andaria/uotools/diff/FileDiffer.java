package eu.janinko.andaria.uotools.diff;

import eu.janinko.andaria.ultimasdk.UOFiles;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import eu.janinko.andaria.ultimasdk.files.*;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class FileDiffer {

    public static final Path filePath = Paths.get("/home/janinko/Ultima/grafika/soubory");
    public static final Path origPath = filePath.resolve("orig");
    public static final Path workPath = filePath.resolve("work");
    public static final String uopath = "/home/janinko/Stažené/UO orig/";

    public static void main(String[] args) {
        if (args.length < 3) {
            printHelp();
            System.exit(1);
        }
        Path oldPath = Paths.get(args[1]);
        Path newPath = Paths.get(args[2]);
        Optional<Path> outPath = args.length < 4 ? Optional.empty() : Optional.of(Paths.get(args[3]));

        exitIfNotDirectory(oldPath);
        exitIfNotDirectory(newPath);
        outPath.ifPresent(FileDiffer::exitIfNotDirectory);

        try {
            switch (args[0].toLowerCase()) {
                case "gump": diffGump(oldPath, newPath, outPath);
                    break;
                case "tiledata": diffTiledata(oldPath, newPath);
                    break;
                case "art": diffArt(oldPath, newPath, outPath);
                    break;
                case "hue": diffHue(oldPath, newPath);
                    break;
                case "cliloc": diffCliLoc(oldPath, newPath);
                    break;
                case "anim": diffAnim(oldPath, newPath, outPath);
                    break;
                case "map": diffMap(oldPath, newPath, outPath);
                    break;
                case "sounds": diffSounds(oldPath, newPath, outPath);
                    break;
                case "statics": diffStatics(oldPath, newPath, outPath);
                    break;
                default: printHelp();
                    System.exit(1);
                    break;
            }
        } catch (IOException ex) {
            System.err.println("Error: " + ex.getLocalizedMessage());
            System.exit(2);
        }
    }

    private static void exitIfNotDirectory(Path oldPath) {
        if(!Files.isDirectory(oldPath)){
            System.err.println(oldPath  + " is not a directoy.");
            System.exit(1);
        }
    }

    private static void printHelp() {
        System.out.println("Usage: FileDiffer TYPE OLD NEW [OUTPUT]");
        System.out.println();
        System.out.println("TYPE: one of: gump, tiledata, art, hue, cliloc, anim, map, statics");
        System.out.println("OLD: directory with old files");
        System.out.println("NEW: directory with new files");
        System.out.println("OUTPUT: optional directory where output files will be generatd");
    }

    private static void diffGump(Path oldPath, Path newPath, Optional<Path> outPath) throws IOException {
        Gumps origGumps = UOFiles.loadGumpsFromDir(oldPath);
        Gumps newGumps = UOFiles.loadGumpsFromDir(newPath);

        GumpsDiff gd = outPath.map(GumpsDiff::new).orElseGet(GumpsDiff::new);
        gd.diff(origGumps, newGumps);
    }

    private static void diffTiledata(Path oldPath, Path newPath) throws IOException {
        TileData oldTiledata = UOFiles.loadTileDataFromDir(oldPath);
        TileData newTiledata = UOFiles.loadTileDataFromDir(newPath);

        TiledataDiff td = new TiledataDiff();
        td.diff(oldTiledata, newTiledata);
    }

    private static void diffArt(Path oldPath, Path newPath, Optional<Path> outPath) throws IOException {
        Arts oldArt = UOFiles.loadArtsFromDir(oldPath);
        Arts newArt = UOFiles.loadArtsFromDir(newPath);

        ArtsDiff ad = outPath.map(ArtsDiff::new).orElseGet(ArtsDiff::new);
        ad.diff(oldArt, newArt);
    }

    private static void diffHue(Path oldPath, Path newPath) throws IOException {
        Hues oldTiledata = UOFiles.loadHuesFromDir(oldPath);
        Hues newTiledata = UOFiles.loadHuesFromDir(newPath);

        HuesDiff hf = new HuesDiff();
        hf.diff(oldTiledata, newTiledata);
    }

    private static void diffCliLoc(Path oldPath, Path newPath) throws IOException {
        CliLocs oldCliLocs = UOFiles.loadCliLocsFromDir(oldPath);
        CliLocs newCliLocs = UOFiles.loadCliLocsFromDir(newPath);

        CliLocsDiff cd = new CliLocsDiff();
        cd.diff(oldCliLocs, newCliLocs);
    }

    private static void diffAnim(Path oldPath, Path newPath, Optional<Path> outPath) throws IOException {
        if(Files.isRegularFile(oldPath.resolve(UOFiles.ANIM1_IDX))){
            diffAnims(oldPath, newPath, outPath, 1);
        }
        if(Files.isRegularFile(oldPath.resolve(UOFiles.ANIM2_IDX))){
            diffAnims(oldPath, newPath, outPath, 2);
        }
        if(Files.isRegularFile(oldPath.resolve(UOFiles.ANIM3_IDX))){
            diffAnims(oldPath, newPath, outPath, 3);
        }
        if(Files.isRegularFile(oldPath.resolve(UOFiles.ANIM4_IDX))){
            diffAnims(oldPath, newPath, outPath, 4);
        }
        if(Files.isRegularFile(oldPath.resolve(UOFiles.ANIM5_IDX))){
            diffAnims(oldPath, newPath, outPath, 5);
        }
    }

    private static void diffAnims(Path oldPath, Path newPath, Optional<Path> outPath, int anim) throws IOException {
        Anims oldArt = UOFiles.loadAnimsFromDir(anim, oldPath);
        Anims newArt =  UOFiles.loadAnimsFromDir(anim, newPath);
        AnimDiff ad = outPath.map(p -> p.resolve("anim" + anim)).map(AnimDiff::new).orElseGet(AnimDiff::new);
        ad.diff(oldArt, newArt);
    }

    private static void diffMap(Path oldPath, Path newPath, Optional<Path> outPath) throws IOException {
        Map origMap = UOFiles.loadMapFromDir(oldPath);
        Map newMap = UOFiles.loadMapFromDir(newPath);

        MapDiff md = outPath.map(MapDiff::new).orElseGet(MapDiff::new);
        md.diff(origMap, newMap);
    }

    private static void diffSounds(Path oldPath, Path newPath, Optional<Path> outPath) throws IOException {
        Sounds origStatics = UOFiles.loadSoundsFromDir(oldPath);
        Sounds newStatics = UOFiles.loadSoundsFromDir(newPath);

        SoundsDiff sd = outPath.map(SoundsDiff::new).orElseGet(SoundsDiff::new);
        sd.diff(origStatics, newStatics);
    }

    private static void diffStatics(Path oldPath, Path newPath, Optional<Path> outPath) throws IOException {
        Statics origStatics = UOFiles.loadStaticsFromDir(oldPath);
        Statics newStatics = UOFiles.loadStaticsFromDir(newPath);

        StaticsDiff sd = outPath.map(StaticsDiff::new).orElseGet(StaticsDiff::new);
        sd.diff(origStatics, newStatics);
    }

}

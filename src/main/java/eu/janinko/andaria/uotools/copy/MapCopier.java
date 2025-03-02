package eu.janinko.andaria.uotools.copy;

import eu.janinko.andaria.ultimasdk.UOFiles;
import eu.janinko.andaria.ultimasdk.files.Map;
import eu.janinko.andaria.ultimasdk.files.Statics;
import eu.janinko.andaria.ultimasdk.files.map.MapTile;
import eu.janinko.andaria.ultimasdk.files.statics.Static;
import eu.janinko.andaria.ultimasdk.files.statics.StaticsBlock;
import eu.janinko.andaria.uotools.diff.MapDiff;
import static eu.janinko.andaria.uotools.diff.MapDiff.HEIGHT;
import static eu.janinko.andaria.uotools.diff.MapDiff.WIDTH;
import eu.janinko.andaria.uotools.diff.MapDifference;
import eu.janinko.andaria.uotools.diff.StaticsDiff;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 *
 * @author janinko
 */
public class MapCopier {

    private final MapDiff mapDiff;
    private final StaticsDiff staticsDiff;
    private final Path oldDir;
    private final Path newDir;
    private final Path targetDir;
    private final Path outputDir;

    private final MapDifference diff = new MapDifference();

    public MapCopier(Path oldDir, Path newDir, Path targetDir, Path outputDir) throws IOException {
        this.oldDir = oldDir;
        this.newDir = newDir;
        this.targetDir = targetDir;
        this.outputDir = outputDir;
        this.mapDiff = new MapDiff(outputDir);
        this.staticsDiff = new StaticsDiff(outputDir);
        generateDiff();
    }

    /**
     * Computes changes betwen oldDir maps and newDir maps and applies them to targetDir maps, saving the result in
     * outputDir.
     *
     * @throws IOException
     */
    public void copyChangesInMapToDifferentMap() throws IOException {
        // copy Map
        System.out.println("Coping map");
        try (Map targetMap = UOFiles.loadMapFromDir(targetDir); Map newMap = UOFiles.loadMapFromDir(newDir)) {
            copyMap(targetMap, newMap, diff);
            System.out.println("Saving map");
            UOFiles.saveToDir(targetMap, outputDir);
        }

        // copy Map
        System.out.println("Coping static");
        try (Statics targetStatics = UOFiles.loadStaticsFromDir(targetDir); Statics newStatics = UOFiles.loadStaticsFromDir(newDir)) {
            copyStatics(newStatics, targetStatics, diff);
            System.out.println("Saving statics");
            UOFiles.saveToDir(targetStatics, outputDir);
            newStatics.close();
        }
    }

    private void generateDiff() throws IOException {
        // diff Map
        try (Map oldMap = UOFiles.loadMapFromDir(oldDir); Map newMap = UOFiles.loadMapFromDir(newDir)) {
            System.out.println("Computing map diff");
            mapDiff.computeDifference(oldMap, newMap, diff);
        }

        // diff Static
        try (Statics oldStatics = UOFiles.loadStaticsFromDir(oldDir); Statics newStatics = UOFiles.loadStaticsFromDir(newDir)) {
            System.out.println("Computing static diff");
            staticsDiff.computeDifference(oldStatics, newStatics, diff);
        }
    }

    public void printChangesInMapBlocks() throws IOException {
        diff.paintImage(outputDir);
        diff.paintImageRough(outputDir);

        int tilesW = 2846 / 8 + 1;
        int tilesH = 2704 / 8 + 1;

        for (int x = 0; x < tilesW; x++) {
            BLOCK:
            for (int y = 0; y < tilesH; y++) {

                for (int tx = 0; tx < 8; tx++) {
                    for (int ty = 0; ty < 8; ty++) {
                        if (diff.getTile(x * 8 + tx, y * 8 + ty) != 0) {
                            System.out.println((x * 8 + 4) + "," + (y * 8 + 4));
                            continue BLOCK;
                        }
                    }
                }
            }
        }

    }

    private void copyMap(Map targetMap, Map newMap, MapDifference diff) throws IOException {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (diff.getTile(x, y) != 0) {
                    MapTile tile = newMap.getTile(x, y);
                    targetMap.setTile(x, y, tile);
                }
            }
        }
    }

    private void copyStatics(Statics sourceStatic, Statics targetStatics, MapDifference diff) throws IOException {
        int tilesW = WIDTH / 8;
        int tilesH = HEIGHT / 8;

        ArrayList<Static> toAdd = new ArrayList<>();
        for (int x = 0; x < tilesW; x++) {
            for (int y = 0; y < tilesH; y++) {
                StaticsBlock block = targetStatics.getBlock(x, y);

                boolean changed = false;
                for (int tx = 0; tx < 8; tx++) {
                    for (int ty = 0; ty < 8; ty++) {
                        final int px = x * 8 + tx;
                        final int py = y * 8 + ty;
                        if (diff.getTile(px, py) != 0) {
                            changed = true;
                            block.clearStaticsAt(px, py);
                            toAdd.addAll(sourceStatic.getStatics(px, py));
                            System.out.println("Copping static at position " + px + "," + py);
                        }
                    }
                }
                if (changed) {
                    for (Static aStatic : toAdd) {
                        block.addStatic(aStatic);
                    }
                    toAdd.clear();
                    targetStatics.setBlock(x, y, block);
                }
            }
        }
    }

    //############# vvv ## old stuff, used?? ## vvv ##########

    public static void main(String[] args) throws IOException {
        Path mapa = Paths.get("/home/janinko/Ultima/soubory/mapa");
        MapCopier mc = new MapCopier(mapa.resolve("mapa-2020-09-30 - preZima"), mapa.resolve("mapa-2021-11-05 - preZima"), mapa.resolve("mapa-2020-11-28 - zima"), mapa.resolve("out"));

        //mc.copyChangesInMapToDifferentMap();
        mc.printChangesInMapBlocks();
        //mc.copyStaticChangesFromFile(mapa.resolve("orig20"), mapa.resolve("zima20-wip"), mapa.resolve("opravena"), Paths.get("/tmp/nezasnezene-spatne-pozice"));
        //UOFiles filesOld = new UOFiles(mapa.resolve("79"));
        //UOFiles filesNew = new UOFiles(mapa.resolve("80"));
        //MapDifference diff = new MapDifference();
        //diff.setRect(MapDifference.STAT_MODIFIED, 1, 3500, 700, 4095);
        //final Statics target = filesNew.getStatics();
        //mc.copyStatics(filesOld.getStatics(), target, diff);
        //UOFiles.saveToDir(target, mapa.resolve("updated"));
    }

    public void copyStaticChangesFromFile(Path sourceDir, Path targetDir, Path outputDir, Path diffFile) throws IOException {
        MapDifference diff = loadDiffFromFile(diffFile, (byte) 255);
        try (Statics sourceStatics = UOFiles.loadStaticsFromDir(sourceDir); Statics targetStatics = UOFiles.loadStaticsFromDir(targetDir)) {
            copyStatics(sourceStatics, targetStatics, diff);

            System.out.println("Saving statics");
            UOFiles.saveToDir(targetStatics, outputDir);
        }
    }

    private MapDifference loadDiffFromFile(Path locationPaths, byte difference) throws IOException {
        MapDifference diff = new MapDifference();
        byte[][] diffMap = diff.getDiffMap();
        Files.lines(locationPaths).map(l -> l.split(",")).forEach(l -> {
            int x = Integer.parseInt(l[0]);
            int y = Integer.parseInt(l[1]);
            diffMap[x][y] = difference;
        });
        return diff;
    }

}

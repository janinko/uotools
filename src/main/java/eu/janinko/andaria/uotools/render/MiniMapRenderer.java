package eu.janinko.andaria.uotools.render;

import eu.janinko.andaria.ultimasdk.UOFiles;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import eu.janinko.andaria.ultimasdk.files.Map;
import eu.janinko.andaria.ultimasdk.files.Radarcol;
import eu.janinko.andaria.ultimasdk.files.Statics;
import eu.janinko.andaria.ultimasdk.files.TileData;
import eu.janinko.andaria.ultimasdk.graphics.impl.BasicBitmap;
import eu.janinko.andaria.ultimasdk.graphics.Color;
import eu.janinko.andaria.ultimasdk.tools.MiniMap;
import eu.janinko.andaria.uotools.dump.Dumper;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class MiniMapRenderer {
    
    public static void main(String[] args) {
        
        if (args.length < 4) {
            printHelp();
            System.exit(1);
        }
        try{
            Path files = Paths.get(args[0]);
            int width = Integer.parseInt(args[1]);
            int height = Integer.parseInt(args[2]);
            Path output = Paths.get(args[3]);
        
            MiniMap miniMap = getMiniMap(files);
            Color[][] map = miniMap.getMap(width, height);
            BasicBitmap bitmap = new BasicBitmap(map);
            Dumper.save(output.getParent(), output.getFileName(), bitmap.getImage());
        } catch (IOException | NumberFormatException ex) {
            System.err.println("Error: " + ex.getLocalizedMessage());
            System.exit(2);
        }
    }

    private static void printHelp() {
        System.out.println("Usage: MiniMapRenderer FILES_DIR WIDTH HEIGHT OUTPUT");
        System.out.println();
        System.out.println("FILES_DIR: directory with following files: map0.mul, statics0.mul, staidx0.mul, radarcol.mul, tiledata.mul");
        System.out.println("WIDTH: width of the map");
        System.out.println("HEIGHT: height of the map");
        System.out.println("OUTPUT: file where the map should be saved (without extension)");
    }

    private static MiniMap getMiniMap(Path files) throws IOException {
        Map map = UOFiles.loadMapFromDir(files);
        Statics statics = UOFiles.loadStaticsFromDir(files);
        Radarcol radarcol = UOFiles.loadRadarcolFromDir(files);
        TileData tiledata = UOFiles.loadTileDataFromDir(files);
        return new MiniMap(map, statics, radarcol, tiledata);
    }
}

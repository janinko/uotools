package eu.janinko.andaria.uotools.copy;

import eu.janinko.andaria.ultimasdk.UOFiles;
import eu.janinko.andaria.ultimasdk.files.Map;
import eu.janinko.andaria.ultimasdk.files.Statics;
import eu.janinko.andaria.ultimasdk.files.map.MapTile;
import eu.janinko.andaria.ultimasdk.files.statics.Static;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author janinko
 */
public class MapMover {
    
    public static void main(String[] args) throws IOException {
        UOFiles files = new UOFiles(Paths.get("/tmp/map/old"));
        
        Statics statics = files.getStatics();
        List<Static>[][] cutStatics = cutStatics(statics, 5585, 2747, 5605, 2767);
        pasteStatics(statics, 5585, 2730, cutStatics);
        Path idx = Paths.get("/tmp/map/new/staidx0.mul");
        Path mul = Paths.get("/tmp/map/new/statics0.mul");
        statics.save(Files.newOutputStream(idx), Files.newOutputStream(mul));
        statics.close();
        
        Map map = files.getMap();
        MapTile empty = new MapTile(580, (byte) 0, 0, 0);
        MapTile[][] cutMap = cutMap(map, 5585, 2747, 5605, 2767, empty);
        pasteMap(map, 5585, 2730, cutMap);
        Path map0 = Paths.get("/tmp/map/new/map0.mul");
        map.save(Files.newOutputStream(map0));
        
    }
    private static MapTile[][] cutMap(Map map, int x1, int y1, int x2, int y2, MapTile empty) throws IOException {
        if(x2 < x1) throw new IllegalArgumentException("x2 < x1");
        if(y2 < y1) throw new IllegalArgumentException("y2 < y1");
        MapTile[][] cutMap = new MapTile[x2-x1+1][y2-y1+1];
        for(int x = x1; x <= x2; x++){
            for (int y = y1; y <= y2; y++) {
                cutMap[x-x1][y-y1] = map.getTile(x, y);
                map.setTile(x, y, empty);
            }
        }
        return cutMap;
    }
    
    private static void pasteMap(Map map, int px, int py, MapTile[][] cutMap) throws IOException {
        int xx = cutMap.length;
        int yy = cutMap[0].length;
        
        for (int dx = 0; dx < xx; dx++) {
            int x = px + dx;
            for (int dy = 0; dy < yy; dy++) {
                int y = py + dy;
                map.setTile(x, y, cutMap[dx][dy]);
            }
        }
    }

    private static List<Static>[][] cutStatics(Statics statics, int x1, int y1, int x2, int y2) throws IOException {
        if(x2 < x1) throw new IllegalArgumentException("x2 < x1");
        if(y2 < y1) throw new IllegalArgumentException("y2 < y1");
        List<Static>[][] cutStatic = new List[x2-x1+1][y2-y1+1];
        for(int x = x1; x <= x2; x++){
            for (int y = y1; y <= y2; y++) {
                cutStatic[x-x1][y-y1] = statics.getStatics(x, y);
                statics.setStatics(x, y, Collections.emptyList());
            }
        }
        return cutStatic;
    }

    private static void pasteStatics(Statics statics, int px, int py, List<Static>[][] cutStatics) throws IOException {
        int xx = cutStatics.length;
        int yy = cutStatics[0].length;
        
        for (int dx = 0; dx < xx; dx++) {
            int x = px + dx;
            for (int dy = 0; dy < yy; dy++) {
                int y = py + dy;
                statics.setStatics(x, y, cutStatics[dx][dy]);
            }
        }
    }
    
}

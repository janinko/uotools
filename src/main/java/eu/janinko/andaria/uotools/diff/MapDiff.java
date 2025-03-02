package eu.janinko.andaria.uotools.diff;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import eu.janinko.andaria.ultimasdk.files.Map;
import eu.janinko.andaria.ultimasdk.files.map.MapTile;

/**
 *
 * @author Honza Brázdil &lt;jbrazdil@redhat.com&gt;
 */
public class MapDiff extends AbstractFileDiff<Map> {

    public static final int WIDTH = 7168;
    public static final int HEIGHT = 4096;

    public MapDiff() {
    }

    public MapDiff(Path outputPath) {
        super(outputPath);
    }

    public MapDiff(PrintStream textOutput) {
        super(textOutput);
    }

    public MapDiff(Path outputPath, PrintStream textOutput) {
        super(outputPath, textOutput);
    }

    @Override
    public void diff(Map left, Map right) throws IOException {
        if (outputPath == null) {
            perform(left, right, _printDiff());
        } else {
            final MapDifference diff = new MapDifference();
            computeDifference(left, right, diff);
            diff.paintImage(outputPath);
        }
    }

    private MapOperation _printDiff() {
        return (x, y, leftTile, rightTile) -> {
            if (leftTile.getId() != rightTile.getId() || leftTile.getAlt() != rightTile.getAlt()) {
                textOutput.println((x) + "," + (y) + ": id: " + leftTile.getId() + " -> " + rightTile.getId() + " alt: " + leftTile.getAlt() + " -> " + rightTile.getAlt());
            }
        };
    }

    private MapOperation _computeDifference(MapDifference diff) {
        byte[][] diffMap = diff.getDiffMap();
        return (x, y, leftTile, rightTile) -> {
            if (leftTile.getId() != rightTile.getId()) {
                diffMap[x][y] |= MapDifference.MAP_TILE;
            }
            if (leftTile.getAlt() != rightTile.getAlt()) {
                diffMap[x][y] |= MapDifference.MAP_ALT;
            }
        };
    }

    public void computeDifference(Map left, Map right, MapDifference diff) throws IOException {
        perform(left, right, _computeDifference(diff));
    }

    public void perform(Map left, Map right, MapOperation operation) throws IOException {
        int tilesW = WIDTH / 8;
        int tilesH = HEIGHT / 8;

        for (int bx = 0; bx < tilesW; bx++) {
            for (int by = 0; by < tilesH; by++) {
                MapTile[][] leftTiles = left.getBlock(bx, by).getTiles();
                MapTile[][] rightTiles = right.getBlock(bx, by).getTiles();
                for (int tx = 0; tx < 8; tx++) {
                    for (int ty = 0; ty < 8; ty++) {
                        MapTile leftTile = leftTiles[tx][ty];
                        MapTile rightTile = rightTiles[tx][ty];
                        final int x = bx * 8 + tx;
                        final int y = by * 8 + ty;
                        operation.consume(x, y, leftTile, rightTile);
                    }
                }
            }
        }
    }

    @FunctionalInterface
    private interface MapOperation {

        void consume(int x, int y, MapTile leftTile, MapTile rightTile);
    }

}

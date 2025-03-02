package eu.janinko.andaria.uotools.diff;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eu.janinko.andaria.ultimasdk.files.Statics;
import eu.janinko.andaria.ultimasdk.files.statics.Static;
import static eu.janinko.andaria.uotools.diff.MapDiff.HEIGHT;
import static eu.janinko.andaria.uotools.diff.MapDiff.WIDTH;

/**
 *
 * @author Honza Brázdil &lt;jbrazdil@redhat.com&gt;
 */
public class StaticsDiff extends AbstractFileDiff<Statics> {
    public StaticsDiff() {
    }

    public StaticsDiff(Path outputPath) {
        super(outputPath);
    }

    public StaticsDiff(PrintStream textOutput) {
        super(textOutput);
    }

    public StaticsDiff(Path outputPath, PrintStream textOutput) {
        super(outputPath, textOutput);
    }

    @Override
    public void diff(Statics left, Statics right) throws IOException {
        if (outputPath == null) {
            perform(left, right, _printDiff());
        } else {
            final MapDifference diff = new MapDifference();
            computeDifference(left, right, diff);
            diff.paintImage(outputPath);
        }
    }

    private StaticOperation _printDiff() {
        return (x, y, leftTile, rightTile) -> {
            if (leftTile != null && rightTile != null) {
                Iterator<Static> lit = leftTile.iterator();
                while (lit.hasNext()) {
                    Static search = lit.next();
                    Iterator<Static> rit = rightTile.iterator();
                    while (rit.hasNext()) {
                        Static next = rit.next();
                        if (search.equalsStatic(next)) {
                            lit.remove();
                            rit.remove();
                            break;
                        }
                    }
                }
            }

            if (leftTile != null) {
                for (Static statik : leftTile) {
                    textOutput.println((x) + "," + (y) + ": - id:" + statik.getId() + " c: " + statik.getColor() + " z: " + statik.getZ());
                }
            }
            if (rightTile != null) {
                for (Static statik : rightTile) {
                    textOutput.println((x) + "," + (y) + ": + id:" + statik.getId() + " c: " + statik.getColor() + " z: " + statik.getZ());
                }
            }
        };
    }

    private StaticOperation _computeDifference(MapDifference diff) {
        byte[][] diffMap = diff.getDiffMap();
        return (x, y, leftTile, rightTile) -> {
            if (leftTile == null || rightTile == null) {
                if (leftTile != null) {
                    diffMap[x][y] |= MapDifference.STAT_REMOVED;
                } else if (rightTile != null) {
                    diffMap[x][y] |= MapDifference.STAT_ADDED;
                }
            } else {
                Iterator<Static> lit = leftTile.iterator();
                while (lit.hasNext()) {
                    Static search = lit.next();
                    Iterator<Static> rit = rightTile.iterator();
                    while (rit.hasNext()) {
                        Static next = rit.next();
                        if (search.equalsStatic(next)) {
                            lit.remove();
                            rit.remove();
                            break;
                        }
                    }
                }
                if (!leftTile.isEmpty() || !rightTile.isEmpty()) {
                    if (leftTile.isEmpty()) {
                        diffMap[x][y] |= MapDifference.STAT_ADDED;
                    } else if (rightTile.isEmpty()) {
                        diffMap[x][y] |= MapDifference.STAT_REMOVED;
                    } else {
                        diffMap[x][y] |= MapDifference.STAT_MODIFIED;
                    }
                }
            }
        };
    }

    public void computeDifference(Statics left, Statics right, MapDifference diff) throws IOException {
        StaticOperation operatiom = _computeDifference(diff);
        perform(left, right, operatiom);
    }

    private void perform(Statics left, Statics right, StaticOperation operatiom) throws IOException {
        int tilesW = WIDTH / 8;
        int tilesH = HEIGHT / 8;

        for (int bx = 0; bx < tilesW; bx++) {
            for (int by = 0; by < tilesH; by++) {
                List<Static> leftTiles = left.getBlock(bx, by).getStatics();
                List<Static> rightTiles = right.getBlock(bx, by).getStatics();

                List<Static>[][] leftStatics = divideStatics(leftTiles);
                List<Static>[][] rightStatics = divideStatics(rightTiles);

                for (int tx = 0; tx < 8; tx++) {
                    for (int ty = 0; ty < 8; ty++) {
                        List<Static> leftTile = leftStatics[tx][ty];
                        List<Static> rightTile = rightStatics[tx][ty];
                        final int x = bx * 8 + tx;
                        final int y = by * 8 + ty;
                        operatiom.consume(x, y, leftTile, rightTile);
                    }
                }
            }
        }
    }

    private List<Static>[][] divideStatics(List<Static> tile) {
        List<Static>[][] statics = new List[8][8];
        for (Static statik : tile) {
            int tx = statik.getX() % 8;
            int ty = statik.getY() % 8;
            if (statics[tx][ty] == null) {
                statics[tx][ty] = new ArrayList<>();
            }
            statics[tx][ty].add(statik);
        }
        return statics;
    }

    @FunctionalInterface
    private interface StaticOperation {

        void consume(int x, int y, List<Static> leftTile, List<Static> rightTile);
    }

}

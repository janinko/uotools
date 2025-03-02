package eu.janinko.andaria.uotools.diff;

import eu.janinko.andaria.uotools.dump.Dumper;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

/**
 *
 * @author janinko
 */
public class MapDifference {

    public static final byte MAP_TILE = 0x1;
    public static final byte MAP_ALT = 0x2;
    public static final byte STAT_REMOVED = 0x4;
    public static final byte STAT_ADDED = 0x8;
    public static final byte STAT_MODIFIED = 0x10;
    private byte[][] diffMap = new byte[MapDiff.WIDTH][MapDiff.HEIGHT];

    public byte[][] getDiffMap() {
        return diffMap;
    }

    public byte getTile(int x, int y) {
        return diffMap[x][y];
    }

    public void setRect(byte diff, int x1, int y1, int x2, int y2) {
        if (x1 > x2) {
            throw new IllegalArgumentException("x₁ larger then x₂");
        }
        if (y1 > y2) {
            throw new IllegalArgumentException("y₁ larger then y₂");
        }
        for (int x = x1; x <= x2; x++) {

            for (int y = y1; y <= y2; y++) {
                diffMap[x][y] = diff;
            }
        }
    }

    public void paintImage(Path outputPath) throws IOException {
        BufferedImage image = new BufferedImage(MapDiff.WIDTH, MapDiff.HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);

        for (int x = 0; x < MapDiff.WIDTH; x++) {
            for (int y = 0; y < MapDiff.HEIGHT; y++) {
                byte tile = getTile(x, y);
                Color color;
                if ((tile & MapDifference.STAT_MODIFIED) != 0) {
                    color = Color.YELLOW;
                } else if ((tile & MapDifference.STAT_ADDED) != 0) {
                    color = Color.GREEN;
                } else if ((tile & MapDifference.STAT_REMOVED) != 0) {
                    color = Color.RED;
                } else if ((tile & MapDifference.MAP_TILE) != 0) {
                    color = Color.BLUE;
                } else if ((tile & MapDifference.MAP_ALT) != 0) {
                    color = Color.CYAN;
                } else {
                    continue;
                }
                image.setRGB(x, y, color.getRGB());
            }
        }
        Dumper.save(outputPath, "map-diff", image);
    }

    public void paintImageRough(Path outputPath) throws IOException {
        int tilesW = MapDiff.WIDTH / 8;
        int tilesH = MapDiff.HEIGHT / 8;

        BufferedImage image = new BufferedImage(MapDiff.WIDTH, MapDiff.HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);

        for (int bx = 0; bx < tilesW; bx++) {
            for (int by = 0; by < tilesH; by++) {
                byte tile = 0;
                for (int tx = 0; tx < 8; tx++) {
                    for (int ty = 0; ty < 8; ty++) {
                        int x = bx * 8 + tx;
                        int y = by * 8 + ty;
                        tile |= getTile(x, y);
                    }
                }
                Color color;
                if ((tile & MapDifference.STAT_MODIFIED) != 0) {
                    color = Color.YELLOW;
                } else if ((tile & MapDifference.STAT_ADDED) != 0) {
                    color = Color.GREEN;
                } else if ((tile & MapDifference.STAT_REMOVED) != 0) {
                    color = Color.RED;
                } else if ((tile & MapDifference.MAP_TILE) != 0) {
                    color = Color.BLUE;
                } else if ((tile & MapDifference.MAP_ALT) != 0) {
                    color = Color.CYAN;
                } else {
                    continue;
                }
                for (int tx = 0; tx < 8; tx++) {
                    for (int ty = 0; ty < 8; ty++) {
                        int x = bx * 8 + tx;
                        int y = by * 8 + ty;
                        image.setRGB(x, y, color.getRGB());
                    }
                }
            }
        }

        Dumper.save(outputPath, "map-diff-rough", image);
    }
}

package eu.janinko.andaria.uotools.importer;

import eu.janinko.andaria.ultimasdk.UOFiles;
import eu.janinko.andaria.ultimasdk.files.arts.Art;
import eu.janinko.andaria.ultimasdk.files.gumps.Gump;
import eu.janinko.andaria.ultimasdk.graphics.impl.WritableBitmap;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageImport {

    private static Matcher filenameMatcher = Pattern.compile("0x[0-9a-f]+\\.png|[0-9]+\\.png").matcher("");
    UOFiles files;

    public ImageImport(Path origDir) {
        files = new UOFiles(origDir);
    }

    public void importGumps(Path images, Path output) throws IOException {
        Files.walk(images, 1).filter(ImageImport::isNumericFile).forEach(this::importGump);
        UOFiles.saveToDir(files.getGumps(), output);
    }

    private void importGump(Path image) {
        try {
            int id = parseID(image);
            Gump gump = new Gump(new WritableBitmap(ImageIO.read(image.toFile())));
            files.getGumps().setGump(id, gump);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void importArts(Path images, Path output) throws IOException {
        Files.walk(images, 1).filter(ImageImport::isNumericFile).forEach(this::importArt);
        UOFiles.saveToDir(files.getArts(), output);
    }

    private void importArt(Path image) {
        try {
            int id = parseID(image);
            Art art = new Art(ImageIO.read(image.toFile()));
            files.getArts().setStatic(id, art);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static int parseID(Path image) {
        String filename = image.getFileName().toString();
        String number = filename.substring(0, filename.length() - 4);
        int id;
        if (number.startsWith("0x")) {
            id = Integer.parseInt(number.substring(2), 16);
        } else {
            id = Integer.parseInt(number, 10);
        }
        return id;
    }

    private static boolean isNumericFile(Path path) {
        String filename = path.getFileName().toString();
        filenameMatcher.reset(filename);
        return filenameMatcher.matches();
    }
}

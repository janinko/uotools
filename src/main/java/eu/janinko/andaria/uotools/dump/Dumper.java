package eu.janinko.andaria.uotools.dump;

import eu.janinko.andaria.ultimasdk.UOFiles;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalInt;

import eu.janinko.andaria.ultimasdk.files.Anims;
import eu.janinko.andaria.ultimasdk.files.Arts;
import eu.janinko.andaria.ultimasdk.files.Fonts;
import eu.janinko.andaria.ultimasdk.files.Gumps;
import eu.janinko.andaria.ultimasdk.files.Hues;
import eu.janinko.andaria.ultimasdk.files.TileData;
import eu.janinko.andaria.ultimasdk.files.UniFonts;
import eu.janinko.andaria.ultimasdk.files.anims.Anim;
import eu.janinko.andaria.ultimasdk.files.anims.Body;
import eu.janinko.andaria.ultimasdk.files.anims.Frame;
import eu.janinko.andaria.ultimasdk.files.arts.Art;
import eu.janinko.andaria.ultimasdk.files.fonts.CharImg;
import eu.janinko.andaria.ultimasdk.files.fonts.Font;
import eu.janinko.andaria.ultimasdk.graphics.Color;
import eu.janinko.andaria.ultimasdk.files.gumps.Gump;
import eu.janinko.andaria.ultimasdk.files.hues.Hue;
import eu.janinko.andaria.ultimasdk.files.tiledata.ItemData;
import eu.janinko.andaria.ultimasdk.files.tiledata.TileFlag;
import eu.janinko.andaria.ultimasdk.files.unifont.UniCharImg;
import eu.janinko.andaria.ultimasdk.graphics.Bitmap;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class Dumper {
    private final UOFiles files;
    private final Path outPath;


    public Dumper(Path filePath, Path outPath) {
        this.files = new UOFiles(filePath);
        this.outPath = outPath;
    }

	public void generateArts() throws IOException{
        Arts arts = files.getArts();
        Path dir = outPath.resolve("arts");
		for (int i = 0; i<0x3FFF; i++) {
			Art art = arts.getStatic(i);
			if(art == null){
				continue;
			}
			BufferedImage image = art.getImage();
			if (image != null) {
                save(dir, i, image);
			}
		}
	}


    public void generateAllArts() throws IOException{
        Arts arts = files.getArts();
        Path dir = outPath.resolve("arts");
        for (int i = 0; i<arts.count(); i++) {
            //System.out.println(i);
            Art art = arts.get(i);
            if(art == null){
                continue;
            }
            BufferedImage image = art.getImage();
            if (image != null) {
                save(dir, i, image);
            }
        }
    }

    public void generateArt(int id, int hueID, String filename) throws IOException{
        Arts arts = files.getArts();
        Art art = arts.getStatic(id);
        if(art == null) throw new IllegalArgumentException("Art " + id + " doesn't exist.");
        Bitmap bitmap = art.getBitmap();
        if(hueID != 0){
            TileData tiledata = files.getTileData();
            Hues hues = files.getHues();
            ItemData tile = tiledata.getItem(id);
            bitmap.hue(hues.get(hueID), tile.getFlags().contains(TileFlag.PartialHue));
        }
        save(outPath, filename, bitmap.getImage());
    }

    public void generateArtHues(int id) throws IOException {
        Arts arts = files.getArts();
        Hues hues = files.getHues();

        Path dir = outPath.resolve("arts/hued/" + id);
        Art a = arts.getStatic(id);
        for (int i = 1; i <= 3000; i++) {
            Hue hue = hues.get(i);
            Bitmap b = a.getBitmap();
            b.hue(hue, false);
            BufferedImage image = b.getImage();
            if (image != null) {
                Dumper.save(dir, i, image);
            }
        }
    }

    public void generateHueStripes() throws IOException {
        Hues hues = files.getHues();
        Path dir = outPath.resolve("hues");
        for (int i = 1; i <= 3000; i++) {
            Hue h = hues.get(i);
            BufferedImage image = new BufferedImage(32, 16, BufferedImage.TYPE_4BYTE_ABGR);

            for (int x = 0; x < 32; x++) {
                Color c = h.getColor(x);
                for (int y = 0; y < 16; y++) {
                    image.setRGB(x, y, c.getAGBR());
                }
            }

            Dumper.save(dir, i + "hue", image);
        }
    }

    public void generateBodyAnim(int body) throws IOException {
        Path dir = outPath.resolve("anims").resolve(String.valueOf(body));
        Anims anims = files.getAnims();
        Body bodyAnim = anims.getBody(body);
        for (Body.Action action : bodyAnim.getActions()) {
            for (Body.Direction direction : Body.Direction.values()) {
                Anim anim = bodyAnim.getAnim(action, direction);
                int j = 22000 + ((body - 200) * 65) + action.getOffset()*5 + direction.getOffset();
                if(anim != null){
                    for (int i = 0; i < anim.frameCount(); i++) {
                        Frame f = anim.getFrame(i);
                        Dumper.save(dir, body + "-" + j + "-" + action + "_" + direction + "-" + i, f.getCenteredImage());
                    }
                }
            }
        }
    }

    public void doAnimStay(int animFile, int body, OptionalInt hue) throws IOException {
        Path dir = outPath.resolve("anims/stay").resolve(String.valueOf(body));
        Hues hues = files.getHues();
        Anims anims = files.getAnims(animFile);
        Anim anim = anims.getAnim(body, Anims.STAY, Body.Direction.SOUTH);
        if (anim == null) {
            throw new IllegalArgumentException("Unknown body");
        }
        Frame f = anim.getFrame(0);
        if (f == null) {
            throw new IllegalArgumentException("Unknown frame");
        }
        Bitmap bitmap = f.getBitmap();
        hue.ifPresent(h -> bitmap.hue(hues.get(h), false));
        int h = hue.orElse(0);
        BufferedImage image = bitmap.getImage();
        if (image != null) {
            Dumper.save(dir, body + "-" + h, image);
        }
    }

    public void doAnimWalk(int andimFile, int body, int order, Optional<Integer> hue) throws IOException {
        Path dir = outPath.resolve("anims/walk/" + order).resolve(String.valueOf(body));
        Hues hues = files.getHues();
        Anims anims = files.getAnims(andimFile);
        Anim anim = anims.getAnim(body, Anims.WALK, Body.Direction.SOUTH);
        if (anim == null) {
            throw new IllegalArgumentException("Unknown body");
        }
        for (int i = 0; i < anim.frameCount(); i++) {
            Frame f = anim.getFrame(i);
            BufferedImage image = hue.map(hues::get)
                    .map(f::getCenteredImage)
                    .orElseGet(f::getCenteredImage);
            if (image != null) {
                Dumper.save(dir, body + "-" + hue.orElse(0) + "." + i, image);
            }
        }
    }

    public void doAnimsStay() throws IOException {
        for (int a = 1; a <= 5; a++) {
            Path dir = outPath.resolve("anims" + a + "/stay");
            Anims anims = files.getAnims(a);

            int animCount = anims.count();
            System.out.println("Anim count for " + a + ": " + animCount);
            for (int i = 0; i < animCount; i++) {
                Anim anim = anims.getAnim(i, Anims.STAY, Body.Direction.SOUTH);
                if (anim == null) {
                    continue;
                }
                Frame f = anim.getFrame(0);
                if (f == null) {
                    continue;
                }
                BufferedImage image = f.getImage();
                if (image != null) {
                    Dumper.save(dir, i, image);
                }
            }
        }
    }

	public void generateGumpPaperdol() throws IOException{
        TileData tiledata = files.getTileData();
        Gumps gumps = files.getGumps();
        Path dir = outPath.resolve("gumps/paperdoll");
		boolean partialHue = false;
		for (ItemData item : tiledata.getItems()) {
			if (item.getAnimation() != 0
					&& (!partialHue || item.getFlags().contains(TileFlag.PartialHue))) {
				int animid = 0xffff & item.getAnimation();
				System.out.println(Integer.toHexString(item.getId()) + ": " + item.getName()
						+ " - " + animid + " (" + item.getAnimation() + ") " + Integer.toHexString(animid)
                        + "\t" + item.getFlags().toString());
                if (animid > 1000) {
                    System.out.println("XXXXX");
                    continue;
                }
				Gump g = gumps.get(50000 + animid);
				if(g == null){
					System.out.println("null");
					continue;
				}
				BufferedImage image = g.getImage();
				if (image != null) {
                    Dumper.save(dir, (50000 + animid), image);
				}
			}
		}
	}

	public void generateGumpPaperdolHued(int hueID) throws IOException{
        TileData tiledata = files.getTileData();
        Gumps gumps = files.getGumps();
        Hues hues = files.getHues();
        Hue hue = hues.get(hueID);
        Path dir = outPath.resolve("gumps/paperdoll");
		for (ItemData item : tiledata.getItems()) {
			if (item.getAnimation() != 0){
                int animid = 0xffff & item.getAnimation();
				System.out.println(Integer.toHexString(item.getId()) + ": " + item.getName()
						+ " - " + animid + " (" + item.getAnimation() + ") " + Integer.toHexString(animid)
						+ "\t" + item.getFlags().toString());
				Gump g = gumps.get(50000 + animid);
				if(g == null){
					System.out.println("null");
					continue;
				}
                Bitmap b = g.getBitmap();
                b.hue(hue, item.getFlags().contains(TileFlag.PartialHue));
				BufferedImage image = b.getImage();
				if (image != null) {
                    Dumper.save(dir, (50000 + animid), image);
				}
            }
		}
	}

	public void generateGumpPaperdolHued(int id, int hueID, String filename) throws IOException{
        Gumps gumps = files.getGumps();
        Hues hues = files.getHues();
        Hue hue = hues.get(hueID);
        Path dir = outPath.resolve("gumps/paperdoll-hued");
        Gump g = gumps.get(50000 + id);
        Bitmap b = g.getBitmap();
        b.hue(hue, false);
        BufferedImage image = b.getImage();
        Dumper.save(dir, filename, image);
	}

	public void generateGumpPaperdolHues(int id) throws IOException{
        Gumps gumps = files.getGumps();
        Hues hues = files.getHues();

        Path dir = outPath.resolve("gumps/paperdoll-hued/" + id);
        Gump g = gumps.get(50000 + id);
		for (int i = 1; i<=3000; i++) {
            Hue hue = hues.get(i);
            Bitmap b = g.getBitmap();
            b.hue(hue, false);
			BufferedImage image = b.getImage();
			if (image != null) {
                Dumper.save(dir, i, image);
			}
		}
	}

	public void generateFonts() throws IOException{
        Fonts fonts = files.getFonts();
        Path fontsDir = outPath.resolve("fonts");
        for(int f = 0;f<fonts.count(); f++){
            Path dir = fontsDir.resolve("" + f);
            Font font = fonts.get(f);
            for (char c = 32; c<256; c++) {
                CharImg chr = font.get(c);
                if(chr == null){
                    continue;
                }
                BufferedImage image = chr.getImage();
                if (image != null) {
                    save(dir, (int) chr.getId(), image);
                }
            }
        }
	}
    
    public void generateUnifont(int i) throws IOException {
        UniFonts fonts = files.getUniFonts(i);
        Path fontsDir = outPath.resolve("unifonts");
        Path dir = fontsDir.resolve("" + i);

        for (int j = 0; j < fonts.count(); j++) {
            UniCharImg chr = fonts.get(j);
            if (chr == null) {
                continue;
            }
            BufferedImage image = chr.getImage();
            if (image != null) {
                save(dir, (int) chr.getId(), image);
            }
        }

    }

    public static void save(Path dir, Object name, BufferedImage image) throws IOException {
        Files.createDirectories(dir);
        Path out = dir.resolve(name+".png");
        try (final OutputStream os = Files.newOutputStream(out)) {
            ImageIO.write(image, "png", os);
        }
    }
}

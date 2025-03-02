package eu.janinko.andaria.uotools.diff;

import java.io.IOException;


/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 * @param <T> Type of the file.
 */
public interface FileDiff<T> {

    void diff(T left, T right) throws IOException;
}

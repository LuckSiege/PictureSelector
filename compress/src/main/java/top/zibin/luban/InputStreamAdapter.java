package top.zibin.luban;

import java.io.IOException;
import java.io.InputStream;

import top.zibin.luban.io.ArrayPoolProvide;

/**
 * Automatically close the previous InputStream when opening a new InputStream,
 * and finally need to manually call {@link #close()} to release the resource.
 */
public abstract class InputStreamAdapter implements InputStreamProvider {

    @Override
    public InputStream open() throws IOException {
        return openInternal();
    }

    public abstract InputStream openInternal() throws IOException;

    @Override
    public void close() {
        ArrayPoolProvide.getInstance().clearMemory();
    }
}
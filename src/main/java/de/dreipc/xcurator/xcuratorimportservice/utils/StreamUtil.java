package de.dreipc.xcurator.xcuratorimportservice.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtil {

    public static InputStream getInputStream(URL url) throws IOException {
        URLConnection urlConnection = url.openConnection();
        return urlConnection.getInputStream();
    }

    public static <T> Stream<T> stream(Iterable<T> itor) {
        return StreamSupport.stream(itor.spliterator(), false);
    }

    public static Stream<URL> getUrlStreams(int total  , LimitOffsetURLFunction function) {
        var itemProBatch = total < 2000 ? (float) total : 2000f;
        var numBatches = Math.round(total / itemProBatch);
        return IntStream.range(0, numBatches).mapToObj(batchIndex -> {
            int offset = batchIndex * (int) itemProBatch;
            int limit = Math.abs(Math.min(total - offset, (int) itemProBatch));
            return function.execute(limit, offset);
        });
    }

   public interface LimitOffsetURLFunction {
        URL execute(int limit, int offset);
    }

}

package karate.downloadactiondetail;

import java.util.function.Function;

public class FileDataComparator {

    private static final String tagStartData = "stream";
    private static final String tagEndData = "endstream";

    public static Boolean compareFileData(byte[] originalFile, byte[] responseFile) {

        Function<byte[], Integer> findIndex = (file) -> new String(file).indexOf(tagStartData);
        Function<byte[], Integer> findEndIndex = (file) -> new String(file).indexOf(tagEndData);

        Function<byte[], String> getData = (file) -> {
            int startPosition = findIndex.apply(file);
            int endPosition = findEndIndex.apply(file);
            if (startPosition <0 || endPosition <0) {
                return new String();
            }
            return new String(file).substring(startPosition, endPosition);
        };

        return getData.apply(originalFile).equals(getData.apply(responseFile));
    }
}
package tr.otunctan.utils;

public final class FileUtils {


    public static String sizeInMegaBytesString(long length) {
        return sizeInMegaBytes(length) + " MB";
    }

    public static double sizeInMegaBytes(long length) {
        return (double) length / (1024 * 1024);
    }

    public static String sizeInKiloBytesString(long length) {
        return sizeInKiloBytes(length) + " KB";
    }

    public static double sizeInKiloBytes(long length) {
        return (double) length / 1024;
    }

    public static String sizeInBytes(long length) {
        return length + " BYTES";
    }


}

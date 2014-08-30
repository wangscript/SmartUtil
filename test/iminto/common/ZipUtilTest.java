package iminto.common;
import iminto.io.ZipUtil;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

public class ZipUtilTest {
	public static void main(String[] args) throws IOException {
		File file=new File("h:\\desk\\code\\debian.txt");
		ZipUtil.zip(file);
		File zipFile = new File("h:\\desk\\test.zip");
		ZipOutputStream zos = ZipUtil.createZip(zipFile);
		ZipUtil.addToZip(zos).file("h:\\desk\\code").path("sbdata").comment("This is sb data file").add();
	}

}

package expense_tally.ocr;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

public class TestDriver {
  public static void main(String args[]) {
    final String testPictureFileName = "D:\\Dropbox\\Camera Uploads\\2019-05-18 21.19.40.png";

    // Set environment variable
//    Map<String, String> environmentvariable = new HashMap<>(System.getenv());
//    environmentvariable.put("TESSDATA_PREFIX", "D:\\code\\expense-tally\\src\\main\\resource\\");
//    try {
//      setEnv(environmentvariable);
//    } catch (Exception e) {
//      e.printStackTrace();
//    }

    TessBaseAPI tessBaseAPI = new TessBaseAPI();
    // Initialize tesseract-ocr with English, without specifying tessdata path
    if (tessBaseAPI.Init(null, "eng") != 0) {
      System.err.println("Could not initialize tesseract.");
      System.exit(1);
    }

    // Open input image with leptonica library
    PIX image = org.bytedeco.leptonica.global.lept.pixRead(testPictureFileName);
    tessBaseAPI.SetImage(image);

    // Get OCR result
    BytePointer ocrResultBytePointer = tessBaseAPI.GetUTF8Text();
    System.out.println(ocrResultBytePointer.getString());

    tessBaseAPI.End();
    ocrResultBytePointer.deallocate();
    org.bytedeco.leptonica.global.lept.pixDestroy(image);
  }

  protected static void setEnv(Map<String, String> newenv) throws Exception {
    try {
      Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
      Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
      theEnvironmentField.setAccessible(true);
      Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
      env.putAll(newenv);
      Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
      theCaseInsensitiveEnvironmentField.setAccessible(true);
      Map<String, String> cienv = (Map<String, String>)     theCaseInsensitiveEnvironmentField.get(null);
      cienv.putAll(newenv);
    } catch (NoSuchFieldException e) {
      Class[] classes = Collections.class.getDeclaredClasses();
      Map<String, String> env = System.getenv();
      for(Class cl : classes) {
        if("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
          Field field = cl.getDeclaredField("m");
          field.setAccessible(true);
          Object obj = field.get(env);
          Map<String, String> map = (Map<String, String>) obj;
          map.clear();
          map.putAll(newenv);
        }
      }
    }
  }
}

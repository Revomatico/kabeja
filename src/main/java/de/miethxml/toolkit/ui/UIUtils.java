package de.miethxml.toolkit.ui;

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class UIUtils {
  public static byte[] resourceToBytes(final String resource) {
    return resourceToBytes(de.miethxml.toolkit.ui.UIUtils.class, resource);
  }

  public static byte[] resourceToBytes(final Class clazz, final String resource) {
    final InputStream in = clazz.getResourceAsStream(resource);
    final ByteArrayOutputStream bo = new ByteArrayOutputStream();
    final byte[] b = new byte[1024];
    int pos = -1;
    try {
      while ((pos = in.read(b)) > -1) {
        bo.write(b, 0, pos);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bo.toByteArray();
  }
}
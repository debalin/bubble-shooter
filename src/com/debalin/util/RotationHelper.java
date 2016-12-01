package com.debalin.util;

import processing.core.PConstants;

public class RotationHelper {

  public static float mapToRange(float rotation) {
    float r = rotation % (2 * PConstants.PI);
    if (Math.abs(r) <= Math.PI)
      return r;
    else {
      if (r > Math.PI)
        return (r - 2 * PConstants.PI);
      else
        return (r + 2 * PConstants.PI);
    }
  }

}

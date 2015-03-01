package com.launch.twinkle.twinkle;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Utils {

  public static TreeMap<String, String> sortByValue(HashMap<String, String> map) {
    ValueComparator vc =  new ValueComparator(map);
    TreeMap<String, String> sortedMap = new TreeMap<String, String>(vc);
    sortedMap.putAll(map);
    return sortedMap;
  }

  static class ValueComparator implements Comparator<String> {

    Map<String, String> map;

    public ValueComparator(Map<String, String> base) {
      this.map = base;
    }

    public int compare(String a, String b) {
      return map.get(a).compareTo(map.get(b));
    }
  }
}

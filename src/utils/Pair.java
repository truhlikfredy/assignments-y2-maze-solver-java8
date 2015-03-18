package utils;

/**
 * Structure to hold tuples
 * 
 * @author Anton Krug
 * @date 2015/02/20
 * @version 0.2
 */

public class Pair<firstType, secondType> {
  public final firstType first;
  public final secondType second;

  public Pair(firstType first, secondType second) {
    this.first = first;
    this.second = second;
  }
}
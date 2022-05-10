package game.server;

public class Setting<T extends Number> {


  private String name;
  private T val;

  public Setting (String name, T val) {
      this.name = name;
      this.val = val;
  }

  public String getName() {
    return name;
  }

  public Setting<T> setName(String name) {
    this.name = name;
    return this;
  }

  public T getVal() {
    return val;
  }

  public Setting<T> setVal(T val) {
    this.val = val;
    return this;
  }

}

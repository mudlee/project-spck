package spck.sandbox;

import spck.core.Application;
import spck.core.props.Preferences;

public class SandboxApplication {
  public static void main(String[] args) {
    final var prefs = new Preferences();

    new Application(new SandboxGame(), prefs).start();
  }
}

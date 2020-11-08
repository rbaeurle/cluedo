package de.rolf.games.cluedo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CluedoApplication implements CommandLineRunner {


  @Autowired
  CluedoGUI gui;

  public static void main(String[] args) {

    SpringApplication app = new SpringApplication(CluedoApplication.class);
    app.setBannerMode(Mode.OFF);
    app.run(args);
  }

  @Override
  public void run(String... args) {
    gui.run();
  }
}

package it.condarelli.javafx;

import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class StatusIndicator extends Button {

  public class Status {
    String                    name;
    EventHandler<ActionEvent> handler;
    Status                    next;
    String                    text;
    Image                     image;

    public Status(String name, EventHandler<ActionEvent> handler, String text, Image image) {
      this.name = name;
      this.handler = handler;
      this.text = text;
      this.image = image;
      if (text == null && image == null)
        this.image = unknown;
      next = this;
      states.put(name, this);
    }

    public Status(String name, EventHandler<ActionEvent> handler, Status next, String text, Image image) {
      this(name, handler, text, image);
      this.next = next;
    }

    public void trigger() {
      if (handler != null)
        handler.handle(new ActionEvent());
    }
  }

  private Map<String, Status>    states    = new HashMap<String, Status>();
  private Status                 current;
  private static final ImageView imageView = new ImageView();
  private static final Image     unknown   = new Image(
      StatusIndicator.class.getResourceAsStream("dialog-question-2.png"));

  private boolean                selected;

  public StatusIndicator() {
    super(null, imageView);
    setOnAction(ae -> {
      if (null != current) {
        selected = false;
        current.trigger();
        if (!selected)
          select(current.next);
      }
    });
  }

  public Status select(String name) {
    return select(states.get(name));
  }

  public Status select(Status status) {
    if (current == null || !current.equals(status)) {
      if (status != null) {
        imageView.setImage(status.image);
        setDisabled(false);
      } else {
        imageView.setImage(unknown);
        setDisabled(true);
      }
      current = status;
    }
    selected = true;
    return current;
  }

  public Status add(String name, Image img, EventHandler<ActionEvent> ea) {
    return new Status(name, ea, null, img);
  }

  public Status add(String name, Image img, EventHandler<ActionEvent> ea, Status next) {
    return new Status(name, ea, next, null, img);
  }
}

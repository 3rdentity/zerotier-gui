package it.condarelli.zerotier.gui.pages;

import java.util.List;

import org.tbee.javafx.scene.layout.MigPane;

import com.github.edouardswiac.zerotier.ZTService;
import com.github.edouardswiac.zerotier.api.ZTCNetwork;
import com.github.edouardswiac.zerotier.api.ZTController;

import it.condarelli.javafx.StatusIndicator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class Controller extends FxDialog {
  protected Controller() {
    super("ZeroTier Controller Status");
  }

  @FXML private MigPane          mpController;
  @FXML private Text             txtInstanceId;
  @FXML private ImageView        ivOK;
  @FXML private ComboBox<String> cbNetworks;
  @FXML private StatusIndicator  status;

  private ZTService              zts;
  private ZTCNetwork             ztcn;

  @Override
  protected void initialize(FxAdapter parent) {
    zts = FxAdapter.service(ZTService.class);

    if (zts != null) {
      ZTController ztc = zts.controller();
      if (ztc != null) {
        txtInstanceId.setText(ztc.getInstanceId());
        mpController.setVisible(true);
      } else
        mpController.setVisible(false);
    }
    status.add("refresh", new Image(getClass().getResourceAsStream("icons/16x16/view-refresh-3.png")), ae -> {
      String s = cbNetworks.getSelectionModel().getSelectedItem();
      List<String> nets = zts.getCNetworks();
      cbNetworks.setItems(FXCollections.observableArrayList(nets));
      if (!nets.contains(s))
        s = null;
      cbNetworks.getSelectionModel().select(s);
    }).trigger();
    status.add("inspect", new Image(getClass().getResourceAsStream("icons/16x16/archive-extract-2.png")), ae -> {
      String s = cbNetworks.getSelectionModel().getSelectedItem();
      if (s != null) {
        FxDialog.create(this, Network.class);
      }
      status.select("refresh");
    });
    status.select("refresh");
  }

  @FXML
  public void onSelectNet() {
    String s = cbNetworks.getSelectionModel().getSelectedItem();
    ztcn = zts.getCNetwork(s);
    status.select("inspect");
  }

  @Override
  public <T extends Object> T adapt(String what, Class<T> clazz) {
    switch (what) {
    case "selected_network":
      return clazz.cast(ztcn);
    case "add_network":
      return clazz.cast(new Callback<String, Void>() {
        @Override
        public Void call(String param) {
          cbNetworks.getItems().add(param);
          cbNetworks.setValue(param);
          status.select("refresh").trigger();
          return null;
        }
      });
    case "del_network":
      return clazz.cast(new Callback<String, Void>() {
        @Override
        public Void call(String param) {
          String v = cbNetworks.getValue();
          if (v != null && v.equals(param))
            cbNetworks.setValue(null);
          cbNetworks.getItems().remove(param);
          status.select("refresh").trigger();
          return null;
        }
      });
    }
    return super.adapt(what, clazz);
  }

}

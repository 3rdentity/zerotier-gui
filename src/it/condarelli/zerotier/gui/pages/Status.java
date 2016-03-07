package it.condarelli.zerotier.gui.pages;

import java.util.List;

import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.tbee.javafx.scene.layout.MigPane;

import com.github.edouardswiac.zerotier.ZTService;
import com.github.edouardswiac.zerotier.api.ZTNet;
import com.github.edouardswiac.zerotier.api.ZTStatus;

import it.condarelli.javafx.FxAdapter;
import it.condarelli.javafx.FxDialog;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class Status extends FxDialog {
  public Status() {
    super("ZeroTier Client Status");
  }

  @FXML Text            txtAddress;
  @FXML Text            txtVersion;
  @FXML Text            txtWorldId;
  @FXML Text            txtOnline;
  @FXML ComboBox<ZTNet> cbNets;
  @FXML Button          btnRefresh;
  @FXML Button          btnDel;
  @FXML Button          btnAdd;
  @FXML MigPane         mpNet;
  @FXML Text            txtNName;
  @FXML Text            txtNId;
  @FXML Text            txtNStatus;
  @FXML Text            txtNMAC;

  private ZTService     zts;

  @Override
  protected void initialize(FxAdapter parent) {
    zts = FxAdapter.service(ZTService.class);
    onRefresh();
  };

  private void fetchStatus() {
    ZTStatus st = zts.status();
    txtAddress.setText(st.getAddress());
    txtVersion.setText(st.getVersion());
    txtWorldId.setText(String.valueOf(st.getWorldId()));
    txtOnline.setText(st.isOnline() ? "ON line" : "OFF line");
  }

  private void fetchNets() {
    List<ZTNet> nets = zts.getNets();
    Callback<ListView<ZTNet>, ListCell<ZTNet>> cf = new Callback<ListView<ZTNet>, ListCell<ZTNet>>() {
      @Override
      public ListCell<ZTNet> call(ListView<ZTNet> list) {
        return new ListCell<ZTNet>() {
          @Override
          protected void updateItem(ZTNet net, boolean empty) {
            super.updateItem(net, empty);
            if (net == null | empty) {
              setText(null);
            } else {
              StringBuilder sb = new StringBuilder();
              sb.append(net.getNwid());
              sb.append(" - ");
              sb.append(net.getName());
              setText(sb.toString());
            }
          }
        };
      }
    };
    cbNets.setItems(FXCollections.observableArrayList(nets));
    cbNets.setButtonCell(cf.call(null));
    cbNets.setCellFactory(cf);
    onSelect();
  }

  @FXML
  public void onSelect() {
    ZTNet net = cbNets.valueProperty().get();
    if (net != null) {
      txtNName.setText(net.getName());
      txtNId.setText(net.getNwid());
      txtNStatus.setText(net.getStatus());
      txtNMAC.setText(net.getMac());
      mpNet.setVisible(true);
      btnDel.setDisable(false);
    } else {
      mpNet.setVisible(false);
      btnDel.setDisable(true);
    }
  }

  @FXML
  public void onRefresh() {
    if (zts != null) {
      fetchStatus();
      fetchNets();
    }
  }

  @FXML
  public void onNew() {
    cbNets.setValue(null);
    cbNets.setPromptText("Enter net address (16 hex digits)");
    cbNets.setEditable(true);
    EventHandler<ActionEvent> oa = cbNets.getOnAction();

    cbNets.setOnAction(null);
    TextField e = cbNets.getEditor();
    ValidationSupport vs = new ValidationSupport();
    vs.registerValidator(e, true,
        Validator.createRegexValidator("Net address must be 16 hex chars", "\\p{XDigit}{16}", Severity.ERROR));
    vs.initInitialDecoration();
    cbNets.addEventFilter(KeyEvent.KEY_PRESSED, (ev) -> {
      switch (ev.getCode()) {
      case ENTER:
        if (!vs.isInvalid()) {
          String a = e.getText();
          ZTNet n = null;
          for (ZTNet m : cbNets.getItems()) {
            if (m.getNwid().equals(a)) {
              n = m;
              break;
            }
          }
          if (n == null) {
            n = zts.joinNet(a);
            if (n != null)
              cbNets.getItems().add(n);
            else {
              (new AudioClip(getClass().getResource("icons/sounds/alarm.wav").toExternalForm())).play();
              Alert x = new Alert(AlertType.ERROR);
              x.setContentText("Unable to join network.");
              x.setHeaderText("ERROR:");
              x.showAndWait();
            }
          }
          cbNets.setEditable(false);
          cbNets.setOnAction(oa);
          cbNets.setValue(n);
        } else {
          (new AudioClip(getClass().getResource("icons/sounds/alert.wav").toExternalForm())).play();
        }
        // ev.consume();
        break;
      case DELETE:
        break;
      default:

      }
    });
  }

  @FXML
  public void onDel() {

  }
}

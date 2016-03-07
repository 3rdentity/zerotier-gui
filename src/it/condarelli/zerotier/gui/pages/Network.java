package it.condarelli.zerotier.gui.pages;

import java.util.Map;

import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.tbee.javafx.scene.layout.MigPane;

import com.github.edouardswiac.zerotier.ZTService;
import com.github.edouardswiac.zerotier.api.ZTCMember;
import com.github.edouardswiac.zerotier.api.ZTCNetwork;
import com.github.edouardswiac.zerotier.api.ZTStatus;

import it.condarelli.javafx.StatusIndicator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.util.Callback;

public class Network extends FxDialog {
  protected Network() {
    super("ZeroTier Network Member");
  }

  @FXML private MigPane          mpNetwork;
  @FXML private Text             txtId;
  @FXML private StatusIndicator  status;
  @FXML private TextField        tfName;
  @FXML private ToggleButton     tbPrivate;
  @FXML private ImageView        ivPrivate;
  @FXML private ComboBox<String> cbAssignModeV4;
  @FXML private ToggleButton     tbBroadcast;
  @FXML private ImageView        ivBroadcast;
  @FXML private ComboBox<String> cbAssignModeV6;
  @FXML private ToggleButton     tbBridging;
  @FXML private ImageView        ivBridging;
  @FXML private Button           btnDel;
  @FXML private Button           btnNew;
  @FXML private Button           btnSave;
  @FXML private ComboBox<String> cbMembers;
  @FXML private Button btnShow;
  
  private ZTService              zts;
  private ZTCNetwork             ztcn;
  private FxAdapter parent; 

  private static final Image     priv = new Image(Network.class.getResourceAsStream("icons/16x16/object-locked.png"));
  private static final Image     publ = new Image(Network.class.getResourceAsStream("icons/16x16/object-unlocked.png"));
  private static final Image     brod = new Image(Network.class.getResourceAsStream("icons/16x16/irkickflash.png"));
  private static final Image     pt2p = new Image(Network.class.getResourceAsStream("icons/16x16/irkickoff.png"));
  private static final Image     brid = new Image(Network.class.getResourceAsStream("icons/16x16/link.png"));
  private static final Image     locl = new Image(Network.class.getResourceAsStream("icons/16x16/link-break.png"));

  @Override
  protected void initialize(FxAdapter parent) {
    this.parent = parent;
    
    status.select(status.add("updating", new Image(getClass().getResourceAsStream("icons/16x16/run-build-2.png")), null));

    zts = FxAdapter.service(ZTService.class);
    ztcn = parent.adapt("selected_network", ZTCNetwork.class);

    refresh();

    status.select(status.add("ready", new Image(getClass().getResourceAsStream("icons/16x16/run-build-2.png")), null));
  }

  protected void refresh() {
    txtId.setText(ztcn.getNwid());
    tfName.setText(ztcn.getName());
    tbPrivate.setSelected(ztcn.isPrivate());
    ivPrivate.setImage(ztcn.isPrivate() ? priv : publ);
    cbAssignModeV4.setValue(ztcn.getV4AssignMode());
    tbBroadcast.setSelected(ztcn.isEnableBroadcast());
    ivBroadcast.setImage(ztcn.isEnableBroadcast() ? brod : pt2p);
    cbAssignModeV6.setValue(ztcn.getV6AssignMode());
    tbBridging.setSelected(ztcn.isAllowPassiveBridging());
    ivBridging.setImage(ztcn.isAllowPassiveBridging() ? brid : locl);
    Map<String, Integer> ml = zts.getCMembers(ztcn.getNwid());
    cbMembers.setItems(FXCollections.observableArrayList(ml.keySet()));
    onSelectMember();
  }

  protected boolean isChanged() {
    // txtId non editable
    if (!tfName.getText().equals(ztcn.getName()))
      return false;
    if (!(tbPrivate.isSelected() == ztcn.isPrivate()))
      return false;
    if (!cbAssignModeV4.getValue().equals(ztcn.getV4AssignMode()))
      return false;
    if (!(tbBroadcast.isSelected() == ztcn.isEnableBroadcast()))
      return false;
    if (!cbAssignModeV6.getValue().equals(ztcn.getV6AssignMode()))
      return false;
    if (!(tbBridging.isSelected() == ztcn.isAllowPassiveBridging()))
      return false;
    return true;
  }

  private ZTCNetwork workCopy;

  protected ZTCNetwork getCopy() {
    if (workCopy == null)
      workCopy = ztcn.clone();
    return workCopy;
  }

  private Alert createAlert(AlertType type) {
    Alert dlg = new Alert(type);
    dlg.initModality(Modality.APPLICATION_MODAL);
    // dlg.initOwner(owner);
    return dlg;
  }

  @FXML
  void onDel(ActionEvent event) {
    if (ztcn == null) {
      (new Alert(AlertType.ERROR, "Current ZTCNetwork is null; nothing to delete!")).showAndWait();
    } else {
      Alert dlg = createAlert(AlertType.CONFIRMATION);
      dlg.setTitle("Delete Network");
      dlg.getDialogPane().setContentText(
          String.format("Network '%s' (%s) \nwill be DESTROYED, are you SURE?", ztcn.getName(), ztcn.getNwid()));
      dlg.getDialogPane().setHeaderText("Destroy current Network?");
      dlg.showAndWait().ifPresent(result -> {
        if (result == ButtonType.OK) {
          String nwid = ztcn.getNwid();
          zts.deleteCNetwork(nwid);
          @SuppressWarnings("unchecked")
          Callback<String, Void>cb = parent.adapt("del_network", Callback.class);
          if (cb != null)
            cb.call(ztcn.getNwid());
          ztcn = null;
        }
      });
    }

  }

  @FXML
  void onNew(ActionEvent event) {
    TextInputDialog dlg = new TextInputDialog("______");
    ValidationSupport vs = new ValidationSupport();
    vs.registerValidator(dlg.getEditor(), true, Validator
        .createRegexValidator("Id must be 6 hex chars or 6 underscores", "______|\\p{XDigit}{6}", Severity.ERROR));
    dlg.setTitle("New Network");
    dlg.getDialogPane().setContentText("Enter Network ID \n(or leave 6 underscores to \nhave it random generated)");
    dlg.getDialogPane().setHeaderText("Create new Network?");
    dlg.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(vs.invalidProperty());
    dlg.showAndWait().ifPresent(result -> {
      String ni = FxAdapter.service(ZTStatus.class).getAddress() + result;
      ZTCNetwork n = getCopy(); 
      n.initNetworkId(ni);
      n = zts.updateCNetwork(getCopy());
      ni = n.getNwid();
      @SuppressWarnings("unchecked")
      Callback<String, Void>cb = parent.adapt("add_network", Callback.class);
      if (cb != null)
        cb.call(ni);
    });
  }

  @FXML
  void onSave(ActionEvent event) {
    if (null != workCopy && !workCopy.equals(ztcn)) {
      ztcn = zts.updateCNetwork(workCopy);
      refresh();
      workCopy = null;
    }
  }

  @FXML
  void onSelectAMV4(ActionEvent event) {
    ZTCNetwork n = getCopy();
    n.setV4AssignMode(cbAssignModeV4.getValue());
  }

  @FXML
  void onSelectAMV6(ActionEvent event) {
    ZTCNetwork n = getCopy();
    n.setV6AssignMode(cbAssignModeV6.getValue());
  }

  @FXML
  void onToggleBridging(ActionEvent event) {
    ZTCNetwork n = getCopy();
    n.setAllowPassiveBridging(tbBridging.isSelected());
  }

  @FXML
  void onToggleBroadcast(ActionEvent event) {
    ZTCNetwork n = getCopy();
    n.setEnableBroadcast(tbBroadcast.isSelected());
  }

  @FXML
  void onTogglePrivate(ActionEvent event) {
    ZTCNetwork n = getCopy();
    n.setPrivate(tbPrivate.isSelected());
  }

  @FXML
  void onSelectMember() {
    btnShow.setDisable(cbMembers.getValue() == null);
  }
  
  @FXML
  void onShowMember() {
    String m = cbMembers.getValue();
    if (m != null && !m.trim().isEmpty()) {
      FxDialog.create(this,  Member.class);
    }
  }


  protected void handleButtons() {
    boolean changed = isChanged();
    btnDel.setDisable(changed);
    btnNew.setDisable(false);
    btnSave.setDisable(!changed);
  }
  
  @Override
  public <T extends Object> T adapt(String what, Class<T> clazz) {
    switch (what) {
    case "selected_member":
      String s = cbMembers.getValue();
      if (s == null || s.trim().isEmpty())
        return null;
      ZTCMember m = zts.getCMember(ztcn.getNwid(), cbMembers.getValue());
      return clazz.cast(m);
    case "network":
      return clazz.cast(ztcn);
    }
    return super.adapt(what, clazz);
  }
}

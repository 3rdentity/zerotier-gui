package it.condarelli.zerotier.gui.pages;

import org.tbee.javafx.scene.layout.MigPane;

import com.github.edouardswiac.zerotier.ZTService;
import com.github.edouardswiac.zerotier.api.ZTCMember;
import com.github.edouardswiac.zerotier.api.ZTCNetwork;

import it.condarelli.javafx.FxAdapter;
import it.condarelli.javafx.FxDialog;
import it.condarelli.javafx.StatusIndicator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class Member extends FxDialog {
  public Member() {
    super("ZeroTier Network Member");
  }

  @FXML private MigPane          mpMember;
  @FXML private Text             txtNetwork;
  @FXML private StatusIndicator  status;
  @FXML private Text             txtMemberAddr;
  @FXML private Text             txtMemberId;
  @FXML private Button           btnMSave;
  @FXML private ImageView        ivSave;
  @FXML private CheckBox         cbAuthorized;
  @FXML private CheckBox         cbBridging;
  @FXML private ListView<String> lvIPs;
  @FXML private ListView<String> lvLog;

  private ZTService              zts;
  private ZTCNetwork             ztcn;
  private ZTCMember              ztcm;

  private ZTCMember              workCopy;

  @Override
  protected void initialize(FxAdapter parent) {
    status
        .select(status.add("updating", new Image(getClass().getResourceAsStream("icons/16x16/run-build-2.png")), null));

    zts = FxAdapter.service(ZTService.class);
    ztcm = parent.adapt("selected_member", ZTCMember.class);
    ztcn = parent.adapt("network", ZTCNetwork.class);

    txtNetwork.setText(ztcn.getName() + " - " + ztcn.getNwid());
    refresh();
    checkMSave();
    status
        .select(status.add("ready", new Image(getClass().getResourceAsStream("icons/16x16/view-refresh-3.png")), ae -> {
          ztcm = zts.getCMember(ztcm.getNwid(), ztcm.getAddress());
          refresh();
          checkMSave();
        }));
  }

  private String wrap(String str, int siz) {
    String[] sa = str.split(String.format("(?<=(?:[:]|\\G.{%d}))", siz));
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (String s : sa) {
      if (first)
        first = false;
      else
        sb.append('\n');
      sb.append(s);
    }
    return sb.toString();
  }

  protected void refresh() {
    txtMemberAddr.setText(ztcm.getAddress());
    txtMemberId.setText(wrap(ztcm.getIdentity(), 32));
    cbAuthorized.setSelected(ztcm.isAuthorized());
    cbBridging.setSelected(ztcm.isActiveBridge());
    lvIPs.setItems(FXCollections.observableArrayList(ztcm.getIpAssignments()));
    // TODO: all other fields
  }

  private ZTCMember getCopy() {
    if (null == ztcm)
      workCopy = null;
    else {
      if (null != workCopy)
        if (workCopy.getNwid() != ztcm.getNwid() || workCopy.getAddress() != ztcm.getAddress())
          workCopy = null;
      if (null == workCopy) {
        workCopy = ztcm.clone();
      }
    }
    return workCopy;
  }

  @FXML
  public void onMSave() {
    if (null != workCopy && !ztcm.equals(workCopy)) {
      zts.updateCMember(workCopy);
      workCopy = null;
    } else {
      ztcm = zts.getCMember(ztcm.getNwid(), ztcm.getIdentity());
      refresh();
    }
    checkMSave();
  }

  @FXML
  public void onMAuth() {
    ZTCMember m = getCopy();
    if (null != m)
      m.setAuthorized(cbAuthorized.isSelected());
    checkMSave();
  }

  @FXML
  public void onMBrid() {
    ZTCMember m = getCopy();
    if (null != m)
      m.setActiveBridge(cbBridging.isSelected());
    checkMSave();
  }

  private void checkMSave() {
    if (null == ztcm) {
      ivSave.setImage(new Image(getClass().getResourceAsStream("icons/16x16/dialog-block.png")));
      btnMSave.setDisable(true);
    } else {
      if (ztcm.equals(workCopy)) {
        ivSave.setImage(new Image(getClass().getResourceAsStream("icons/16x16/view-refresh-3.png")));
      } else {
        ivSave.setImage(new Image(getClass().getResourceAsStream("icons/16x16/document-save-6.png")));
      }
      btnMSave.setDisable(false);
    }
  }
}

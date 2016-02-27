package it.condarelli.zerotier.gui.pages;

import java.util.List;

import org.tbee.javafx.scene.layout.MigPane;

import com.github.edouardswiac.zerotier.ZTService;
import com.github.edouardswiac.zerotier.api.ZTCNetwork;
import com.github.edouardswiac.zerotier.api.ZTController;

import it.condarelli.zerotier.gui.Controller;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class Dispatcher extends Controller {
	@FXML private MigPane						mpDispatcher;
	@FXML private Text							txtInstanceId;
	@FXML private ImageView					ivOK;
	@FXML private ComboBox<String>	cbNetworks;
	@FXML private MigPane						mpNetwork;
	@FXML private TextField					tfName;
	@FXML private ToggleButton			btnPrivate;
	@FXML private ImageView					ivPrivate;
	@FXML private ComboBox<String>	cbAssignModeV4;
	@FXML private ToggleButton			tbBroadcast;
	@FXML private ImageView					ivBroadcast;
	@FXML private ComboBox<String>	cbAssignModeV6;
	@FXML private ToggleButton			tbBridging;
	@FXML private ImageView					ivBridging;

	private ZTService								zts;
	private ZTCNetwork ztcn;

	@Override
	protected void setNode(Node n) {
		super.setNode(n);
		zts = Login.getService();
		if (zts != null) {
			ZTController ztc = zts.controller();
			if (ztc != null) {
				txtInstanceId.setText(ztc.getInstanceId());
				mpNetwork.setVisible(false);
				onRefresh();
				mpDispatcher.setVisible(true);
			} else
				mpDispatcher.setVisible(false);
		}
	}

	@FXML
	void onRefresh() {
		String s = cbNetworks.getSelectionModel().getSelectedItem();
		List<String> nets = zts.getCNetworks();
		cbNetworks.setItems(FXCollections.observableArrayList(nets));
		if (nets.contains(s)) {
			cbNetworks.getSelectionModel().select(s);
		} else {
			cbNetworks.getSelectionModel().select(null);
			mpNetwork.setVisible(false);
		}
		ivOK.setImage(new Image(getClass().getResourceAsStream("icons/16x16/dialog-ok-apply-4.png")));
	}

	@FXML
	void onSelectNet() {
//		final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
//		executor.schedule(() -> {
			String s = cbNetworks.getSelectionModel().getSelectedItem().trim();
				if (s != null && !s.isEmpty()) {
					ztcn = zts.getCNetwork(s);
					
					tfName.setText(ztcn.getName());
					setPrivate(ztcn.isPrivate());
					cbAssignModeV4.getSelectionModel().select(ztcn.getV4AssignMode());
					setBroadcast(ztcn.isEnableBroadcast());
					cbAssignModeV6.getSelectionModel().select(ztcn.getV6AssignMode());
					setBridging(ztcn.isAllowPassiveBridging());
					
					mpNetwork.setVisible(true);
				} else
					mpNetwork.setVisible(false);
//		}, 2, TimeUnit.SECONDS);
	}

	private void setPrivate(boolean priv) {
		btnPrivate.setSelected(priv);
		onTogglePrivate();
	}

	private void setBroadcast(boolean bcast) {
		tbBroadcast.setSelected(bcast);
		onToggleBroadcast();
	}

	private void setBridging(boolean bridge) {
		tbBridging.setSelected(bridge);
		onToggleBridging();
	}

	@FXML
	void onSelectAMV4() {

	}

	@FXML
	void onSelectAMV6() {

	}

	@FXML
	void onToggleBridging() {
		ivBridging.setImage(new Image(getClass().getResourceAsStream(tbBridging.isSelected()? "icons/16x16/link.png": "icons/16x16/link-break.png")));
	}

	@FXML
	void onToggleBroadcast() {
		ivBroadcast.setImage(new Image(getClass().getResourceAsStream(tbBroadcast.isSelected()? "icons/16x16/irkickflash.png": "icons/16x16/irkickoff.png")));
	}

	@FXML
	void onTogglePrivate() {
		ivPrivate.setImage(new Image(getClass().getResourceAsStream(btnPrivate.isSelected()? "icons/16x16/object-locked.png": "icons/16x16/object-unlocked.png")));
	}

}

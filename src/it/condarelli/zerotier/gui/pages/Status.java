package it.condarelli.zerotier.gui.pages;

import java.util.List;

import org.tbee.javafx.scene.layout.MigPane;

import com.github.edouardswiac.zerotier.ZTService;
import com.github.edouardswiac.zerotier.api.ZTNet;
import com.github.edouardswiac.zerotier.api.ZTStatus;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class Status extends FxController {
	@FXML Text						txtAddress;
	@FXML Text						txtVersion;
	@FXML Text						txtWorldId;
	@FXML Text						txtOnline;
	@FXML ComboBox<ZTNet>	cbNets;
	@FXML MigPane					mpNet;
	@FXML Text						txtNName;
	@FXML Text						txtNId;
	@FXML Text						txtNStatus;
	@FXML Text						txtNMAC;

	private ZTService			zts;
	private List<ZTNet>		nets;

	@Override
	protected void setNode(Node n) {
		super.setNode(n);
		zts = Login.getService();
		if (zts != null) {
			fetchStatus();
			fetchNets();
		}
	};

	private void fetchStatus() {
		ZTStatus st = zts.status();
		txtAddress.setText(st.getAddress());
		txtVersion.setText(st.getVersion());
		txtWorldId.setText(String.valueOf(st.getWorldId()));
		txtOnline.setText(st.isOnline() ? "ON line" : "OFF line");
	}

	private void fetchNets() {
		nets = zts.getNets();
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
		} else
			mpNet.setVisible(false);
	}

	@FXML
	public void onRefresh() {

	}
}

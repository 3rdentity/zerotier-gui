package it.condarelli.zerotier.gui.pages;

import com.github.edouardswiac.zerotier.ZTService;
import com.github.edouardswiac.zerotier.ZTServiceImpl;
import com.github.edouardswiac.zerotier.api.ZTStatus;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class Login extends FxController {
	@FXML TextField						tfURL;
	@FXML TextField						tfKey;
	@FXML Text								txtError;

	private static ZTService	zts;
	private static String			ztca;

	@Override
	protected void setNode(Node n) {
		super.setNode(n);
		tfURL.setText("https://zerotier.condarelli.it:47443/api");
		tfKey.setText("0nktesq0w2gk9oyaxmr11ni4");
	}
	
	@FXML
	public void onLogin() {
		root.setVisible(false);
		zts = new ZTServiceImpl(tfURL.getText(), tfKey.getText());
		ZTStatus s = zts.status();
		if (s == null || !s.isOnline()) {
			zts = null;
			txtError.setText("ERROR: Unable to contact server.");
			txtError.setVisible(true);
			root.setVisible(true);
			fire("LOG.FAIL");
		} else {
			ztca = s.getAddress();
			fire("LOG.OK");
		}
	}

	static ZTService getService() {
		return zts;
	}

	static String getControllerAddress() {
		return ztca;
	}
}

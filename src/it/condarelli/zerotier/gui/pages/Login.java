package it.condarelli.zerotier.gui.pages;

import com.github.edouardswiac.zerotier.ZTService;
import com.github.edouardswiac.zerotier.ZTServiceImpl;
import com.github.edouardswiac.zerotier.api.ZTStatus;

import it.condarelli.zerotier.gui.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class Login extends Controller {
	@FXML TextField	tfURL;
	@FXML TextField	tfKey;
	@FXML Text			txtError;

	static ZTService				zts;

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
			fire("LOG.OK");
		}
	}
	
	static ZTService getService() {
		return zts;
	}
}

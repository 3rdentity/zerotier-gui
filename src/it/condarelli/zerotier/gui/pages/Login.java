package it.condarelli.zerotier.gui.pages;

import com.github.edouardswiac.zerotier.ZTService;
import com.github.edouardswiac.zerotier.ZTServiceImpl;
import com.github.edouardswiac.zerotier.api.ZTStatus;

import it.condarelli.javafx.FxAdapter;
import it.condarelli.javafx.FxDialog;
import it.condarelli.javafx.memento.Memento;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class Login extends FxDialog {
	
	public Login() {
		super("Login");
	}

	@FXML TextField						tfURL;
	@FXML TextField						tfKey;
	@FXML Text								txtError;

//	@Override
//	protected void setNode(Node n) {
//		super.setNode(n);
//		tfURL.setText("https://zerotier.condarelli.it:47443/api");
//		tfKey.setText("0nktesq0w2gk9oyaxmr11ni4");
//	}

	@FXML
	public void onLogin() {
		root.setVisible(false);
		ZTService zts = new ZTServiceImpl(tfURL.getText(), tfKey.getText());
		FxAdapter.provide(zts, ZTService.class);
		ZTStatus s = zts.status();
		if (s == null || !s.isOnline()) {
			zts = null;
			txtError.setText("ERROR: Unable to contact server.");
			txtError.setVisible(true);
			root.setVisible(true);
			fire("LOG.FAIL");
		} else {
			FxAdapter.provide(s);
			fire("LOG.OK");
		}
	}

	@Override
	public void doRestore(Memento m) {
		tfURL.setText(m.getChild("URL").getText());
		tfKey.setText(m.getChild("Key").getText());
	}

	@Override
	public void doSave(Memento m) {
		m.getChild("URL").setText(tfURL.getText());
		m.getChild("Key").setText(tfKey.getText());
	}
}

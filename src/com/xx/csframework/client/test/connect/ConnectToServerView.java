package com.xx.csframework.client.test.connect;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.xx.csframework.client.test.login.LoginView;
import com.xx.csframework.core.Client;
import com.xx.csframework.core.ClientActionAdapter;
import com.xx.csframework.core.INetListener;
import com.mec.util.IMecView;
import com.mec.util.ViewTool;

public class ConnectToServerView implements IMecView, INetListener {
	private Client client;
	private JFrame jfrmConnectView;
	private JLabel jlblMessage;
	private int count;

	public ConnectToServerView() {
		this.count = 1;
		this.client = new Client();
		try {
			this.client.initClient("/netcfg.properties");
			this.client.addListener(this);
			this.client.setClientAction(new ConnectAction());
			
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void initView() {
		jfrmConnectView = new JFrame("微易码视频系统-连接服务器");
		jfrmConnectView.setSize(400, 200);
		jfrmConnectView.setLocationRelativeTo(null);
		jfrmConnectView.setResizable(false);
		jfrmConnectView.setLayout(new BorderLayout());
		jfrmConnectView.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		jlblMessage = new JLabel("", JLabel.CENTER);
		jlblMessage.setFont(topicFont);
		jlblMessage.setForeground(topicColor);
		jfrmConnectView.add(jlblMessage, BorderLayout.CENTER);
	}

	@Override
	public void reinitView() {
		jlblMessage.setText("第" + count++ + "次连接服务器……");
	}

	@Override
	public void addEventListener() {
	}

	@Override
	public void showView() {
		jfrmConnectView.setVisible(true);
		while (client.connectToServer() == false) {
			int choice = ViewTool.userChoice(jfrmConnectView, "是否继续尝试连接服务器?");
			if (choice == JOptionPane.NO_OPTION) {
				break;
			}
			jlblMessage.setText("第" + count++ + "次连接服务器……");
		}
		closeView();
	}

	@Override
	public void closeView() {
		jfrmConnectView.dispose();
	}

	@Override
	public void dealNetMessage(String message) {
		ViewTool.showMessage(jfrmConnectView, message);
	}

	class ConnectAction extends ClientActionAdapter {

		@Override
		public void connectSuccess() {
			client.removeListener(ConnectToServerView.this);
			
			LoginView loginView = new LoginView(client);
			loginView.showView();
			closeView();
		}

		@Override
		public void serverOutOfRoom() {
			ViewTool.showMessage(jfrmConnectView, "服务器已满，请稍后尝试！");
			closeView();
		}

		@Override
		public void connectTooFast() {
			ViewTool.showMessage(jfrmConnectView, "连接过于频繁，请稍后尝试！");
			closeView();
		}
	}
	
}

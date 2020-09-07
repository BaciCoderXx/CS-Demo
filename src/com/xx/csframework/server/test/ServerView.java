package com.xx.csframework.server.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.xx.csframework.actioner.ActionFactory;
import com.xx.csframework.core.INetListener;
import com.xx.csframework.core.Server;
import com.mec.util.IMecView;
import com.mec.util.PropertiesParser;
import com.mec.util.ViewTool;

public class ServerView implements INetListener, IMecView {
	private Server server;
	
	private JFrame jfrmServerConsole;
	private JTextArea jtatSystemMessage;
	private JTextField jtxtCommand;
	
	public ServerView() {
		ActionFactory.scanActioner("com.xx.csframework.server.test.action");
		server = new Server();
		server.addListener(this);
		server.initServer("/netcfg.properties");
		
		init();
	}
	
	@Override
	public void dealNetMessage(String message) {
		jtatSystemMessage.append(message + '\n');
		jtatSystemMessage.setCaretPosition(
				jtatSystemMessage.getText().length());
	}
	
	private void dealCommand(String command) {
		if (command.equalsIgnoreCase("exit")
				|| command.equalsIgnoreCase("x")) {
			closeView();
		} else if (command.equalsIgnoreCase("startup")
				|| command.equalsIgnoreCase("st")) {
			try {
				server.startup();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (command.equalsIgnoreCase("shutdown")
				|| command.equalsIgnoreCase("sd")) {
			server.shutdown();
		}
	}

	@Override
	public void initView() {
		jfrmServerConsole = new JFrame("视频系统-服务器控制台");
		jfrmServerConsole.setMinimumSize(new Dimension(800, 600));
		jfrmServerConsole.setLocationRelativeTo(null);
//		jfrmServerConsole.setExtendedState(JFrame.MAXIMIZED_BOTH); TODO
		jfrmServerConsole.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		jfrmServerConsole.setLayout(new BorderLayout());
		
		JLabel jlblTopic = new JLabel("服务器控制台", JLabel.CENTER);
		jlblTopic.setFont(topicFont);
		jlblTopic.setForeground(topicColor);
		jfrmServerConsole.add(jlblTopic, BorderLayout.NORTH);
		
		jtatSystemMessage = new JTextArea();
		jtatSystemMessage.setFont(normalFont);
		JScrollPane jscpSystemMessage = new JScrollPane(jtatSystemMessage);
		TitledBorder ttbdSystemMessage = new TitledBorder("系统消息");
		ttbdSystemMessage.setTitleColor(Color.red);
		ttbdSystemMessage.setTitleFont(normalFont);
		ttbdSystemMessage.setTitlePosition(TitledBorder.TOP);
		ttbdSystemMessage.setTitleJustification(TitledBorder.CENTER);
		jscpSystemMessage.setBorder(ttbdSystemMessage);
		jfrmServerConsole.add(jscpSystemMessage, BorderLayout.CENTER);
		
		JPanel jpnlCommand = new JPanel();
		jfrmServerConsole.add(jpnlCommand, BorderLayout.SOUTH);
		
		JLabel jlblCommand = new JLabel("命令：");
		jlblCommand.setFont(normalFont);
		jpnlCommand.add(jlblCommand);
		
		jtxtCommand = new JTextField(40);
		jtxtCommand.setFont(normalFont);
		jpnlCommand.add(jtxtCommand);
	}

	@Override
	public void reinitView() {
		jtatSystemMessage.setEditable(false);
		jtatSystemMessage.setFocusable(false);
	}

	@Override
	public void addEventListener() {
		jfrmServerConsole.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeView();
			}
		});
		
		jtxtCommand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String command = jtxtCommand.getText().trim();
				jtxtCommand.setText("");
				dealCommand(command);
			}
		});
	}

	@Override
	public void showView() {
		jfrmServerConsole.setVisible(true);
		String mess = PropertiesParser.value("auto_startup");
		boolean autoStartup = Boolean.valueOf(mess);
		if (autoStartup) {
			try {
				server.startup();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void closeView() {
		if (server.isServerStartup()) {
			ViewTool.showMessage(jfrmServerConsole, "服务器尚未宕机，请先宕机！");
			return;
		}
		jfrmServerConsole.dispose();
	}
	
}

package com.xx.csframework.client.test.login;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.xx.about_dialog.WaittingDialog;
import com.xx.csframework.actioner.ActionFactory;
import com.xx.csframework.annotation.Actioner;
import com.xx.csframework.annotation.MecAction;
import com.xx.csframework.core.Client;
import com.xx.csframework.core.ClientActionAdapter;
import com.xx.csframework.core.INetListener;
import com.mec.util.ArgumentMaker;
import com.mec.util.IMecView;
import com.mec.util.ViewTool;
import com.xx.csframework.server.test.model.UserModel;

@MecAction
public class LoginView implements IMecView, INetListener {
	private Client client;
	
	private JFrame jfrmLoginView;
	private JTextField jtxtUserId;
	private JTextField jtxtvcode;
	private JPasswordField jpswPassword;
	
	private JLabel jlblUserRegist;
	private JButton jbtnLogin;
	private JButton[] jbtnSBs;
	
	private WaittingDialog waittingDialog;
	
	private ValidCode vcode;
	
	private static final Font topicFontX = new Font("楷体", Font.BOLD, 40);
	
	
	public LoginView(Client client) {
		this.client = client;
		this.client.addListener(this);
		this.client.setClientAction(new LoginAction());
		this.vcode = new ValidCode();
		try {
			ActionFactory.addAction(this);
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void dealNetMessage(String message) {
		System.out.println(message + "");
	}

	@Override
	public void initView() {
		jfrmLoginView = new JFrame("抖音");
		jfrmLoginView.setIconImage(Toolkit.getDefaultToolkit().getImage("src/DY.jpg"));
		jfrmLoginView.setSize(245, 400);
		jfrmLoginView.setLayout(new BorderLayout());
		jfrmLoginView.setResizable(false);
		jfrmLoginView.setLocationRelativeTo(null);
		jfrmLoginView.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		// 标题居中显示
		JLabel jlblTopic = new JLabel("登录抖音", JLabel.CENTER);
		jlblTopic.setFont(topicFontX);
		jlblTopic.setForeground(Color.blue);
		jfrmLoginView.add(jlblTopic, BorderLayout.NORTH);
		// 账号和密码包裹在一个JPanel中，
		// 这个画板使用FlowLayout布局
		JPanel jpnlBody = new JPanel();
		jfrmLoginView.add(jpnlBody);
		
		JLabel jlblUserId = new JLabel();
		jlblUserId.setIcon(new ImageIcon("src/user.png"));
		jpnlBody.add(jlblUserId);
		
		jtxtUserId = new JTextField(24);
		jtxtUserId.setFont(normalFont);
		jpnlBody.add(jtxtUserId);
		
		JLabel jlblPassword = new JLabel();
		jlblPassword.setIcon(new ImageIcon("src/lock.png"));
		jpnlBody.add(jlblPassword);
		
		jpswPassword = new JPasswordField(24);
		jpswPassword.setFont(normalFont);
		jpnlBody.add(jpswPassword);
		//插入验证码
		JLabel jlblvcode = new JLabel();
		jlblvcode.setText("验证码: ");
		jlblvcode.setBackground(Color.black);
		jlblvcode.setFont(normalFont);
		jpnlBody.add(jlblvcode);
		
		jtxtvcode = new JTextField(10);
		jtxtvcode.setFont(normalFont);
		jpnlBody.add(jtxtvcode);
		
		vcode.setSize(40, 13);
		jpnlBody.add(vcode, BorderLayout.SOUTH);
		
		JPanel jpnlSB = new JPanel(new GridLayout(4, 4));
		jbtnSBs = new JButton[16];
		for(int i = 0 ; i < 16 ; i++) {
			jbtnSBs[i] = new JButton("O");
			jbtnSBs[i].setSize(30, 30);
			jbtnSBs[i].setFont(new Font("黑体", Font.PLAIN, 20));
			jbtnSBs[i].setBackground(Color.WHITE);
			jbtnSBs[i].setBorderPainted(false);
			jpnlSB.add(jbtnSBs[i]);
		}
		
		jpnlBody.add(jpnlSB);
		
		// 界面最后有一个“注册用户”和“登录”按钮
		JPanel jpnlFooter = new JPanel(new FlowLayout());
		jfrmLoginView.add(jpnlFooter, BorderLayout.SOUTH);
		
		// 更改密码使用JLabel，这里更改了鼠标形式！
		jlblUserRegist = new JLabel("注册用户    ");
		jlblUserRegist.setFont(buttonFont);
		jlblUserRegist.setForeground(hrefColor);
		// 更改鼠标形式为“小手”
		jlblUserRegist.setCursor(
				new Cursor(Cursor.HAND_CURSOR));
		jpnlFooter.add(jlblUserRegist);
		
		jbtnLogin = new JButton(" 登录 ");
		jbtnLogin.setFont(buttonFont);
		jpnlFooter.add(jbtnLogin);
	}

	@Override
	public void reinitView() {
	}

	@Override
	public void addEventListener() {
		vcode.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				vcode.generateCode();
				vcode.repaint();				
			}
		});
		jfrmLoginView.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				client.offline();
				closeView();
			}
		});
		
		jtxtUserId.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jpswPassword.requestFocus();
			}
		});
		
		jtxtUserId.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				jtxtUserId.selectAll();
			}
		});
		
		jpswPassword.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbtnLogin.requestFocus();
			}
		});
		
		jpswPassword.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				jpswPassword.setText("");
			}
		});
		
		jbtnLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(jtxtvcode.getText().isEmpty()) {
					ViewTool.showMessage(jfrmLoginView, "请输入验证码");
					jpswPassword.setText("");
					return;
				}else if(!isValidCodeRight()) {
					ViewTool.showMessage(jfrmLoginView, "验证码错误！");
					jtxtvcode.setText("");
					return;
				}
				vcode.generateCode();
				vcode.repaint();
				client.sendRequest("userLogin", new ArgumentMaker()
						.addArg("id", jtxtUserId.getText().trim())
						.addArg("password", new String(jpswPassword.getPassword()))
						.toString());
				waittingDialog = new WaittingDialog(
						jfrmLoginView, true)
						.initDialog("正在努力登陆中");
				waittingDialog.showDialog();
			}
			
		});
		
		for(JButton jbtn : jbtnSBs) {
			jbtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(jbtn.getText().equals("O")) {
						jbtn.setText("X");
					}else jbtn.setText("O");
				}
			});
		}
	}
	
	@Actioner(action="userLogin")
	public void afterUserLogin(UserModel user) {
		waittingDialog.closeDialog();
		if(user.getId().equals("ERROR")) {
			ViewTool.showMessage(null, "账号或密码错误");
			jpswPassword.setText("");
			jtxtvcode.setText("");
			jtxtUserId.requestFocus();
		} else {
			ViewTool.showMessage(jfrmLoginView, "欢迎" + user.getId() 
			+ "<" + user.getNick() + ">登陆小夏的系统");
		}
	}

	private boolean isValidCodeRight() {
		if(jtxtvcode == null) {
			return false;
		}else if(vcode.getCode().equalsIgnoreCase(jtxtvcode.getText())) {
			return true;
		}
		return false;
	}
	
	private String getButtonCode(JButton[] jbtns) {
		StringBuffer rs = new StringBuffer();
		for(int i = 0; i < jbtns.length ; i++) {
			if(jbtns[i].getText().equals("X")) {
				char tmp = (char) (i + 'A');
				rs.append(String.valueOf(tmp));
			}
		}
		return rs.toString();
	}
	
	@Override
	public void showView() {
		jfrmLoginView.setVisible(true);
	}

	@Override
	public void closeView() {
		jfrmLoginView.dispose();
	}
	
	class LoginAction extends ClientActionAdapter {
		public LoginAction() {
		}

		@Override
		public void serverAbnormalDrop() {
			ViewTool.showMessage(jfrmLoginView, "服务器异常宕机，服务停止！");
			closeView();
		}

		@Override
		public boolean confirmOffline() {
			return true;
		}

	}

}

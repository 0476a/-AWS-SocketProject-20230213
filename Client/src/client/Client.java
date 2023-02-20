package client;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.gson.Gson;

import clientDto.CreateRoomReqDto;
import clientDto.JoinReqDto;
import clientDto.MessageReqDto;
import clientDto.RequestDto;
import lombok.Getter;

@Getter
public class Client extends JFrame {

	private static Socket socket;
	private Gson gson;
	private String username;

	private static Client instance;

	private JPanel mainPanel;
	private JTextField usernameField;
	private JTextField messageField;
	private JLabel inputMessage;
	private CardLayout mainCard;
	private JTextArea contentView;
	private JList<String> roomList;
	private DefaultListModel<String> roomListModel;
	private JScrollPane roomListPanel;
	private JScrollPane contentViewPanel;
	private JScrollPane messagePanel;
	private JLabel usernameLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client frame = Client.getInstance();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static Client getInstance() {
		if (instance == null) {
			instance = new Client();
		}
		return instance;
	}

	/**
	 * Create the frame.
	 */
	private Client() {
		gson = new Gson();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 480, 800);
		mainPanel = new JPanel();
		mainPanel.setBorder(null);

		setContentPane(mainPanel);
		mainCard = new CardLayout();
		mainPanel.setLayout(mainCard);

		JPanel loginPanel = new JPanel();
		loginPanel.setBackground(new Color(0, 255, 64));
		mainPanel.add(loginPanel, "loginPanel");
		loginPanel.setLayout(null);

		usernameField = new JTextField();
		usernameField.setBounds(100, 500, 250, 50);
		loginPanel.add(usernameField);
		usernameField.setColumns(10);

		JButton loginButton = new JButton("카카오로 시작하기");
		loginButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				try {

					if (!usernameField.getText().isBlank()) {
						socket = new Socket("127.0.0.1", 9090);
						System.out.println("연결");
						mainCard.show(mainPanel, "listPanel");

					} else {
						JOptionPane.showMessageDialog(null, "아이디를 입력하세요.", "접속실패", JOptionPane.ERROR_MESSAGE);
					}

					username = usernameField.getText();
					usernameLabel.setText(username);
					JoinReqDto joinReqDto = new JoinReqDto(username);
					String joinReqDtoJson = gson.toJson(joinReqDto);
					RequestDto requestDto = new RequestDto("join", joinReqDtoJson);
					String requestDtoJson = gson.toJson(requestDto);

					OutputStream outputStream = socket.getOutputStream();
					PrintWriter out = new PrintWriter(outputStream, true);
					out.println(requestDtoJson);

					ClientRecive clientRecive = new ClientRecive(socket);
					clientRecive.start();

				} catch (ConnectException e1) {
					JOptionPane.showMessageDialog(null, "서버에 연결할 수 없습니다.", "접속실패", JOptionPane.ERROR_MESSAGE);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		});
		loginButton.setBounds(100, 560, 250, 40);
		loginPanel.add(loginButton);

		JPanel roomPanel = new JPanel();
		mainPanel.add(roomPanel, "roomPanel");
		roomPanel.setLayout(null);

		JLabel roomName = new JLabel("New label");
		roomName.setBounds(92, 25, 133, 15);
		roomPanel.add(roomName);

		JLabel exitRoom = new JLabel("");
		exitRoom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mainCard.show(mainPanel, "listPanel");
			}
		});
		exitRoom.setIcon(new ImageIcon("C:\\Users\\ITPS\\Desktop\\아이콘\\free-icon-exit-to-app-button-612083.png"));
		exitRoom.setBounds(373, 10, 39, 45);
		roomPanel.add(exitRoom);

		inputMessage = new JLabel("");
		inputMessage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				sendMessage();
			}
		});
		inputMessage.setIcon(new ImageIcon("C:\\Users\\ITPS\\Desktop\\아이콘\\free-icon-right-arrow-4510674 (1).png"));
		inputMessage.setBounds(413, 705, 39, 46);
		roomPanel.add(inputMessage);

		contentViewPanel = new JScrollPane();
		contentViewPanel.setBounds(0, 88, 464, 607);
		roomPanel.add(contentViewPanel);

		contentView = new JTextArea();
		contentViewPanel.setViewportView(contentView);

		messagePanel = new JScrollPane();
		messagePanel.setBounds(10, 701, 391, 60);
		roomPanel.add(messagePanel);

		messageField = new JTextField();
		messagePanel.setViewportView(messageField);
		messageField.setColumns(10);

		JPanel listPanel = new JPanel();
		mainPanel.add(listPanel, "listPanel");
		listPanel.setLayout(null);

		JLabel createRoom = new JLabel("");
		createRoom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String input = JOptionPane.showInputDialog(null, "방생성");

				if (input != null) {
					mainCard.show(mainPanel, "roomPanel");

					try {
						CreateRoomReqDto createRoomReqDto = new CreateRoomReqDto(input);
						String createRoomReqDtoJson = gson.toJson(createRoomReqDto);
						RequestDto requestDto = new RequestDto("createRoom", createRoomReqDtoJson);
						String requestDtoJson = gson.toJson(requestDto);

						OutputStream outputStream = socket.getOutputStream();
						PrintWriter out = new PrintWriter(outputStream, true);
						out.println(requestDtoJson);
					} catch (IOException e1) {

						e1.printStackTrace();
					}

				}

			}
		});
		createRoom.setIcon(new ImageIcon("C:\\Users\\ITPS\\Desktop\\아이콘\\free-icon-plus-657023 (2).png"));
		createRoom.setBounds(29, 91, 45, 45);
		listPanel.add(createRoom);

		roomListPanel = new JScrollPane();
		roomListPanel.setBounds(106, 86, 358, 675);
		listPanel.add(roomListPanel);

		roomListModel = new DefaultListModel<>();
		roomList = new JList(roomListModel);
		roomListPanel.setViewportView(roomList);

		roomList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				roomList = (JList) e.getSource();
				if (e.getClickCount() == 2) {
					mainCard.show(mainPanel, "roomPanel");
				}

			}
		});
		usernameLabel = new JLabel("");
		usernameLabel.setBounds(12, 10, 79, 35);
		listPanel.add(usernameLabel);
	}

	private void sendRequest(String resourse, String body) {
		OutputStream outputStream;
		try {
			outputStream = socket.getOutputStream();
			PrintWriter out = new PrintWriter(outputStream, true);
			RequestDto requestDto = new RequestDto(resourse, body);
			out.println(gson.toJson(requestDto));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void sendMessage() {
		if (!messageField.getText().isBlank()) {

			MessageReqDto messageReqDto = new MessageReqDto(username, messageField.getText());

			sendRequest("sendMessage", gson.toJson(messageReqDto));
			messageField.setText("");
		}
	}

}

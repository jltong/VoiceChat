import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import javax.swing.*;

@SuppressWarnings("serial")
public class ChatConsole extends JFrame {
	public mainclass father = null;
	audio audio;
	
	private JTextArea jtfMessage;
	private JTextArea jtaSig;
	
	private JPanel pNorth;
	private JPanel pCenter;
	private JPanel p1;
	private JPanel p2;
	

	private JList list;
	private JScrollPane scrollPane;

	private DefaultListModel dftList;

	private ListCellRenderer renderer; 
	
	
	private JPopupMenu jpMenu;
	private JMenuItem jmiInfoEdit;
	
	
	private String[] iptable;
	private String iplocal;   // record the ip addr locally
	private friendInfo[] friend;
	private friendInfo[] friendNew;
	
	
	private linkState [] linkstate;
	//private StartChat [] chat = new StartChat[10] ;
	TreeMap<String,StartChat> chat= new TreeMap<String,StartChat>();
	private InfoEdit edit;
	
	public ChatConsole(mainclass fat){
		this.father = fat;
		audio=new audio(father);
	//	audio.capture("172.25.47.156");

		iptable = father.getip();//varied
		System.out.println(iptable.length);
		
		
		
		linkstate = new linkState[iptable.length];

		
		
		for(int j=0;j<iptable.length;j++){
			linkstate[j] = new linkState();
			
			linkstate[j].srcip = new String(iptable[0]);
			linkstate[j].destip = new String(iptable[j]);
			linkstate[j].isConnect = false;
			linkstate[j].isReady =false;	
		}

		System.out.println("ip :" + this.iplocal );
			
		this.iplocal = new String(iptable[0]);
		//set the default Information;
		friend = new friendInfo[iptable.length];
		friendNew = new friendInfo[iptable.length];
		for(int i=0;i<iptable.length;i++){
			//firstly,initialize  to the same;
			//and before next check , the friend[] never change 
			//while the friendNew[] may change a lot;
			
			//we use the friend[] to maintain the current info of online friend
			//while the friendNew[] to store the refresh record.
			
			friend[i]=new friendInfo();
			
			friend[i].setIp(iptable[i]);
			friend[i].setName("Owner of "+iptable[i]);
			friend[i].setProfile("Hello,everyone!I'm "+friend[i].getName());
			friend[i].setHeadPicture(new ImageIcon(getClass().getResource("image/"+i+".jpg")));
			
			friendNew[i]=new friendInfo();
			
			friendNew[i].setIp(iptable[i]);
			friendNew[i].setName("Owner of "+iptable[i]);
			friendNew[i].setProfile("Hello,everyone!I'm "+friendNew[i].getName());
			friendNew[i].setHeadPicture(new ImageIcon(getClass().getResource("image/"+i+".jpg")));
		}
	
		
		//before the build of the UI,obtain the personalInfo firstly.
		
		initWindow();// initiate the user interface
//		listenTo();// have been added into initWindow()
		
		new Thread(new MsgRecv()).start();//polling and check the request from outside
		
		
		
		Timer timer1 = new Timer() ;
		timer1.schedule( new fleshtimer() , 500 , 1000) ;
		
	}

	public int getfriend(String targetip){
		int index=-1;
		for (int i=0;i<friend.length;i++){
			if(targetip.equals(friend[i].Ip)){
				index=i;
				break;
			}
		}		
		return index;
	}
	
	public int getfriendNew(String targetip){
		int index=-1;
		for (int i=0;i<friendNew.length;i++){
			if(targetip.equals(friendNew[i].Ip)){
				index=i;
				break;
			}
		}		
		return index;
	}
	
	public void initWindow(){
		
				
		jtfMessage = new JTextArea();		
		jtaSig = new JTextArea();
		pNorth = new JPanel();	
		pNorth.setLayout(new BorderLayout());
		
		jpMenu = new JPopupMenu();
		jmiInfoEdit =  new JMenuItem("Edit Personal Information");
		jpMenu.add(jmiInfoEdit);

		ImageIcon localIcon = friend[0].getHeadPicture();
		
		//How to adjust size?
		localIcon.setImage(localIcon.getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT));
		
		
		
		JLabel jl = new JLabel(localIcon);
		
		
		pNorth.add(jl,BorderLayout.WEST);
		String myInfo = "";
		myInfo+="My IpAddr: \r\n"+friend[0].Ip+"\r\n";
		myInfo+="My Name: \r\n"+friend[0].Name;
		jtfMessage.setText(myInfo);
		
 		jtfMessage.setFont(new Font("Consolas",Font.BOLD,16));
 		jtfMessage.setForeground(Color.BLACK);
		jtfMessage.setEditable(false);
		
		jtfMessage.setLineWrap(true);
		
		pNorth.add(jtfMessage,BorderLayout.CENTER);
		
		add(pNorth, BorderLayout.NORTH);

		pCenter = new JPanel(new BorderLayout());
		
		String mySig = "";
		mySig+="My Signature: \r\n"+friend[0].getProfile();
		jtaSig.setText(mySig);
		jtaSig.setFont(new Font("Consolas",Font.BOLD,12));
		jtaSig.setEditable(false);
		jtaSig.setLineWrap(true);
		
		p1 = new JPanel(new GridLayout(1,1));
		p1.add(jtaSig);
		p1.setBorder(BorderFactory.createEtchedBorder());
		
		pCenter.add(p1,BorderLayout.NORTH);
		
		p2 = new JPanel(new BorderLayout());
		
		dftList=new DefaultListModel();
		//add item into JList
		list=new JList(dftList);
		
		pNorth.setComponentPopupMenu(jpMenu);
		
		
		renderer = new MyListCellRenderer();
		
		ImageIcon[] icons = new ImageIcon[iptable.length]; 
		
		for(int i=1;i<iptable.length;i++){
			icons[i] = friend[i].getHeadPicture();
			icons[i].setImage(icons[i].getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT));//
			
			dftList.addElement(new Object[]{icons[i],friend[i].Name});
		}
		
		list.setCellRenderer(renderer);
		
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		scrollPane = new JScrollPane(list);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		//list.setFixedCellWidth(200);
		p2.add(scrollPane,BorderLayout.CENTER);
		p2.setBorder(BorderFactory.createEtchedBorder());
		
		pCenter.add(p2,BorderLayout.CENTER);
		
		
		add(pCenter,BorderLayout.CENTER);

		
		setTitle("Distributed Talk System");
		setSize(300, 500);
		this.setLocation(900, 100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);	
		listenTo() ;
	}
	
	public void listenTo(){
		list.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				int mod = arg0.getModifiers();
				System.out.println("click");
				if((mod&InputEvent.BUTTON1_MASK)!=0){
					//confine to click left
					System.out.println(arg0.getClickCount()) ;
					if(arg0.getClickCount()==2)
					{
						
						System.out.println("fuck");
						//JOptionPane.showMessageDialog(list, "click 2");
						String iptarget="";
						
						int index;
						if(list.getSelectedValue()!=null){
							System.out.println("getSelectedValue success!!!");
							index = list.getSelectedIndex();
							if(index>-1){
								System.out.println("\n\n\ntartget-----------------!"+index);
								if (index>=0)
								{
									iptarget = iptable[index+1];
								}
							}
						}
						if(iptarget.equals(""))
						{
							System.out.println("getSelectedValue failed");
							return;
						}
						//send!
						
//						if(!isRecv)
						
							
//						 index = ChatConsole.this.getLinkIndex(iptarget);
//						 System.out.println("\n\n\n-----------------!"+index);
						 
						 //index 可能变化   试用hashmap改变存储形式！！！！！！！！
						 
//						 System.out.println("test for chat[]: "+chat[index]);
						// System.out.println(chat.size());

						 //possible method:liner probe of hashing;
						 //require that once one person get downline,turn this chat[] to null right now 

						 
						 chat.remove(iptarget);
						 StartChat newchat = new StartChat(iplocal,iptarget);
						 chat.put(iptarget,newchat);
						 new Thread(chat.get(iptarget)).start();
					
//						else{
//							index = ChatConsole.this.getLinkIndex(iptarget,iplocal);
//							chat[index] = new StartChat(iptarget,iplocal,false);
//						}
						
						//StartChat chat = new StartChat(iplocal,iptarget,true);
						//new Thread(chat).start();
						
						//send voice talk request from iplocal to iptarget!
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
				
			}
		});
		
		pNorth.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				showPopup(arg0);
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				showPopup(arg0);
			}
			
		});
		
		jmiInfoEdit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.out.println("Start to Edit!");

				edit = new InfoEdit();
				new Thread(edit).start();
				
			}
		});
	
	}
	
	
	
	
	protected void showPopup(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.isPopupTrigger()){
			jpMenu.show(arg0.getComponent(),arg0.getX(),arg0.getY());
		}
		
	}



	public friendInfo getFriend(friendInfo []fri,String Ip){
		int index = -1;
		for(int i=0;i<fri.length;i++){
			if(fri[i].getIp().equals(Ip)){
				index = i; 
				break;
			}
		}
		if(index==-1)
			return null;
		else
			return fri[index];
	}
	
	class fleshtimer extends TimerTask{

		@Override
		public void run() {
		
			
			boolean isChanged = false; //judge whether the friend online has changed?
		
			iptable = father.getip();
			linkstate = new linkState[iptable.length];
			
			System.out.println(iptable.length);
			for(int j=0;j<iptable.length;j++){
				linkstate[j] = new linkState();
				
				linkstate[j].srcip = new String(iptable[0]);
				linkstate[j].destip = new String(iptable[j]);
				linkstate[j].isConnect = false;
				linkstate[j].isReady =false;	
			}
			
			//check whether the friendInfo changes or not?
			if(friend.length!=iptable.length){
				isChanged = true;
			}
			else{
				for(int i=0;i<iptable.length;i++){
					if(!friend[i].getIp().equals(friendNew[i].getIp())
							||!friend[i].getName().equals(friendNew[i].getName())
							||!friend[i].getProfile().equals(friendNew[i].getProfile())
							){
						System.out.println("new name"+friendNew[i].getName());
						isChanged = true;
					}
				}
			}
			
			if(isChanged){
				friend = new friendInfo[iptable.length];
				
				friendInfo fri;
				for(int i=0;i<iptable.length;i++){
					
					fri = getFriend(friendNew,iptable[i]);
					if(fri==null){
						//this ip addr isn't exist before,
						//and need to initialize it;
						friend[i]=new friendInfo();
						
						friend[i].setIp(iptable[i]);
						friend[i].setName("Owner of "+iptable[i]);
						friend[i].setProfile("Hello,everyone!I'm "+friend[i].getName());
						friend[i].setHeadPicture(new ImageIcon(getClass().getResource("image/"+i+".jpg")));
					}
					else{
						friend[i]=new friendInfo();
						
						friend[i].setIp(fri.getIp());
						friend[i].setName(fri.getName());
						friend[i].setProfile(fri.getProfile());
						friend[i].setHeadPicture(fri.getHeadPicture());
					}
					
				}
				friendNew = new friendInfo[iptable.length];
				for(int i=0;i<iptable.length;i++){
					friendNew[i]=new friendInfo();
					
					friendNew[i].setIp(friend[i].getIp());
					friendNew[i].setName(friend[i].getName());
					friendNew[i].setProfile(friend[i].getProfile());
					friendNew[i].setHeadPicture(friend[i].getHeadPicture());
				}
				
				
				ChatConsole.this.fresh();
			}
		}
		
	}
	
	public void fresh(){
//		ImageIcon localIcon = friend[0].getHeadPicture();
//		
//		//How to adjust size?
//		localIcon.setImage(localIcon.getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT));
//				
//		JLabel jl = new JLabel(localIcon);
//		
//		
//		
//		String myInfo = "";
//		myInfo+="My IpAddr: \r\n"+friend[0].Ip+"\r\n";
//		myInfo+="My Name: \r\n"+friend[0].Name;
//		jtfMessage.setText(myInfo);
//				
//		String mySig = "";
//		mySig+="My Signature: \r\n"+friend[0].getProfile();
//		jtaSig.setText(mySig);
//
		String myInfo = new String("");
		myInfo+="My IpAddr: \r\n"+friend[0].Ip+"\r\n";
		myInfo+="My Name: \r\n"+friend[0].Name;
		jtfMessage.setText(myInfo);
		myInfo = new String("");
		
		myInfo="My Signature: \r\n"+friend[0].getProfile();
		jtaSig.setText(myInfo);
		
		
		ImageIcon[] icons = new ImageIcon[iptable.length]; 
		
		
		dftList.clear();
		for(int i=1;i<iptable.length;i++){
			icons[i] = friend[i].getHeadPicture();//
			icons[i].setImage(icons[i].getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT));//
			dftList.addElement(new Object[]{icons[i],friend[i].Name});
		}

		list.setModel(dftList);
		
		
//		pNorth.repaint();
//		pCenter.repaint();		
	}	

	public class InfoEdit implements Runnable{

		//private int id;
		
		private JLabel jlblName = new JLabel("Name");
		private JLabel jlblProfile = new JLabel("Signature");
		private JLabel jlblHeadPicture = new JLabel("HeadIcon");
		private JTextField jtfName = new JTextField();
		private JTextField jtfProfile = new JTextField();
		private JTextField jtfHeadPicture = new JTextField();
		private JTextArea jtaTips = new JTextArea();
		private JButton jbtBrowse = new JButton("Browse");
		private JButton jbtRefresh = new JButton("Refresh");
		
		private void windowopen(){
			JFileChooser fileChooser = new JFileChooser();
			if(fileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION)
			{
				java.io.File file = fileChooser.getSelectedFile();
				jtfHeadPicture.setText(file.getAbsolutePath());
				//System.out.println(file.getAbsolutePath());
				Scanner input;
				try {
					input = new Scanner(file);
				
				while (input.hasNext())
				{
					System.out.println(input.nextLine());
				}
				input.close();
				} catch (FileNotFoundException e) {
					// TODO 自动生成 catch 块
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println("No file selected");
			}
		}
		
		public InfoEdit(){
			
		}
		public void run() {
			// TODO Auto-generated method stub
			InitDialog();
		}
		public void InitDialog(){
			JFrame jf = new JFrame();
			
			JPanel p1 = new JPanel(new GridLayout(3,1));
			p1.add(jlblName);
			p1.add(jlblProfile);
			p1.add(jlblHeadPicture);
			
			JPanel p2 = new JPanel(new BorderLayout());
			p2.add(jtfHeadPicture,BorderLayout.CENTER);
			p2.add(jbtBrowse,BorderLayout.EAST);
			
			JPanel p3 = new JPanel(new GridLayout(3,1));
			p3.add(jtfName);
			p3.add(jtfProfile);
			p3.add(p2);
			
			JPanel p4 = new JPanel(new BorderLayout());
			p4.add(p1,BorderLayout.WEST);
			p4.add(p3,BorderLayout.CENTER);
			
			JPanel p5 = new JPanel(new BorderLayout());
			p5.add(jbtRefresh,BorderLayout.WEST);
			jtaTips.setText("Click the button left to refresh the personal information.");
			jtaTips.setLineWrap(true);
			jtaTips.setEditable(false);
			p5.add(jtaTips,BorderLayout.CENTER);
			
			jf.setLayout(new BorderLayout());
			jf.add(p4,BorderLayout.CENTER);
			jf.add(p5,BorderLayout.SOUTH);
			
			jf.setTitle("Edit for Personal Inforamtion");
			jf.setSize(300,150);
			
			jf.setVisible(true);
			listenToEdit();
			
			
			
		}
		void listenToEdit(){
			jbtBrowse.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt){
					System.out.println("Open the JFileChooser!");	
					windowopen();
				}
			});
			
			jbtRefresh.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt){
					
					if(!jtfName.getText().isEmpty())
						friendNew[0].setName(jtfName.getText());
					if(!jtfProfile.getText().isEmpty())
						friendNew[0].setProfile(jtfProfile.getText());
					if(!jtfHeadPicture.getText().isEmpty()){
						System.out.println(jtfHeadPicture.getText());
										friendNew[0].setHeadPicture(new ImageIcon(getClass().getResource(jtfHeadPicture.getText())));
					}	
					
					
		
					//timer check will flush the info of friend locally!
					

					
					for(int i=0;i<iptable.length;i++){
					//send!!
						if(iptable[i].equals(iplocal))
							continue;
						
						SoftData outdata = new SoftData();
						outdata.op = 2;
						outdata.destip = iptable[i];
						outdata.srcip =  iplocal;
						outdata.friendinfo= new friendInfo(friendNew[0]);
						
						ChatConsole.this.father.softsenddata(outdata);
					}
					//NOTE: 收到的包的destip已经被连接池改为回复的目的地址，即所收到包的源地址
				}
			});			
		}
		
	}
	
	public class StartChat implements Runnable{
		private String TargetIp;
		private String SourceIp;
//		private boolean isRequest;		
		
		private JFrame jf = new JFrame();
		private JTextField jtfSrc = new JTextField();
		private JTextField jtfDest = new JTextField();
		JTextArea jtaMsg = new JTextArea();
		JTextArea jtaInput = new JTextArea();
		private JButton jbtStart = new JButton();
		private JButton jbtDisconnect = new JButton();
		private JButton jbtSend = new JButton();
		int linkindex;
		
		
		
		public StartChat(String SourceIp,String TargetIp){
			this.SourceIp = SourceIp;
			this.TargetIp = TargetIp;
//			this.isRequest = isRequest;//is it the request sent from local to outside?
		}
		
		public void run(){
			//linkindex = ChatConsole.this.getLinkIndex(this.TargetIp);
			initDialog();
		}
			
		public void initDialog(){
			
			
			JPanel p1 = new JPanel();
			p1.setLayout(new BorderLayout());
			p1.add(new JLabel("From"),BorderLayout.WEST);
			jtfSrc.setText(this.SourceIp);
			jtfSrc.setEditable(false);
			p1.add(jtfSrc,BorderLayout.CENTER);
			
			JPanel p2 = new JPanel();
			p2.setLayout(new BorderLayout());
			p2.add(new JLabel("To"),BorderLayout.WEST);
			jtfDest.setText(this.TargetIp);
			jtfDest.setEditable(false);
			p2.add(jtfDest,BorderLayout.CENTER);
			
			JPanel p3 = new JPanel();
			p3.setLayout(new GridLayout(1,2));
			
			jbtStart.setText("VoiceSend");
			jbtDisconnect.setText("Terminate");
			
			p3.add(jbtStart);
			p3.add(jbtDisconnect);
			
			JPanel p = new JPanel();
			p.setLayout(new BorderLayout());
			p.add(p1,BorderLayout.NORTH);
			p.add(p2,BorderLayout.CENTER);
			p.add(p3,BorderLayout.SOUTH);
			
			
			JPanel pdown = new JPanel(new BorderLayout());
			
			jf.setLayout(new BorderLayout());
			jf.add(p,BorderLayout.NORTH);
			
			pdown.add(new JScrollPane(jtaInput),BorderLayout.CENTER);
			jbtSend.setText("TextSend");
			pdown.add(jbtSend,BorderLayout.EAST);
			jtaMsg.setEditable(false);
			jf.add(new JScrollPane(jtaMsg),BorderLayout.CENTER);
			jf.add(pdown,BorderLayout.SOUTH);
			
			jf.setTitle("Chat with "+this.TargetIp);
			jf.setSize(500,300);
			//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jf.setVisible(true);
			
			jf.setLocationRelativeTo(null);
			listen();
		}
		
		public void listen(){
			jtaInput.addKeyListener(new KeyListener(){
				@Override
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER&&e.isControlDown()){
						jtaInput.append("\n");
					}		
					if (e.getKeyCode() == KeyEvent.VK_ENTER){
						
						SoftData output = new SoftData();
						output.op = 4;
						output.srcip = StartChat.this.SourceIp;
						output.destip = StartChat.this.TargetIp;
						output.Msg = StartChat.this.jtaInput.getText();
						
						jtaInput.setText(null);
						jtaMsg.append("\n"+friend[0].Name+" "+new Date()+"\n   "+output.Msg);
										
						ChatConsole.this.father.softsenddata(output);
					}				
				}

				@Override
				public void keyPressed(KeyEvent e) {				
				}

				@Override
				public void keyTyped(KeyEvent e) {				
				}
			});
			
			
			jbtSend.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0){
					
					SoftData output = new SoftData();
					output.op = 4;
					output.srcip = StartChat.this.SourceIp;
					output.destip = StartChat.this.TargetIp;
					output.Msg = StartChat.this.jtaInput.getText();
					
					jtaInput.setText(null);
					jtaMsg.append("\n"+friend[0].Name+" "+new Date()+"\n   "+output.Msg);
									
					ChatConsole.this.father.softsenddata(output);
					
				}
			});
			
			
			jbtStart.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0){
					//send the request to the target
					SoftData outdata = new SoftData();
					outdata.op = 1;
					outdata.srcip = iplocal;
					outdata.destip = StartChat.this.TargetIp;
					outdata.isReply = false;
					
					ChatConsole.this.father.softsenddata(outdata);
				}
			});
			jbtDisconnect.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0){
					SoftData outdata = new SoftData();
					outdata.op = 1;
					outdata.srcip = iplocal;
					outdata.destip = StartChat.this.TargetIp;
					
					outdata.isReply = true;
					outdata.accept = false;
					
					if(ChatConsole.this.audio.yymode == 1)
						ChatConsole.this.audio.setmode(0);
					
					ChatConsole.this.father.softsenddata(outdata);
				}
			});
			
			
		}

	}
	
	public class MsgRecv implements Runnable{
		
		
		boolean isReady;
		boolean isConnect;
		public void run() {
			// TODO Auto-generated method stub
			
			//NOTE: 收到的包的destip已经被连接池改为回复的目的地址，即所收到包的源地址
			while( true )
			{
				System.out.println("+++++++++++++");
				SoftData data = ChatConsole.this.father.indata.get() ;//receive data packet;
				
				System.out.println("--------------\n------------");
				
				//here the data.destip is the dest of next send!!!
				
				System.out.println( "src  " + data.srcip + "  dst  " + data.destip) ;

				switch(data.op){
					case 1:	
						if(data.isReply){//reply
							if(data.accept){
							
								chat.get(data.destip).jtaMsg.append("The voice talk request has been accepted.\nPlease start speaking!\n");
								if(ChatConsole.this.audio.yymode!=1)
									ChatConsole.this.audio.setmode(1);
								ChatConsole.this.audio.capture(data.destip);
							}
							else
							{
								chat.get(data.destip).jtaMsg.append("The voice talk request has been Rejected.\n");
								ChatConsole.this.audio.setmode(0);
							}
						}
						else{//request
							
							System.out.println("here comes a voice request!!!! from "+data.destip);
							int n=JOptionPane.showConfirmDialog(null,"Here comes a voice talk request from "+data.destip+".Accept?","AcceptOrReject",JOptionPane.OK_CANCEL_OPTION);
							if(n == JOptionPane.OK_OPTION){
								
								SoftData outdata = new SoftData();
								outdata.op = 1;
								outdata.destip = data.destip;
								outdata.srcip  = iplocal;
								outdata.isReply = true;
								outdata.accept  = true;
								ChatConsole.this.father.softsenddata(outdata);
								
								audio.capture(data.destip);
								
								 chat.remove(data.destip);
								 StartChat newchat = new StartChat(iplocal,data.destip);
								 chat.put(data.destip,newchat);
								 new Thread(chat.get(data.destip)).start();
							}
							else{
								
								SoftData outdata = new SoftData();
								outdata.op = 1;
								outdata.destip = data.destip;
								outdata.srcip  = iplocal;
								outdata.isReply = true;
								outdata.accept  = false;
								ChatConsole.this.father.softsenddata(outdata);
								
							}
						}	
					break;
					case 2:
						System.out.println(getfriendNew(data.destip));
					    friendNew[getfriendNew(data.destip)].setName(data.friendinfo.getName());
					    friendNew[getfriendNew(data.destip)].setProfile(data.friendinfo.getProfile());
					    if(data.friendinfo.getHeadPicture()!=null)
					    	friendNew[getfriendNew(data.destip)].setHeadPicture(data.friendinfo.getHeadPicture());
					    System.out.println(getfriendNew(data.destip)+"+++++++++++++++++++++++++++++");
						
					break;
					case 3:				
						ChatConsole.this.audio.play((byte[]) data.sound);
					break;
					case 4:					
						chat.get(data.destip).jtaMsg.append("\n"+friend[getfriend(data.destip)].getName()+" "+new Date()+"\n   "+data.Msg);	
					break;
					default: 
						System.out.println("data.op error!");
				
				}
			}	
		}		
	}
	
	
	


	


	
	
}


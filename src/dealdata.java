import javax.swing.JOptionPane;

		void dealdata(Object data,String ipaddr){
			System.out.println("databag end  "+pool.getnum());
			
			if (data.equals("Please consent to communicate!"))
			{
				System.out.println("a yy request from "+ipaddr);
				if (yymode==0) {
					//�����Ի����Ƿ�ͬ��,�ϲ㷵��ֵ������������0
					int answer=JOptionPane.showConfirmDialog(null,ipaddr+" requests the connection. Receive it?");
					if (answer==JOptionPane.YES_OPTION) 
					{
						yymode=1;
						pool.senddata(ipaddr,"I agree!");
						audio.capture(ipaddr);
						return;
					}
					else pool.senddata(ipaddr, "I am busy!");
				}
				else{
					pool.senddata(ipaddr, "I am busy!");
				}
			}
			
			if (data.equals("I am busy!")){
				yymode=0;
				JOptionPane.showMessageDialog(null,ipaddr+" refuses.");
				//�����Ի���
			}
			
			if (data.equals("I agree!")){
				yymode=1;
				JOptionPane.showMessageDialog(null,ipaddr+" agrees.");
				audio.capture(ipaddr);
				return;
			}
			
			if (yymode==1){
				byte[] audioData=(byte[]) data;
				audio.play(audioData);
			}
		}
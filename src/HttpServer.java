import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class HttpServer implements Runnable {
    /**
     * 
     */
	
	public mainclass father = null;
    ServerSocket serverSocket;//������Socket
    
    /** */
    public static int PORT=8080;//��׼HTTP�˿�
    
    /**
     * ��ʼ������ Socket �߳�.
     */
    public HttpServer(mainclass mfather) {
    	
    	this.father = mfather;
    	
        try {
            serverSocket=new ServerSocket(PORT);
        } catch(Exception e) {
            System.out.println("�޷�����HTTP������:"+e.getLocalizedMessage());
        }
        if(serverSocket==null)  
			System.exit(1);//�޷���ʼ������
        new Thread(this).start();
        System.out.println("HTTP��������������,�˿�:"+PORT);
    }
    
    /**
     * ���з��������߳�, �����ͻ������󲢷�����Ӧ.
     */
    public void run() {
        while(true) {
            try {
                Socket client=null;//�ͻ�Socket
                client=serverSocket.accept();//�ͻ���(������ IE �������)�Ѿ����ӵ���ǰ������
                
                
                
                if(client!=null) {
                    System.out.println("���ӵ����������û�:"+client);
                    try {
                        // ��һ�׶�: ��������
                        BufferedReader in=new BufferedReader(new InputStreamReader(
                                client.getInputStream()));
                        
                        System.out.println("�ͻ��˷��͵�������Ϣ: ***************");
                        // ��ȡ��һ��, �����ַ
                        String line=in.readLine();
                        System.out.println(line);
                        String resource=line.substring(line.indexOf('/'),line.lastIndexOf('/')-5);
                        System.out.println("Resource"+resource);
                        //����������Դ�ĵ�ַ
                       
                        resource=URLDecoder.decode(resource, "UTF-8");//������ URL ��ַ
                        String method = new StringTokenizer(line).nextElement().toString();// ��ȡ���󷽷�, GET ���� POST
                        System.out.println("method "+method);
                      
                        // ��ȡ������������͹������������ͷ����Ϣ
                        while( (line = in.readLine()) != null) {
                            System.out.println(line);                           
                            if(line.equals("")) 
								break;
                        }
                        
                        // ��ʾ POST ���ύ������, �������λ����������岿��
                        if("POST".equalsIgnoreCase(method)) {
                            System.out.println(in.readLine());
                        }
                       
                        System.out.println("������Ϣ���� ***************");
                        System.out.println("�û��������Դ��:"+resource);
                        System.out.println("�����������: " + method);

                        // GIF ͼƬ�Ͷ�ȡһ����ʵ��ͼƬ���ݲ����ظ��ͻ���
             
          
                        
                       
                        if(resource.endsWith(".jpg")) {
                            fileService("images/gys.jpg", client);
                            closeSocket(client);
                            continue;
                        }
                        
                        // ���� JPG ��ʽ�ͱ��� 404
                        else if(resource.endsWith(".gif")) {
							PrintWriter out=new PrintWriter(client.getOutputStream(),true);
							out.println("HTTP/1.1 404 Not found");//����Ӧ����Ϣ,������Ӧ��
							out.println();// ���� HTTP Э��, ���н�����ͷ��Ϣ
							out.close();
							closeSocket(client);
							continue;
                        } else {
                        	
                        	PrintWriter out=new PrintWriter(client.getOutputStream(),true);
                            out.println("HTTP/1.0 200 OK");//����Ӧ����Ϣ,������Ӧ��
                            out.println("Content-Type:text/html;charset=GBK");
                            out.println();// ���� HTTP Э��, ���н�����ͷ��Ϣ
                            out.println("<center><h1>Cluster Management System</h1><center>");
                            out.println("");
                         
                            
                            String[] ip;
                            ip = father.getip();
                            
                            out.println("<center><table class='platforms'><tr><th>ID</th><th>IP</th><th>State</th></tr>");
                            for(int i=0;i<ip.length;i++)
                            	out.println("<tr class='c1'><td rowspan='1'>"+i+"</td><td rowspan='1'>"+ip[i]+"</td><td rowspan='1'>True</td>");
                            
                            
                            
                            
                            //out.println("<form method=post action='/'>POST �� <input name=username value=''> <input name=submit type=submit value=submit></form>");
                            out.close();

                            closeSocket(client);
                        }  
                        
                    } 
					catch(Exception e) {
                        System.out.println("HTTP����������:"+e.getLocalizedMessage());
                    }
                }
                //System.out.println(client+"���ӵ�HTTP������");//���������һ��,��������Ӧ�ٶȻ����
            } catch(Exception e) {
                System.out.println("HTTP����������:"+e.getLocalizedMessage());
            }
        }
    }
    
    /**
     * �رտͻ��� socket ����ӡһ��������Ϣ.
     * @param socket �ͻ��� socket.
     */
    void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println(socket + "�뿪��HTTP������");        
    }
    
    /**
     * ��ȡһ���ļ������ݲ����ظ��������.
     * @param fileName �ļ���
     * @param socket �ͻ��� socket.
     */
    void fileService(String fileName, Socket socket){         
        try
        {
        	OutputStream writer = new BufferedOutputStream(socket.getOutputStream());
        	PrintStream out = new PrintStream(writer);
        	
        	
			File file = new File(fileName);
			if (file.exists() && !file.isDirectory()) {
				out.println("HTTP/1.0 200 OK");// ����Ӧ����Ϣ,������Ӧ��
				out.println("Content-Type:text/html");
				//out.println("Content-Length:" + file.length());// ���������ֽ���
				out.println();// ���� HTTP Э��, ���н�����ͷ��Ϣ

				InputStream reader = new FileInputStream(file);
				
				byte[] buff = new byte[reader.available()];

				
				writer.write(buff, 0, reader.read(buff));
					
				
				try {
					reader.close();
				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				out.flush();

			}
        }
        catch(Exception e)
        {
            System.out.println("�����ļ�ʱ����:" + e.getLocalizedMessage());
        }
    }
    
    /**
     * ��ӡ��;˵��.
     */
    private static void usage() {
        System.out.println("Usage: java HTTPServer <port> Default port is 8080.");
    }
    
    
  
//    public static void main(String[] args) {
//        try {
//            if(args.length != 1) {
//                usage();
//            } else if(args.length == 1) {
//                PORT = Integer.parseInt(args[0]);
//            }
//        } catch (Exception ex) {
//            System.err.println("Invalid port arguments. It must be a integer that greater than 0");
//        }
//        
//        new HttpServer();
//    }
    
}
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class HttpServer implements Runnable {
    /**
     * 
     */
	
	public mainclass father = null;
    ServerSocket serverSocket;//服务器Socket
    
    /** */
    public static int PORT=8080;//标准HTTP端口
    
    /**
     * 开始服务器 Socket 线程.
     */
    public HttpServer(mainclass mfather) {
    	
    	this.father = mfather;
    	
        try {
            serverSocket=new ServerSocket(PORT);
        } catch(Exception e) {
            System.out.println("无法启动HTTP服务器:"+e.getLocalizedMessage());
        }
        if(serverSocket==null)  
			System.exit(1);//无法开始服务器
        new Thread(this).start();
        System.out.println("HTTP服务器正在运行,端口:"+PORT);
    }
    
    /**
     * 运行服务器主线程, 监听客户端请求并返回响应.
     */
    public void run() {
        while(true) {
            try {
                Socket client=null;//客户Socket
                client=serverSocket.accept();//客户机(这里是 IE 等浏览器)已经连接到当前服务器
                
                
                
                if(client!=null) {
                    System.out.println("连接到服务器的用户:"+client);
                    try {
                        // 第一阶段: 打开输入流
                        BufferedReader in=new BufferedReader(new InputStreamReader(
                                client.getInputStream()));
                        
                        System.out.println("客户端发送的请求信息: ***************");
                        // 读取第一行, 请求地址
                        String line=in.readLine();
                        System.out.println(line);
                        String resource=line.substring(line.indexOf('/'),line.lastIndexOf('/')-5);
                        System.out.println("Resource"+resource);
                        //获得请求的资源的地址
                       
                        resource=URLDecoder.decode(resource, "UTF-8");//反编码 URL 地址
                        String method = new StringTokenizer(line).nextElement().toString();// 获取请求方法, GET 或者 POST
                        System.out.println("method "+method);
                      
                        // 读取所有浏览器发送过来的请求参数头部信息
                        while( (line = in.readLine()) != null) {
                            System.out.println(line);                           
                            if(line.equals("")) 
								break;
                        }
                        
                        // 显示 POST 表单提交的内容, 这个内容位于请求的主体部分
                        if("POST".equalsIgnoreCase(method)) {
                            System.out.println(in.readLine());
                        }
                       
                        System.out.println("请求信息结束 ***************");
                        System.out.println("用户请求的资源是:"+resource);
                        System.out.println("请求的类型是: " + method);

                        // GIF 图片就读取一个真实的图片数据并返回给客户端
             
          
                        
                       
                        if(resource.endsWith(".jpg")) {
                            fileService("images/gys.jpg", client);
                            closeSocket(client);
                            continue;
                        }
                        
                        // 请求 JPG 格式就报错 404
                        else if(resource.endsWith(".gif")) {
							PrintWriter out=new PrintWriter(client.getOutputStream(),true);
							out.println("HTTP/1.1 404 Not found");//返回应答消息,并结束应答
							out.println();// 根据 HTTP 协议, 空行将结束头信息
							out.close();
							closeSocket(client);
							continue;
                        } else {
                        	
                        	PrintWriter out=new PrintWriter(client.getOutputStream(),true);
                            out.println("HTTP/1.0 200 OK");//返回应答消息,并结束应答
                            out.println("Content-Type:text/html;charset=GBK");
                            out.println();// 根据 HTTP 协议, 空行将结束头信息
                            out.println("<center><h1>Cluster Management System</h1><center>");
                            out.println("");
                         
                            
                            String[] ip;
                            ip = father.getip();
                            
                            out.println("<center><table class='platforms'><tr><th>ID</th><th>IP</th><th>State</th></tr>");
                            for(int i=0;i<ip.length;i++)
                            	out.println("<tr class='c1'><td rowspan='1'>"+i+"</td><td rowspan='1'>"+ip[i]+"</td><td rowspan='1'>True</td>");
                            
                            
                            
                            
                            //out.println("<form method=post action='/'>POST 表单 <input name=username value=''> <input name=submit type=submit value=submit></form>");
                            out.close();

                            closeSocket(client);
                        }  
                        
                    } 
					catch(Exception e) {
                        System.out.println("HTTP服务器错误:"+e.getLocalizedMessage());
                    }
                }
                //System.out.println(client+"连接到HTTP服务器");//如果加入这一句,服务器响应速度会很慢
            } catch(Exception e) {
                System.out.println("HTTP服务器错误:"+e.getLocalizedMessage());
            }
        }
    }
    
    /**
     * 关闭客户端 socket 并打印一条调试信息.
     * @param socket 客户端 socket.
     */
    void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println(socket + "离开了HTTP服务器");        
    }
    
    /**
     * 读取一个文件的内容并返回给浏览器端.
     * @param fileName 文件名
     * @param socket 客户端 socket.
     */
    void fileService(String fileName, Socket socket){         
        try
        {
        	OutputStream writer = new BufferedOutputStream(socket.getOutputStream());
        	PrintStream out = new PrintStream(writer);
        	
        	
			File file = new File(fileName);
			if (file.exists() && !file.isDirectory()) {
				out.println("HTTP/1.0 200 OK");// 返回应答消息,并结束应答
				out.println("Content-Type:text/html");
				//out.println("Content-Length:" + file.length());// 返回内容字节数
				out.println();// 根据 HTTP 协议, 空行将结束头信息

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
            System.out.println("传送文件时出错:" + e.getLocalizedMessage());
        }
    }
    
    /**
     * 打印用途说明.
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
import javax.swing.ImageIcon;

public class friendInfo implements java.io.Serializable{
	
	String Name;
	String Ip;
	String Profile;
	ImageIcon headPicture;
	
	public friendInfo(){
		this.Name = "";
		this.Ip = "";
		this.Profile = "";
		this.headPicture = null;
	}
	
	public friendInfo(friendInfo fri){
		this.Name = fri.Name ;
		this.Ip = fri.Ip;
		this.Profile = fri.Profile;
		this.headPicture = fri.headPicture;
	}
	
	public friendInfo(String Name,String Ip,String Profile,ImageIcon headPicture){
		this.Name = Name ;
		this.Ip = Ip;
		this.Profile = Profile;
		this.headPicture = headPicture;
	}
	
	public String getName(){
		return this.Name;
	}
	
	public String getIp(){
		return this.Ip;
	}
	
	public String getProfile(){
		return this.Profile;
	}
	
	public ImageIcon getHeadPicture(){
		return this.headPicture;
	}
	
	public void setName(String Name){
		this.Name = Name;
	}
	
	public void setIp(String Ip){
		this.Ip = Ip;
	}
	
	public void setProfile(String Profile){
		this.Profile = Profile;
	}
	
	public void setHeadPicture(ImageIcon headPicture){
		this.headPicture = headPicture;
	}
}
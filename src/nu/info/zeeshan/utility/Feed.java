package nu.info.zeeshan.utility;

public class Feed {
	private String title;
	private String time;
	private String description;
	private String link;
	private String image;
	
	public void setTitle(String data){
		title=data;
	}
	
	

	public void setTime(String data){
		time=data;
	}
	
	public void setDesc(String data){
		description=data;
	}
	
	public void setLink(String data){
		link=data;
	}
	
	public void setImage(String url){
		image=url;
	}
	
	public String getTitle(){
		return title;
	}
	
	
	public String getTime(){
		return time;
	}
	
	public String getDesc(){
		return description;
	}
	
	public String getLink(){
		return link;
	}
	
	public String getImage(){
		return image;
	}
}

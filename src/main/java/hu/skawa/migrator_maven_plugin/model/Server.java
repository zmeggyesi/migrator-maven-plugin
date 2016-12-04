package hu.skawa.migrator_maven_plugin.model;

public class Server {
	private String url;
	private String user;
	private String pass;
	private String id;

	public Server(String url, String user, String pass, String id) {
		super();
		this.url = url;
		this.user = user;
		this.pass = pass;
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	public String getPass() {
		return pass;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setPass(String pass) {
		this.pass = pass;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String toBazelServer() {
		StringBuilder sb = new StringBuilder("maven_server(\n");
		sb.append("\tname = \"");
		sb.append(id);
		sb.append("\",");

		sb.append("\n\turl = \"");
		sb.append(url);
		sb.append("\",");

		sb.append("\n)");
		return sb.toString();
	}
}

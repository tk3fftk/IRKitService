package jp.kobe_u.cs27;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * IRKitを操作するためのサービス
 * @author tktk 
 *
 */

@Path("/")
public class IRKitService {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON})
	@Path("/learn/id")
	public Response learn(@PathParam("id") String id) {
		return Response.ok().entity("aa").build();
	}
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON})
	@Path("/send/id")
	public Response send(@PathParam("id") String id) {
		return Response.ok().entity("aa").build();
	}

	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	@Path("/hello")
	public String hello() {
		return "(´・ω・`)hi" + new Date();
	}

}

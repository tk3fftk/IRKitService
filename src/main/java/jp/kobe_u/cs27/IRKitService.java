package jp.kobe_u.cs27;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * IRKitを操作するためのサービス
 * 
 * @author tktk
 *
 */

@Path("/")
public class IRKitService {

	private IRKitController controller = new IRKitController();

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/learn/{id}")
	public Response learn(@PathParam("id") String id) {
		String result = controller.learn(id);
		return Response.ok().entity(result).build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/send/{id}")
	public Response send(@PathParam("id") String id) {
		boolean result = controller.send(id);
		String json = "{\"result\":" + result + "}";
		return Response.ok().entity(json).build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/list")
	public Response list() {
		String json = controller.list();
		return Response.ok().entity(json).build();
	}

	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	@Path("/hello")
	public String hello() {
		return "(´・ω・`)hi" + new Date();
	}

}

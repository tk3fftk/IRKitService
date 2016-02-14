package com.example;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	@Path("/tasu")
	public String plus(@QueryParam("a") int a, @QueryParam("b") int b) {
		return String.valueOf(a + b);
	}

	public int minus(int a, int b) {
		return a - b;
	}

	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	@Path("/hello")
	public String hello() {
		return "(´・ω・`)hi" + new Date();
	}

}

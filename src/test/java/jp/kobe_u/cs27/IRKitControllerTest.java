package jp.kobe_u.cs27;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class IRKitControllerTest {
	
	private IRKitController c;

	@Before
	public void setUp() {
		c = new IRKitController();
	}

	/**
	 * 成功
	 */
	@Test
	public void sendTest01(){
		c.send("tv_volume_up");
		c.send("tv_volume_down");
	}
	
	/**
	 * 失敗 存在しないid
	 */
	@Test
	public void sendTest02(){
		assertFalse(c.send("hoge"));
	}
	

}

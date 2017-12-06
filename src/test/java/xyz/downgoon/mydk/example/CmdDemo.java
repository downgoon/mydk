package xyz.downgoon.mydk.example;

import xyz.downgoon.mydk.process.Cmd;

public class CmdDemo {

	public static void main(String[] args) throws Exception {
		Cmd.exec("ls", m -> {
			System.out.println(m);
		});

		new Cmd("ls").onSucc(m -> {
			System.out.println("succ: " + m);

		}).onFail((e, m) -> {
			System.out.println("fail: " + m);
			System.out.println("fail: " + e.getMessage());

		}).exec();
	}

}

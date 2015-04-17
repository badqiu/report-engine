package com.github.reportengine.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class ResponseUtil {

	public static void writeString(HttpServletResponse resp, String string)
			throws IOException {
		PrintWriter writer = resp.getWriter();
		writer.print(string);
		writer.flush();
	}

}

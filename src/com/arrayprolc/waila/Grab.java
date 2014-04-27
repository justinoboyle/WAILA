package com.arrayprolc.waila;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Grab {

	public static String web(String webPage) {
		String text = null;
		try {
			URL url = new URL(webPage);
			URLConnection urlConnection = url.openConnection();
			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			int numRead;
			char[] charArray = new char[1024];
			StringBuffer sb = new StringBuffer();
			while ((numRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numRead);
			}
			text = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return text;

	}

}

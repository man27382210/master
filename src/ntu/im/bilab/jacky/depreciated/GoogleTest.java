package ntu.im.bilab.jacky.depreciated;

import java.io.IOException;

import org.jsoup.Jsoup;

public class GoogleTest {

	public static void main(String[] args) {
		String url = "http://www.google.com/patents/US20130030987";
		try {
	    Jsoup.connect(url).timeout(0).userAgent("Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52").get();
    } catch (IOException e) {
	    e.printStackTrace();
    }

	}

}

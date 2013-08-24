package core.dbmodel;

import org.javalite.instrumentation.Instrumentation;

public class MakeInstrumentationUtil {
	public static void make() {
		try {
			Instrumentation instrumentation = new Instrumentation();
			instrumentation.setOutputDirectory(ClassLoader.getSystemResource(".").getPath());
			System.out.println(ClassLoader.getSystemResource(".")
			    .getPath());
			instrumentation.instrument();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
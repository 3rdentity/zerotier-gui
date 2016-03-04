package it.condarelli.zerotier.gui.pages;

import java.util.HashMap;
import java.util.Map;

public interface FxAdapter {
	public static final String parent = ".parent";
	
	public <T extends Object> T adapt(String what, Class<T> clazz);
	
	static Map<Class<?>, Object> services = new HashMap<>();
	public static <T extends Object> T service(Class<T> clazz) {
		Object o = services.get(clazz);
		if (clazz.isInstance(o))
			return clazz.cast(o);
		return null;
	}
	public static boolean provide(Object o) {
		return provide(o, o.getClass());
	}
	public static boolean provide(Object o, Class<?> clazz) {
		boolean overwrite = (services.containsKey(clazz) && clazz.isInstance(services.get(clazz)));
		services.put(clazz, o);
		return overwrite;
	}
}
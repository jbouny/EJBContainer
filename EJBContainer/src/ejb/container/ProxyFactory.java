package ejb.container;

import java.lang.reflect.Proxy;

public class ProxyFactory {
	
	public Object createWithBean(Object inBean) {

		Class<?> ejbClass = inBean.getClass();
		Class<?>[] interfaces = ejbClass.getInterfaces();
		BeanHandler handler = new BeanHandler( inBean );
		
		Object proxy = Proxy.newProxyInstance( ejbClass.getClassLoader(), interfaces, handler );
		
		return proxy;
	}
}

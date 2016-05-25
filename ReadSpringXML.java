/**
*	自定义实现模拟SpringIOC
*
*/
public class TestSpringExample {
	private static Map<String, Object> beanFactory = new HashMap<String, Object>();

	@Test
	public void readXML() throws Exception {
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(this.getClass().getClassLoader()
				.getResourceAsStream("beans.xml"));
		Element root = document.getRootElement();
		Namespace ns = Namespace
				.getNamespace("http://www.springframework.org/schema/beans");
		List<Element> listBean = root.getChildren("bean", ns);
		for (Element em : listBean) {
			Object c = Class.forName(em.getAttributeValue("class"))
					.newInstance();
			String id = em.getAttributeValue("id");
			List<Element> propList = em.getChildren("property", ns);
			if (propList != null) {
				for (Element pro : propList) {
					String name = pro.getAttributeValue("name");
					String value = pro.getAttributeValue("value");
					String ref = pro.getAttributeValue("ref");

					Field filed = c.getClass().getDeclaredField(name);
					boolean isaccess = false;
					if (!filed.isAccessible()) {
						filed.setAccessible(true);
						isaccess = true;
					}
					setValueForFiled(filed, name, value, ref, c);
					if (isaccess) {
						filed.setAccessible(false);
					}
				}
			}

			beanFactory.put(id, c);
		}

		PersonService personService = (PersonService) beanFactory
				.get("personService");
		personService.say();

	}

	private static void setValueForFiled(Field field, Object name,
			String value, String ref, Object obj) {
		Class<?> c = field.getType();
		try {
			if (value != null) {
				if (c == int.class || c == Integer.class) {
					field.set(obj, Integer.valueOf(value));
				} else if (c == String.class) {
					field.set(obj, value);
				} else if (c == Float.class || c == float.class) {
					field.set(obj, Float.valueOf(value));
				} else if (c == Double.class || c == double.class) {
					field.set(obj, Double.valueOf(value));
				} else if (c == Byte.class || c == byte.class) {
					field.set(obj, Byte.valueOf(value));
				} else if (c == Boolean.class || c == boolean.class) {
					field.set(obj, Boolean.valueOf(value));
				} else if (Long.class == c || c == long.class) {
					field.set(obj, Long.valueOf(value));
				}
			} else if (ref != null) {
				field.set(obj, beanFactory.get(ref));
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//获取字段上直接声明的注解，实现注解注入
		Annotation[] annotationList = field.getDeclaredAnnotations();
		if (annotationList.length > 0) {
			for (int i = 0; i < annotationList.length; i++) {
				if (annotationList[i].annotationType() == MyClass.class) {
					MyClass type = field.getAnnotation(MyClass.class);
					try {
						field.set(obj, type.value());
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

}

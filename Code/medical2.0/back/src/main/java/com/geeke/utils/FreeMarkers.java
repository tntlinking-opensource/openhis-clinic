package com.geeke.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * FreeMarkers工具类
 * @author lys
 * @version 2018-01-15
 */
public class FreeMarkers {
	
	public static String renderString(String templateString, Map<String, ?> model) {
		return renderString(templateString, model, null);
	}

	public static String renderString(String templateString, Map<String, ?> model, String pathPrefix) {
		try {
			StringWriter result = new StringWriter();
			Configuration cf = new Configuration(Configuration.VERSION_2_3_28);
			if(pathPrefix != null) {
				cf.setClassForTemplateLoading(FreeMarkers.class, pathPrefix);
			}
			
			Template t = new Template("name", new StringReader(templateString), cf);
			t.process(model, result);
			return result.toString();
		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
	}	

	public static String renderTemplate(Template template, Object model) {
		try {
			StringWriter result = new StringWriter();
			template.process(model, result);
			return result.toString();
		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
	}

	public static Configuration buildConfiguration(String directory) throws IOException {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
		Resource path = new DefaultResourceLoader().getResource(directory);
		cfg.setDirectoryForTemplateLoading(path.getFile());
		return cfg;
	}
	
	public static void main(String[] args) throws IOException {
//		// renderString
//		Map<String, String> model = com.google.common.collect.Maps.newHashMap();
//		model.put("userName", "calvin");
//		String result = FreeMarkers.renderString("hello ${userName}", model);
//		System.out.println(result);
//		// renderTemplate
//		Configuration cfg = FreeMarkers.buildConfiguration("classpath:/");
//		Template template = cfg.getTemplate("testTemplate.ftl");
//		String result2 = FreeMarkers.renderTemplate(template, model);
//		System.out.println(result2);
		
//		Map<String, String> model = com.google.common.collect.Maps.newHashMap();
//		model.put("userName", "calvin");
//		String result = FreeMarkers.renderString("hello ${userName} ${r'${userName}'}", model);
//		System.out.println(result);
	}
	
}

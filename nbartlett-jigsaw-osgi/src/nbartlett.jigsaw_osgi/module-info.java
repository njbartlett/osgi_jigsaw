module nbartlett.jigsaw_osgi {
	
	// Standard OSGi packages... NB versions will come from versions.properties
	exports org.osgi.framework;
	exports org.osgi.framework.dto;
	exports org.osgi.framework.hooks.bundle;
	exports org.osgi.framework.hooks.resolver;
	exports org.osgi.framework.hooks.service;
	exports org.osgi.framework.hooks.weaving;
	exports org.osgi.framework.launch;
	exports org.osgi.framework.namespace;
	exports org.osgi.framework.startlevel;
	exports org.osgi.framework.startlevel.dto;
	exports org.osgi.framework.wiring;
	exports org.osgi.framework.wiring.dto;
	exports org.osgi.resource;
	exports org.osgi.resource.dto;
	exports org.osgi.service.packageadmin;
	exports org.osgi.service.startlevel;
	exports org.osgi.service.url;
	exports org.osgi.service.resolver;
	exports org.osgi.util.tracker;
	exports org.osgi.dto;

	// Felix-specific packages
	exports org.apache.felix.framework;
 }
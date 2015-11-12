package nbartlett.osgi_jigsaw;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Module;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.felix.framework.FrameworkFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.wiring.BundleRevision;

public class Main {
	
	private static final boolean TRACE = true;
	private static final boolean LOOSE = true;

	public static void main(String[] args) throws Exception {
		trace("================================================================================%n");
		trace("Loading OSGi...%n");
		FrameworkFactory frameworkFactory = new FrameworkFactory();
		
		// Work out the jmodules readable to OSGi
		Module myModule = Main.class.getModule();
		Set<Module> modules = findReadableModules(myModule);
		trace("Available Modules = %s%n", modules);

		// Calculate system bundle exports
		Properties versionsProps = new Properties();
		try (InputStream versionsPropsInput = Main.class.getResourceAsStream("versions.properties")) {
			versionsProps.load(versionsPropsInput);
		}
		List<String> systemExports = new ArrayList<>();
		for (Module module : modules) {
			systemExports.addAll(calculateExports(module, versionsProps));
		}
		
		// Calculate the system capabilities
		List<String> systemCapabilites = new ArrayList<>();
		for (Module module : Main.class.getModule().getLayer().modules()) {
			systemCapabilites.add(String.format("jmodule; jmodule=%s", module.getName()));
		}
		// TODO: should include all versions up to 1.9
		systemCapabilites.add("osgi.ee; osgi.ee=JavaSE; version:Version=1.9");
		systemCapabilites.add("osgi.ee; osgi.ee=JavaSE; version:Version=1.8");

		// Configure the framework
		Map<String,String> configMap = new HashMap<>();
		configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES, formatList(systemExports));
		configMap.put(Constants.FRAMEWORK_SYSTEMCAPABILITIES_EXTRA, formatList(systemCapabilites));
		Framework framework = frameworkFactory.newFramework(configMap);
		
		// Start the framework
		framework.init();
		framework.start();
		trace("OSGi started%n");
		BundleContext context = framework.getBundleContext();
		
		try {
			// Install all bundles under the 'bundles' directory
			List<Bundle> bundles = new ArrayList<>();
			for (File bundleFile : new File("bundles").listFiles()) {
				if (bundleFile.getName().endsWith(".jar")) {
					try {
						trace("Installing %s%n", bundleFile.getAbsolutePath());
						bundles.add(context.installBundle(bundleFile.toURI().toString()));
					} catch (BundleException e) {
						System.err.printf("Error installing from %s (skipping): %s%n", bundleFile, e.getMessage());
					}
				}
			}
			
			// Start all installed bundles except fragments
			int startedCount = 0;
			for (Bundle bundle : bundles) {
				BundleRevision revision = bundle.adapt(BundleRevision.class);
				if ((revision.getTypes() & BundleRevision.TYPE_FRAGMENT) == 0) {
					try {
						trace("Starting %s%n", bundle.getSymbolicName());
						bundle.start();
						startedCount ++;
					} catch (BundleException e) {
						System.err.printf("Error starting bundle %s%n", bundle.getSymbolicName());
						e.printStackTrace();
					}
				} else {
					trace("Skipping fragment: %s%n", bundle.getSymbolicName());
				}
			}
			
			if (startedCount > 0) {
				trace("Started %d bundles, waiting for shutdown...%n", startedCount);
				// Wait for shutdown from within the framework
				framework.waitForStop(0);
			} else {
				trace("No bundles were started. Exiting main.%n");
			}
		} catch (InterruptedException e) {
			trace("Interrupted while waiting for OSGi to shutdown!%n");
		} finally {
			trace("OSGi stopped%n");
			System.exit(0);
		}
	}

	private static Set<Module> findReadableModules(Module module) {
		Set<Module> allModules = module.getLayer().modules();
		if (LOOSE) return allModules;
		
		// Only return the modules that we require statically.
		Set<Module> readableModules = new HashSet<>();
		for (Module m : allModules) {
			if (module.canRead(m))
				readableModules.add(m);
		}
		
		return readableModules;
	}

	private static List<String> calculateExports(Module module, Properties versionsProps) {
		String[] packageNames = module.getPackages();
		List<String> exports = new ArrayList<>(packageNames.length);
		for (String packageName : packageNames) {
			if (packageName.startsWith("java.")) continue;
			if (module.isExported(packageName)) {
				String version = versionsProps.getProperty(packageName, "0");
				exports.add(String.format("%s;version=%s", packageName, version));
			}
		}
		return exports;
	}

	private static String formatList(Collection<? extends Object> list) {
		StringBuilder b = new StringBuilder();
		for (Iterator<? extends Object> iter = list.iterator(); iter.hasNext(); ) {
			b.append(iter.next().toString());
			if (iter.hasNext()) b.append(",");
		}
		return b.toString();
	}
	
	private static void trace(String format, Object... args) {
		if (TRACE) System.out.printf(format, args);
	}
	
}

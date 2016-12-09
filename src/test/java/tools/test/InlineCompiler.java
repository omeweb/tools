package tools.test;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class InlineCompiler {
	public static void main(String[] args) throws Throwable {
		StringBuilder sb = new StringBuilder(64);
		sb.append("package tools.test.compile;\n");
		sb.append("public class HelloWorld implements tools.test.InlineCompiler.DoStuff {\n");
		sb.append("    public void doStuff() {\n");
		sb.append("        System.out.println(\"print:\" + tools.Convert.toString(\"Hello world\"));\n");
		sb.append("    }\n");
		sb.append("}\n");

		File helloWorldJava = new File("./tools/test/compile/HelloWorld.java");// 注意这里的路径，和包名对应 2014-11-21 by 六三
		if (helloWorldJava.getParentFile().exists() || helloWorldJava.getParentFile().mkdirs()) {
			// 写文件
			Writer writer = null;
			try {
				writer = new FileWriter(helloWorldJava);
				writer.write(sb.toString());
				writer.flush();
			} finally {
				try {
					writer.close();
				} catch (Exception e) {
					;
				}
			}
		}

		/** Compilation Requirements *********************************************************************************************/
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

		// This sets up the class path that the compiler will use.
		// I've added the .jar file that contains the DoStuff interface within in it...
		List<String> optionList = new ArrayList<String>();
		optionList.add("-classpath");
		optionList.add(System.getProperty("java.class.path") + ";");

		Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(Arrays
				.asList(helloWorldJava));
				
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null,
				compilationUnit);
		
		/** Compilation Requirements **/
		
		if (task.call()) {
			/** Load and execute **/
			System.out.println("Yipe");
			// Create a new custom class loader, pointing to the directory that contains the compiled
			// classes, this should point to the top of the package structure!
			URLClassLoader classLoader = new URLClassLoader(new URL[] { new File("./").toURI().toURL() });
			// Load the class from the classloader by name....
			Class<?> loadedClass = classLoader.loadClass("tools.test.compile.HelloWorld");
			// Create a new instance...
			Object obj = loadedClass.newInstance();
			// Santity check
			if (obj instanceof DoStuff) {
				// Cast to the DoStuff interface
				DoStuff stuffToDo = (DoStuff) obj;
				// Run it baby
				stuffToDo.doStuff();
			}
			/** Load and execute **/
		} else {
			for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
				System.out.format("Error on line %d in %s%n", diagnostic.getLineNumber(), diagnostic.getSource()
						.toUri());
			}
		}
		fileManager.close();
	}

	public static interface DoStuff {
		public void doStuff();
	}
}

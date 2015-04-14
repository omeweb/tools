package tools.test.compile;
public class HelloWorld implements tools.test.InlineCompiler.DoStuff {
    public void doStuff() {
        System.out.println("print:" + tools.Convert.toString("Hello world"));
    }
}

import eu.duckee.duckletwebserver.DuckletController;

public class TestClass {

    public static void main(String[] args) {
        DuckletController controller = DuckletController.createController(8080, 20);
        controller.addRoute(new TestRoute());
        controller.addRoute(new Test2Route());
        controller.startController();
    }

}

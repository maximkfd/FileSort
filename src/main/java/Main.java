public class Main {

    public static void main(final String[] args) throws Exception {
        //uncomment ot generate file of 1 gb
//        Generator generator = new Generator();
//        generator.generate("input.txt", 500);

        final Sorter sorter = new Sorter();
        sorter.sort("input.txt");

    }

}

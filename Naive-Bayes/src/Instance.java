public class Instance {

    String[] features;
    String classValue;

    Instance(String[] values) {
        features = new String[values.length - 1];
        System.arraycopy(values, 0, this.features, 0, values.length - 1);
        this.classValue = values[values.length - 1];
    }

}
